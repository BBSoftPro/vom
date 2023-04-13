package io.vom.core;

import io.vom.utils.Point;
import io.vom.utils.Selector;
import io.vom.utils.Size;

import java.time.Duration;
import java.util.Locale;

public interface Driver extends Searchable {
    void prepare(Context context);

    String getPlatform();

    void slipFinger(Point from, Point to, Duration duration);

    Size getWindowSize();

    void removeFocus();

    void click(int x, int y);

    void scrollDown();

    void scrollDown(Duration duration, int length);

    void scrollDown(Duration duration, int length, Selector scrollContainer);

    void scrollDownTo(String text);

    void scrollDownTo(String text, Duration duration, int length);

    void scrollDownTo(String text, Duration duration, int length, Selector scrollContainer);

    void scrollDownTo(Selector selector);

    void scrollDownTo(Selector selector, Duration duration, int length);

    void scrollDownTo(Selector selector, Duration duration, int length, Selector scrollContainer);

    void scrollUp();

    void scrollUp(Duration duration, int length);

    void scrollUp(Duration duration, int length, Selector scrollContainer);

    void scrollUpTo(String text);

    void scrollUpTo(String text, Duration duration, int length);

    void scrollUpTo(String text, Duration duration, int length, Selector selector);

    void scrollUpTo(Selector selector);

    void scrollUpTo(Selector selector, Duration duration, int length);

    void scrollUpTo(Selector selector, Duration duration, int length, Selector scrollContainer);

    void scrollLeft();

    void scrollLeft(Duration duration, int length);

    void scrollLeft(Duration duration, int length, Selector scrollContainer);

    void scrollLeftTo(String text);

    void scrollLeftTo(String text, Duration duration, int length);

    void scrollLeftTo(String text, Duration duration, int length, Selector scrollContainer);

    void scrollRight();

    void scrollRight(Duration duration, int length);

    void scrollRight(Duration duration, int length, Selector scrollContainer);

    void scrollRightTo(String text);

    void scrollRightTo(String text, Duration duration, int length);

    void scrollRightTo(String text, Duration duration, int length, Selector scrollContainer);

    void scrollDownToEnd();

    void scrollUpToStart();

    void scrollLeftToStart();

    void scrollRightToEnd();

    boolean isPresentText(String text);

    String getPageSource();

    byte[] takeScreenshot();

    void back();

    void quit();

    void close();

    Locale getLocale();

    Object getCenterColor(Selector selector);

    Object getCenterColor(Point point);
}
