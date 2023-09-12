import config.ConfigAnnotationProcessor;
import model.CubicExpression;
import service.CubicEquationSolver;
import ui.CubicChart;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Application {
    public static void main(String[] args) throws IOException {
        var exp = ConfigAnnotationProcessor.process(CubicExpression.class);
        var solver = ConfigAnnotationProcessor.process(CubicEquationSolver.class);
        var roots = new File("roots.txt");
        var fw = new FileWriter(roots);
        solver.solve(exp).forEach(r-> {
            try {
                fw.write(r + "");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        fw.flush();
        setUI(exp);
    }

    private static void setUI(CubicExpression exp) {
        JFrame frame = new JFrame("Cubic Function Plot");
        frame.add(new CubicChart(exp));
        frame.setContentPane(new CubicChart(exp));
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
