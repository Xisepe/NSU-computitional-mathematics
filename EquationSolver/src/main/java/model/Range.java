package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Range {
    private double leftBound;
    private double rightBound;

    public void swap() {
        var tmp = leftBound;
        leftBound = rightBound;
        rightBound = tmp;
    }
}
