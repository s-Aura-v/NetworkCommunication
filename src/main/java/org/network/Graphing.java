package org.network;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.util.ArrayList;

public class Graphing {
    static void graph(ArrayList<Double> data) {
        XYSeries hashMapXY = new XYSeries("Temp");

        for (int i = 0; i < data.size(); i++) {
            hashMapXY.add(i, data.get(i));
        }

        XYSeriesCollection solutionsXY = new XYSeriesCollection(hashMapXY);
        JFreeChart hashMapLineChart = ChartFactory.createXYLineChart(
                "TCP-UDP Test",
                "Iteration",
                "Millisecond/Operation",
                solutionsXY,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

         ChartPanel chartPanel = new ChartPanel(hashMapLineChart);
         JFrame frame = new JFrame("Chart Example");
         frame.getContentPane().add(chartPanel);
         frame.pack();
         frame.setVisible(true);
    }
}
