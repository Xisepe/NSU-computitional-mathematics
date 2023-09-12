package ui;

import config.ConfigAnnotationProcessor;
import lombok.SneakyThrows;
import model.CubicExpression;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

public class CubicChart extends JPanel {
    private final CubicExpression expression;
    private final static ChartConfig CONFIG = ConfigAnnotationProcessor.process(ChartConfig.class);
    @SneakyThrows
    public CubicChart(CubicExpression expression) {
        this.expression = expression;
        setLayout(new BorderLayout());

        XYDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);

        add(chartPanel, BorderLayout.CENTER);

    }

    private XYDataset createDataset() {
        XYSeries series = new XYSeries(expression + "");
        for (double x = CONFIG.getLeftBound(); x <= CONFIG.getRightBound(); x += CONFIG.getStep()) {
            double y = expression.getValue(x);
            series.add(x, y);
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        return dataset;
    }
    private JFreeChart createChart(XYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYLineChart(
                expression + "",
                "X",
                "Y",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        XYPlot plot = chart.getXYPlot();
        // Customize the X-axis
        NumberAxis domainAxis = new NumberAxis("X");
        domainAxis.setAutoRangeIncludesZero(false);
        plot.setDomainAxis(domainAxis);

        // Customize the Y-axis
        NumberAxis rangeAxis = new NumberAxis("Y");
        rangeAxis.setAutoRangeIncludesZero(false);
        plot.setRangeAxis(rangeAxis);

        // Add X = 0 and Y = 0 lines
        ValueMarker xMarker = new ValueMarker(0.0);
        xMarker.setPaint(Color.BLACK); // Set line color
        plot.addDomainMarker(xMarker);

        ValueMarker yMarker = new ValueMarker(0.0);
        yMarker.setPaint(Color.BLACK); // Set line color
        plot.addRangeMarker(yMarker);

        return chart;
    }
}
