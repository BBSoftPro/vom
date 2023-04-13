package io.vom.core;

import io.vom.utils.Point;
import io.vom.utils.Size;

import java.time.Duration;

public interface Element extends Searchable {

    Driver getDriver();

    void setText(String text);

    String getText();

    void clear();

    void click();

    Size getSize();

    Point getPoint();

    void removeFocus();

    boolean isFocused();

    String getAttribute(String attr);

    void drag(Point point, Duration duration);

    void drag(Point point);

    Point getCenterPoint();

    byte[] takeScreenshot();

    Object getCenterRGBColor();

    Object getAverageColor();
}
