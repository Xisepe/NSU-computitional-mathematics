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
    val dataset = createDataset(10001, -1.0, 1.0)
    val chart = createChart(dataset)
    val chartPanel = ChartPanel(chart)
    chartPanel.isDomainZoomable = true
    chartPanel.isRangeZoomable = true
    return chartPanel
}

fun createDataset(plotDots: Int, left: Double, right: Double): XYSeriesCollection {
    val (xs,ys) = createDots(left, right, 5, ::abs)
    val f = xs.mapIndexed { index, doubles -> Spline.Builder.build(doubles, ys[index]) }.toTypedArray()
    f.onEach {
        println("err=${MathUtils.calcError(1000, left, right, it::getValue, ::abs)}") }

    val seriesX = xs.map { XYSeries("n=${it.size}") }

    val step = (right - left)/(plotDots - 1)
    var x = left
    repeat (plotDots) {
        seriesX.forEachIndexed { index, xySeries ->
            xySeries.add(x, f[index].getValue(x))
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

fun createDots(left: Double, right: Double, iters: Int, f: (Double) -> Double): Pair<List<DoubleArray>, List<DoubleArray>> {
    val dots = IntArray(iters) {
        (it + 1) * 10 + 1
    }

    val xs = dots.map { splitInSeq(it, left, right) }
    val ys = xs.map { it.map { e-> f(e) }.toDoubleArray()}
    return Pair(xs, ys)
}

fun splitInSeq(dots: Int, left: Double, right:Double): DoubleArray {
    val h = (right - left) / (dots - 1)
    return DoubleArray(dots) {
        left + h * it
    }
}
