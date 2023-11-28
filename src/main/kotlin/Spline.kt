import java.util.*
import kotlin.math.abs

class Spline private constructor(
    private val a: DoubleArray,
    private val b: DoubleArray,
    private val c: DoubleArray,
    private val d: DoubleArray,
    private val x: DoubleArray
) {
    object Builder {
        /**
         * x = n + 1 dot
         * f = n + 1 dot
         * */
        fun build(x: DoubleArray, f: DoubleArray): Spline {
            val a = f.clone()
            //n size from 0 to n - 1
            val h = DoubleArray(f.size - 1) {
                x[it + 1] - x[it]
            }
            //n + 1 size 0 to n
            val c = getCoefficients(a, h).copyInto(DoubleArray(f.size) { 0.0 }, 1)
            //n size
            val d = DoubleArray(f.size - 1) {
                (c[it + 1] - c[it]) / (3 * h[it])
            }
            //n size
            val b = DoubleArray(f.size - 1) {
                (a[it + 1] - a[it]) / h[it] + (2 * c[it + 1] + c[it]) / 3 * h[it]
            }
            return Spline(a, b, c, d, x)
        }

        private fun getCoefficients(
            a: DoubleArray, //n+1 dots
            h: DoubleArray //n dots
        ): DoubleArray {
            val n = h.size
            val r = DoubleArray(n - 1) {
                3 * ((a[it + 2] - a[it + 1]) / h[it + 1] - (a[it + 1] - a[it]) / h[it])
            }
            val lowerDiag = DoubleArray(n - 2) { h[it + 1] }
            val mainDiag = DoubleArray(n - 1) { 2 * (h[it] + h[it + 1]) }
            val upperDiag = DoubleArray(n - 2) { h[it + 1] }
            return MathUtils.threeDiagMatrixSolver(
                a = lowerDiag,
                b = mainDiag,
                c = upperDiag,
                d = r
            )
        }
    }

    fun getValue(x: Double): Double {
        val ind = this.x.indexOfFirst { it > x }
        val diff = x - this.x[ind]
        return a[ind] + b[ind - 1] * diff + c[ind] * diff * diff + d[ind - 1] * diff * diff * diff
    }
}