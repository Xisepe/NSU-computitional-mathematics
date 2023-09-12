package service;

import config.Config;
import config.Value;
import model.CubicExpression;
import model.EquationRoot;
import model.Range;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Config("solver")
public class CubicEquationSolver {
    @Value
    private double epsilon;
    @Value
    private double delta;

    public List<EquationRoot> solve(CubicExpression expression) {
        if (expression == null) throw new IllegalArgumentException("expression must not be null");
        var answer = new ArrayList<EquationRoot>();
        double discriminant = calculateDiscriminant(expression);
        if (discriminant < 0) {
            findRootIfDiscriminantIsLowerZero(expression, answer);
        } else if (discriminant == 0) {
            findRootsIfDiscriminantIsZero(expression, answer, discriminant);
        } else {
            findRootsIfDiscriminantIsGreaterThanZero(expression, answer, discriminant);
        }
        answer.sort(Comparator.comparingDouble(EquationRoot::value));
        return answer;
    }

    private void findRootsIfDiscriminantIsGreaterThanZero(CubicExpression expression, ArrayList<EquationRoot> answer, double discriminant) {
        var args = calculateArgs(discriminant, expression);
        double x1 = args.get(0);
        double x2 = args.get(1);
        double val1 = expression.getValue(x1);
        double val2 = expression.getValue(x2);
        final var x1x2Range = new Range(x1, x2);
        final var negInfX1Range = new Range(Double.NEGATIVE_INFINITY, x1);
        final var x2PosInfRange = new Range(x2, Double.POSITIVE_INFINITY);

        if (val1 > epsilon && val2 < -epsilon) {
            answer.add(findRoot(negInfX1Range, expression));
            answer.add(findRoot(x1x2Range, expression));
            answer.add(findRoot(x2PosInfRange, expression));
        } else if (val2 > epsilon) {
            answer.add(findRoot(negInfX1Range, expression));
        } else if (val1 < -epsilon) {
            answer.add(findRoot(x2PosInfRange, expression));
        } else if (Math.abs(val1) < epsilon && val2 < -epsilon) {
            answer.add(new EquationRoot(x1, 2));
            answer.add(findRoot(x2PosInfRange, expression));
        } else if (val1 > epsilon && Math.abs(val2) < epsilon) {
            answer.add(findRoot(negInfX1Range, expression));
            answer.add(new EquationRoot(x2, 2));
        }
    }

    private void findRootsIfDiscriminantIsZero(CubicExpression expression, ArrayList<EquationRoot> answer, double discriminant) {
        var args = calculateArgs(discriminant, expression);
        double x1 = args.get(0);
        double val1 = expression.getValue(x1);
        if (Math.abs(val1) < epsilon) {
            answer.add(new EquationRoot(x1, 3));
        } else if (val1 > epsilon) {
            answer.add(findRoot(new Range(Double.NEGATIVE_INFINITY, x1), expression));
        } else {
            answer.add(findRoot(new Range(x1, Double.POSITIVE_INFINITY), expression));
        }
    }

    private void findRootIfDiscriminantIsLowerZero(CubicExpression expression, ArrayList<EquationRoot> answer) {
        double value = expression.getValue(0);
        Range searchRange;
        Range rootRange;
        if (value > epsilon)
            searchRange = new Range(Double.NEGATIVE_INFINITY, 0);
        else
            searchRange = new Range(0, Double.POSITIVE_INFINITY);
        rootRange = findRootRange(searchRange, expression);
        answer.add(findRoot(rootRange, expression));
    }

    /**
     * Root degree is always one in cases where this function is called
     */
    private EquationRoot findRoot(Range range, CubicExpression expression) {
        return new EquationRoot(
                findRootAtRange(
                        findRootRange(range, expression),
                        expression
                ),
                1
        );
    }

    public Range findRootRange(Range range, CubicExpression expression) {
        if (range == null) throw new IllegalArgumentException("search range must not be null");
        if (Double.isFinite(range.getLeftBound()) && Double.isFinite(range.getRightBound()))
            return range;
        if (Double.isInfinite(range.getLeftBound()) && Double.isInfinite(range.getRightBound()))
            throw new IllegalArgumentException("Range to find root cannot be from negative infinity to positive infinity");
        double iterDelta;
        if (Double.isInfinite(range.getLeftBound()) && Double.isFinite(range.getRightBound())) {
            iterDelta = range.getRightBound();
            while (expression.getValue(iterDelta) >= -epsilon) {
                iterDelta -= delta;
            }
            return new Range(iterDelta, iterDelta + delta);
        } else if (Double.isInfinite(range.getRightBound()) && Double.isFinite(range.getLeftBound())) {
            iterDelta = range.getLeftBound();
            while (expression.getValue(iterDelta) <= epsilon) {
                iterDelta += delta;
            }
            return new Range(iterDelta - delta, iterDelta);
        }
        return null;
    }

    private double findRootAtRange(Range range, CubicExpression expression) {
        if (expression.getValue(range.getLeftBound()) > expression.getValue(range.getRightBound())) {
            range.swap();
        }
        double mid;
        double root = Double.NaN;
        while (Double.isNaN(root)) {
            mid = (range.getLeftBound() + range.getRightBound()) / 2;
            var tmp = expression.getValue(mid);
            if (Math.abs(tmp) < epsilon)
                root = mid;
            else if (tmp < -epsilon)
                range.setLeftBound(mid);
            else if (tmp > epsilon)
                range.setRightBound(mid);
        }
        return root;
    }

    private double calculateDiscriminant(CubicExpression expression) {
        return expression.a() * expression.a() - 3 * expression.b();
    }

    private List<Double> calculateArgs(double discriminant, CubicExpression expression) {
        if (discriminant < 0)
            throw new IllegalArgumentException("discriminant must be non negative");
        var res = new ArrayList<Double>();
        double dr = Math.sqrt(discriminant);
        res.add((-expression.a() - dr) / 3);
        res.add((-expression.a() + dr) / 3);
        Collections.sort(res);
        return res;
    }
}
