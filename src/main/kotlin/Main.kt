import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.chart.JFreeChart
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.plot.XYPlot
import org.jfree.data.Range
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import java.awt.BorderLayout
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities
import kotlin.math.abs

fun main(args: Array<String>) {
    SwingUtilities.invokeLater {
        val frame = JFrame("Plots")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        val chartPanel = createChartPanel()
        frame.contentPane.add(chartPanel, BorderLayout.CENTER)

        frame.setSize(800, 600)
        frame.setLocationRelativeTo(null)
        frame.isVisible = true
    }
}

fun createChartPanel(): JPanel {
    val dataset = createDataset()
    val chart = createChart(dataset)
    val chartPanel = ChartPanel(chart)
    chartPanel.isDomainZoomable = true
    chartPanel.isRangeZoomable = true
    return chartPanel
}

fun createDataset(): XYSeriesCollection {
    val left = -1.0
    val len = 2.0

    val stepSizes = listOf(0.2, 0.1, 0.05)
    val seriesNames = listOf("n=11", "n=21", "n=41")

    val xs = stepSizes.map { buildArr(len, left, it) }
    val ys = xs.map { it.map { d -> abs(d) }.toDoubleArray() }

    val seriesX = seriesNames.map { XYSeries(it) }

    val step = 1.0 / 10000
    var x = -1.0
    while (x < 1.0) {
        seriesX.forEachIndexed { index, xySeries ->
            val newtonInterpolation = newtonInterpolation(xs[index], ys[index], x)
            xySeries.add(x, newtonInterpolation)
        }
        x += step
    }

    val dataset = XYSeriesCollection()
    seriesX.forEach { dataset.addSeries(it) }

    return dataset
}

fun createChart(dataset: XYSeriesCollection): JFreeChart {
    val chart = ChartFactory.createXYLineChart(
        "Plots", "x", "y",
        dataset
    )

    val plot = chart.plot as XYPlot
    configureAxis(plot.domainAxis as NumberAxis, Range(-1.0, 1.0))
    configureAxis(plot.rangeAxis as NumberAxis, Range(-5.0, 5.0))

    return chart
}

fun configureAxis(axis: NumberAxis, range: Range) {
    axis.range = range
    axis.standardTickUnits = NumberAxis.createStandardTickUnits()
}

fun buildArr(len: Double, left: Double, h: Double) =
    DoubleArray(getDots(len, h)) { i -> left + h * i }

fun getDots(len: Double, h: Double): Int = (len / h).toInt() + 1

fun dividedDifference(x: DoubleArray, y: DoubleArray, n: Int): Double {
    var res = 0.0
    for (i in 0 until n) {
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
    for (i in 0 until x.size - 1) {
        result += dividedDifference(x, y, i + 2) * x.foldIndexed(1.0) { index, acc, d ->
            if (index > i)
                acc
            else
                acc * (value - d)
        }
    }
    return result
}
