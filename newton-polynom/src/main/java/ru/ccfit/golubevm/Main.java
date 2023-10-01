package ru.ccfit.golubevm;

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
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.DoubleStream;

public class Main {
    public static void main(String[] args) {
        var dataset = createDataset();
        var chart = createChart(dataset);
        var chartPanel = new ChartPanel(chart);

        var content = new JPanel(new BorderLayout());
        content.add(chartPanel, BorderLayout.CENTER);

        JFrame frame = new JFrame("polynoms");
        frame.setContentPane(content);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    private static XYDataset createDataset() {
        Function<Double, Double> f = Math::abs;
        double left = -1;
        double right = 1;
        var dataset = new XYSeriesCollection();
        dataset.addSeries(getData(left, right, 1 / 5.0, f));
        dataset.addSeries(getData(left, right, 1 / 20.0, f));
        dataset.addSeries(getData(left, right, 1 / 10.0, f));
        return dataset;
    }

    private static XYSeries getData(double left, double right, double step, Function<Double, Double> f) {
        int nodes = (int) ((right - left) / step) + 1;
        var s = new XYSeries("Nodes: " + nodes);
        var xi = DoubleStream
                .iterate(left, v -> v + step)
                .limit(nodes)
                .toArray();
        var fi = Arrays.stream(xi).map(f::apply).toArray();
        var poly = new NewtonPolynom(xi, fi);
        var rfi = Arrays.stream(xi).map(poly::getValue).toArray();
        for (int i = 0; i < xi.length; i++) {
            s.add(xi[i], rfi[i]);
        }
        return s;
    }

    private static JFreeChart createChart(XYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Newton Polynom",
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
