package io.vom.core;

import io.qameta.allure.Step;
import io.vom.utils.Point;
import io.vom.utils.Selector;
import io.vom.utils.Size;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class View<T extends View<T>> implements Searchable {

    private Context context;
    private Driver driver;
    @SuppressWarnings("unchecked")
    private final T _self = (T) this;

    public void prepare(Context context) {
        this.context = context;
        this.driver = context.getDriver();
    }

    public T job(Consumer<T> consumer) {
        consumer.accept(_self);

        return _self;
    }

    public T slipFinger(Point from, Point to, Duration duration) {
        driver.slipFinger(from, to, duration);

        return _self;
    }

    public Size getWindowSize() {
        return driver.getWindowSize();
    }

    public T removeFocus() {
        driver.removeFocus();

        return _self;
    }

    public T click(int x, int y) {
        driver.click(x, y);

        return _self;
    }

    public T click(Point point) {
        return click(point.getX(), point.getY());
    }

    public boolean isPresentText(String text) {
        return driver.isPresentText(text);
    }

    public <V extends View<V>> V click(Point point, Class<V> vClass) {
        click(point);

        return context.loadView(vClass);
    }


    public Context getContext() {
        return context;
    }

    public Locale getLocale() {
        return driver.getLocale();
    }

    @Override
    public Element findElement(Selector selector) {
        return context.getDriver().findElement(selector);
    }

    @Override
    public Element findElement(Selector selector, Duration waitUntil) {
        return context.getDriver().findElement(selector, waitUntil);
    }

    @Override
    public Element findNullableElement(Selector selector) {
        return context.getDriver().findNullableElement(selector);
    }

    @Override
    public Element findNullableElement(Selector selector, Duration duration) {
        return getContext().getDriver().findNullableElement(selector, duration);
    }

    @Override
    public List<Element> findElements(Selector selector) {
        return context.getDriver().findElements(selector);
    }

    public T scrollDown() {
        driver.scrollDown();

        return _self;
    }

    public T scrollDown(Duration duration, int length) {
        driver.scrollDown(duration, length);

        return _self;
    }

    public T scrollDownTo(String text) {
        driver.scrollDownTo(text);

        return _self;
    }

    public T scrollDownTo(String text, Duration duration, int length) {
        driver.scrollDownTo(text, duration, length);

        return _self;
    }

    public T scrollDownTo(Selector selector) {
        driver.scrollDownTo(selector);

        return _self;
    }

    public T scrollDownTo(Selector selector, Duration duration, int length) {
        driver.scrollDownTo(selector, duration, length);

        return _self;
    }

    public T scrollDownTo(Selector selector, Duration duration, int length, Selector scrollContainer) {
        driver.scrollDownTo(selector, duration, length, scrollContainer);

        return _self;
    }

    public T scrollDownTo(Selector selector, Selector scrollContainer) {
        driver.scrollDownTo(selector, scrollContainer);

        return _self;
    }

    public T scrollUp() {
        driver.scrollUp();

        return _self;
    }

    public T scrollUp(Duration duration, int length) {
        driver.scrollUp(duration, length);

        return _self;
    }

    public T scrollUpTo(String text) {
        driver.scrollUpTo(text);

        return _self;
    }

    public T scrollUpTo(String text, Duration duration, int length) {
        driver.scrollUpTo(text, duration, length);

        return _self;
    }

    public T scrollUpTo(Selector selector) {
        driver.scrollUpTo(selector);

        return _self;
    }

    public T scrollUpTo(Selector selector, Selector scrollContainer) {
        driver.scrollUpTo(selector, scrollContainer);

        return _self;
    }

    public T scrollUpTo(Selector selector, Duration duration, int length) {
        driver.scrollUpTo(selector, duration, length);

        return _self;
    }

    public T scrollUpTo(Selector selector, Duration duration, int length, Selector scrollContainer) {
        driver.scrollUpTo(selector, duration, length, scrollContainer);

        return _self;
    }


    public T scrollLeft() {
        driver.scrollLeft();

        return _self;
    }

    public T scrollLeft(Selector draggableselector) {
        driver.scrollLeft(draggableselector);

        return _self;
    }

    public T delay(int second) {
        try {
            Thread.sleep(second * 1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return _self;
    }

    @Step
    public T delayStep(int second) {
        try {
            Thread.sleep(second * 1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return _self;
    }

    public T delay(int second, boolean reportStep) {
        if (reportStep) {
            return delay(second);
        } else {
            return delayStep(second);
        }
    }


    public T scrollLeft(Duration duration, int length) {
        driver.scrollLeft(duration, length);

        return _self;
    }

    public T scrollLeftTo(String text) {
        driver.scrollLeftTo(text);

        return _self;
    }

    public T scrollLeftTo(String text, Duration duration, int length) {
        driver.scrollLeftTo(text, duration, length);

        return _self;
    }

    public T scrollRight() {
        driver.scrollRight();

        return _self;
    }

    public T scrollRight(Duration duration, int length) {
        driver.scrollRight(duration, length);

        return _self;
    }

    public T scrollRightTo(String text) {
        driver.scrollRightTo(text);

        return _self;
    }

    public T scrollRightTo(String text, Duration duration, int length) {
        driver.scrollRightTo(text, duration, length);

        return _self;
    }

    public T scrollUpToStart() {
        driver.scrollUpToStart();

        return _self;
    }

    public T scrollDownToEnd() {
        driver.scrollDownToEnd();

        return _self;
    }

    public T scrollLeftToStart() {
        driver.scrollLeftToStart();

        return _self;
    }

    public T scrollLeftToStart(Selector selector) {
        driver.scrollLeftToStart(selector);

        return _self;
    }

    public T scrollRightToEnd() {
        driver.scrollRightToEnd();

        return _self;
    }

    public byte[] takeScreenshot() {
        return driver.takeScreenshot();
    }

    public void close() {
        driver.close();
    }

    public void quit() {
        driver.quit();
    }

    public <P extends View<P>> P back(@NonNull Class<P> klass) {
        driver.back();
        return getContext().loadView(klass);
    }

    public View<?> back() {
        driver.back();

        return _self;
    }

    public String getPageSource() {
        return driver.getPageSource();
    }
}
