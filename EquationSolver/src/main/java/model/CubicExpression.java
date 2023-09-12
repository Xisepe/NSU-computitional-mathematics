package model;

import config.Config;
import config.Value;

import java.util.Objects;

@Config
public class CubicExpression {
    @Value
    private double a;
    @Value
    private double b;
    @Value
    private double c;

    public double getValue(double x) {
        return x * x * x + a * x * x + b * x + c;
    }

    public double a() {
        return a;
    }

    public double b() {
        return b;
    }

    public double c() {
        return c;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CubicExpression) obj;
        return Double.doubleToLongBits(this.a) == Double.doubleToLongBits(that.a) &&
                Double.doubleToLongBits(this.b) == Double.doubleToLongBits(that.b) &&
                Double.doubleToLongBits(this.c) == Double.doubleToLongBits(that.c);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b, c);
    }

    @Override
    public String toString() {
        return String.format("x^3 + %.3fx^2 + %.3fx + %.3f", a, b, c);
    }

}
