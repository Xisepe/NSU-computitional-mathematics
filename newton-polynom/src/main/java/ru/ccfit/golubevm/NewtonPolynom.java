package ru.ccfit.golubevm;

public class NewtonPolynom {
    private final double[] xNodes;
    private final double[] nodeValues;
    private final double[] nodeCoef;

    public NewtonPolynom(double[] xNodes, double[] nodeValues) {
        if (xNodes.length != nodeValues.length) {
            throw new IllegalArgumentException("Wrong args dimension size. They must be equal.");
        }
        this.xNodes = xNodes;
        this.nodeValues = nodeValues;
        this.nodeCoef = new double[nodeValues.length];
        calculateNodeCoef();
    }

    public double[] getNodeCoef() {
        return nodeCoef;
    }
    public double getValue(double x) {
        double[] diff = new double[nodeCoef.length];
        diff[0] = 1;
        for (int i = 1; i < diff.length; i++) {
            diff[i] = diff[i-1] * (x - xNodes[i - 1]);
        }
        double res = 0;
        for (int i = 0; i < nodeCoef.length; i++) {
            res += diff[i] * nodeCoef[i];
        }
        return res;
    }

    private void calculateNodeCoef() {
        int n = nodeValues.length;
        nodeCoef[0] = nodeValues[0];
        for (int i = 1; i < n; i++) {
            nodeCoef[i] = getCoef(i + 1);
        }
    }


    private double getCoef(int n) {
        double res = 0.0;
        for (int i = 0; i < n; i++) {
            double divide = 1.0;
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    divide *= xNodes[i] - xNodes[j];
                }
            }
            res += nodeValues[i] / divide;
        }
        return res;
    }
}
