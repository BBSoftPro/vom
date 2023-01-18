package io.vom.core;

import io.vom.utils.Point;
import io.vom.utils.Size;

import java.time.Duration;

public interface Driver extends Searchable {
    String getPlatform();

    void slipFinger(Point from, Point to, Duration duration);

    Size getWindowSize();

    void removeFocus();

    void click(int x, int y);

    void scrollDown();

    void scrollDown(Duration duration, int length);

    void scrollDownTo(String text);

    void scrollDownTo(String text, Duration duration, int length);

    void scrollUp();

    void scrollUp(Duration duration, int length);

    void scrollUpTo(String text);

    void scrollUpTo(String text, Duration duration, int length);

    void scrollLeft();

    void scrollLeft(Duration duration, int length);

    void scrollLeftTo(String text);

    void scrollLeftTo(String text, Duration duration, int length);

    void scrollRight();

    void scrollRight(Duration duration, int length);

    void scrollRightTo(String text);

    void scrollRightTo(String text, Duration duration, int length);

    void scrollDownToEnd();

    void scrollUpToStart();

    void scrollLeftToStart();

    void scrollRightToEnd();
}
