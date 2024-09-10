package io.vom.appium;

import io.vom.core.Driver;
import io.vom.core.Element;
import io.vom.core.View;
import io.vom.exceptions.ElementNotFoundException;
import io.vom.utils.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
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
    public void longPress() {
        Actions actions = new Actions(this.driver.getAppiumDriver());
        actions.clickAndHold(webElement)
                .pause(Duration.ofSeconds(2))  // Duration of the long press
                .release()
                .perform();
    }

    @Override
    public <P extends View<P>> P click(Class<P> klass) {
        webElement.click();
        try {
            return klass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create an instance of " + klass.getName(), e);
        }
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
    public boolean isEnabled() {
        return webElement.isEnabled();
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
    public BufferedImage getImage() {
        File screenshot = ((TakesScreenshot) driver.getAppiumDriver()).getScreenshotAs(OutputType.FILE);
        Dimension size = webElement.getSize();
        org.openqa.selenium.Point point = webElement.getLocation();

        FileImageInputStream inputStream = null;
        try {
            inputStream = new FileImageInputStream(screenshot);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BufferedImage fullScreenshot = null;
        try {
            fullScreenshot = ImageIO.read(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fullScreenshot.getSubimage(
                point.getX(),
                point.getY(),
                size.getWidth(),
                size.getHeight()
        );
    }

    @Override
    public Object getAverageColor() {
        Size size = getSize();
        Point point = getPoint();

        int x = (point.getX() + 5);
        int y = (point.getY() + 5);

        int times = 20;
        int mWidth = (size.getWidth() / times);
        int mHeight = (size.getHeight() / times);
        int c = times - 1;

        List<Object> rgbColorsList = new ArrayList<>();
        for (int i = 0; i < c; i++) {
            x = x + mWidth;
            y = y + mHeight;
            rgbColorsList.add(getCenterRGBColor());
        }

        return CollectionUtils.getAverageDuplicateUniqFromObjectList(rgbColorsList);
    }

    @Override
    public Object getCenterRGBColor() {
        return getDriver().getCenterColor(getCenterPoint());
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
