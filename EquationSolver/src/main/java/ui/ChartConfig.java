package ui;

import config.Config;
import config.Value;
import lombok.Getter;
import lombok.Setter;

@Config("chart")
@Getter
@Setter
public class ChartConfig {
    @Value
    private double leftBound;
    @Value
    private double rightBound;
    @Value
    private Double step;
}
