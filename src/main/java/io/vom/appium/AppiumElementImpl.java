package io.vom.appium;

import io.vom.core.Driver;
import io.vom.core.Element;
import io.vom.exceptions.ElementNotFoundException;
import io.vom.utils.Point;
import io.vom.utils.Properties;
import io.vom.utils.Selector;
import io.vom.utils.Size;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

public class AppiumElementImpl implements Element {
    private final AppiumDriverImpl driver;
    private final WebElement webElement;

    public AppiumElementImpl(AppiumDriverImpl driver, WebElement webElement) {
        this.driver = driver;
        this.webElement = webElement;
    }

    @Override
    public Driver getDriver() {
        return driver;
    }

    @Override
    public void setText(String text) {
        click();
        webElement.sendKeys(text);
        removeFocus();
    }

    @Override
    public String getText() {
        return webElement.getText();
    }

    @Override
    public void clear() {
        click();
        webElement.clear();
        removeFocus();
    }

    @Override
    public void click() {
        webElement.click();
    }


    @Override
    public Size getSize() {
        var dim = webElement.getSize();
        return new Size(dim.getWidth(), dim.getHeight());
    }

    @Override
    public Point getPoint() {
        var loc = webElement.getLocation();
        return new Point(loc.getX(), loc.getY());
    }

    @Override
    public void removeFocus() {
        if (!isFocused()) return;

        var size = getSize();
        var point = getPoint();
        driver.click(point.getX() + (size.getWidth() / 2), point.getY() - 1);
    }

    @Override
    public boolean isFocused() {
        return Boolean.parseBoolean(getAttribute("focused"));
    }

    @Override
    public String getAttribute(String attr) {
        return webElement.getAttribute(attr);
    }

    @Override
    public void drag(Point point) {
        var duration = Integer.parseInt(Properties.getInstance().getProperty("drag_duration_in_millis", "100"));

        drag(point, Duration.ofMillis(duration));
    }

    @Override
    public Point getCenterPoint() {
        var size = getSize();
        var point = getPoint();

        int x = size.getWidth() / 2 + point.getX();
        int y = size.getHeight() / 2 + point.getY();

        return new Point(x, y);
    }

    @Override
    public byte[] takeScreenshot() {
        return webElement.getScreenshotAs(OutputType.BYTES);
    }

    @Override
    public List<Integer> getCenterRGBColor() {
        return getDriver().getCenterRGBColor(getCenterPoint());
    }

    @Override
    public void drag(@NonNull Point point, @NonNull Duration duration) {
        var size = getSize();
        var currentPoint = this.getPoint();
        var centerPoint = new Point(currentPoint.getX() + size.getWidth() / 2, currentPoint.getY() + size.getHeight() / 2);

        driver.slipFinger(centerPoint, point, duration);
    }

    public WebElement getAppiumElement() {
        return webElement;
    }

    @Override
    public Element findElement(@NonNull Selector selector) {
        return AppiumDriverImpl.findElement(driver, webElement, selector);
    }

    @Override
    public Element findElement(Selector selector, Duration waitUntil) {
        return AppiumDriverImpl.findElement(driver, webElement, selector, waitUntil);
    }

    @Override
    public Element findNullableElement(@NonNull Selector selector) {
        try {
            return findElement(selector, Duration.ZERO);
        } catch (ElementNotFoundException e) {
            return null;
        }
    }

    @Override
    public Element findNullableElement(Selector selector, Duration duration) {
        try {
            return findElement(selector, duration);
        } catch (ElementNotFoundException e) {
            return null;
        }
    }

    @Override
    public List<Element> findElements(@NonNull Selector selector) {
        return AppiumDriverImpl.findElements(driver, webElement, selector);
    }
}
