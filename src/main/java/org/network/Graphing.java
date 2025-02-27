package org.network;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.ChartUtils;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Graphing {
    static void graph(ArrayList<Double> data, String title) {
        XYSeries hashMapXY = new XYSeries("Temp");

        for (int i = 0; i < data.size(); i++) {
            hashMapXY.add(i, data.get(i));
        }

        XYSeriesCollection solutionsXY = new XYSeriesCollection(hashMapXY);
        JFreeChart hashMapLineChart = ChartFactory.createXYLineChart(
                title,
                "Iteration",
                "Millisecond/Operation",
                solutionsXY,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        try {
            File graph = new File("src/main/resources/static/" + "[" + Helpers.msgSize + "]" + title + ".png");
            ChartUtils.saveChartAsPNG(graph, hashMapLineChart, 800, 600);
        } catch (IOException e) {
            System.err.println("Error saving chart: " + e.getMessage());
        }
    }
}
