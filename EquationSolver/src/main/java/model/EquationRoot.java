package model;

public record EquationRoot(
        double value,
        int degree
) {
    @Override
    public String toString() {
        return "value=" + value + ", " + "degree=" + degree + "\n";
    }
}
