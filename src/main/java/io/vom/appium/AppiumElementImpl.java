package io.vom.appium;

import io.vom.core.Driver;
import io.vom.core.Element;
import io.vom.core.View;
import io.vom.exceptions.ElementNotFoundException;
import io.vom.utils.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static io.vom.utils.ReflectionUtils.createPageObject;

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
            return createPageObject(this.getDriver().getContext(), klass);
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
        try {
            byte[] screenshotBytes = webElement.getScreenshotAs(OutputType.BYTES);
            return ImageIO.read(new ByteArrayInputStream(screenshotBytes));
        } catch (Exception e) {
            throw new ElementNotFoundException("can't take screenshot");
        }
    }

    @Override
    public Object getAverageColor() {
        var image = getImage();

        int x = 0;
        int y = 0;

        int times = 20;

        double w = ((double) image.getWidth() / times);
        double h = ((double) image.getHeight() / times);
        List<Object> rgbColorsList = new ArrayList<>();
        for (int i = 1; i < (times - 1); i++) {
            x += (int) w;
            for (int j = 1; j < (times - 1); j++) {
                y += (int) h;
                int clr = image.getRGB(x, y);
                String rbgColor = getRBG(clr);
                rgbColorsList.add(rbgColor);
            }
            y = 0;
        }
        return CollectionUtils.getAverageDuplicateUniqFromObjectList(rgbColorsList);
    }

    @Override
    public Object getCenterColor() {
        String scrBase64 = webElement.getScreenshotAs(OutputType.BASE64);
        BufferedImage image;
        try {
            byte[] byteArray = Base64.getDecoder().decode(scrBase64);
            image = ImageIO.read(new ByteArrayInputStream(byteArray));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int clr = image.getRGB((image.getWidth() / 2), image.getHeight() / 2);
        return getRBG(clr);
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

    static private String getRBG(int clr) {
        int red = (clr & 0x00ff0000) >> 16;
        int green = (clr & 0x0000ff00) >> 8;
        int blue = clr & 0x000000ff;
        return String.join(
                ",",
                String.valueOf(red),
                String.valueOf(green),
                String.valueOf(blue));
    }
}
