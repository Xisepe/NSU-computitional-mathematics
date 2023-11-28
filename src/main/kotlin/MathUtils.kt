import kotlin.math.abs

object MathUtils {
    fun newtonsIntegral(steps: Int, a: Double, b: Double, f: (Double) -> Double): Double {
        val h = (b - a) / steps
        var res = 0.0
        var x = a

        repeat(steps) {
            val fx0 = f(x)
            val fx1 = f(x + h)
            val fxMid = f(x + h / 2)

            res += h / 6 * (fx0 + 4 * fxMid + fx1)
            x += h
        }

        return res
    }
    fun calcError(steps: Int, a: Double, b: Double, aprox: (Double) -> Double, real:(Double) -> Double): Double =
        MathUtils.newtonsIntegral(
            steps = 10000,
            a = a,
            b = b,
            f = {e:Double -> abs(aprox.invoke(e) - real.invoke(e)) }
        )

    private fun dividedDifference(x: DoubleArray, y: DoubleArray, n: Int): Double {
        var res = 0.0
        for (i in 0..<n) {
            val tmp = x.foldIndexed(1.0) { index, acc, d ->
                if (index == i || index >= n)
                    acc
                else acc * (x[i] - d)
            }
            res += y[i] / tmp
        }
        return res
    }

    fun newtonInterpolation(x: DoubleArray, y: DoubleArray, value: Double): Double {
        var result = y[0]
        for (i in 0..<x.size - 1) {
            result += dividedDifference(x, y, i + 2) * x.foldIndexed(1.0) { index, acc, d ->
                if (index > i)
                    acc
                else
                    acc * (value - d)
            }
        }
        return result
    }

    fun threeDiagMatrixSolver(a: DoubleArray, b: DoubleArray, c: DoubleArray, d: DoubleArray): DoubleArray {
        val n = b.size
        val cPrime = DoubleArray(n - 1)
        val dPrime = DoubleArray(n)

        // Forward elimination
        cPrime[0] = c[0] / b[0]
        dPrime[0] = d[0] / b[0]

        for (i in 1..<n - 1) {
            val m = 1.0 / (b[i] - a[i - 1] * cPrime[i - 1])
            cPrime[i] = c[i] * m
            dPrime[i] = (d[i] - a[i - 1] * dPrime[i - 1]) * m
        }

        // Backward substitution
        val x = DoubleArray(n)
        x[n - 1] = (d[n - 1] - a[n - 2] * dPrime[n - 2]) / (b[n - 1] - a[n - 2] * cPrime[n - 2])

        for (i in n - 2 downTo 0) {
            x[i] = dPrime[i] - cPrime[i] * x[i + 1]
        }

        return x
    }
}