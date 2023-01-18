package io.vom.appium;

import io.vom.core.Driver;
import io.vom.core.Element;
import io.vom.core.Selector;
import io.vom.utils.Point;
import io.vom.utils.Size;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

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

    public WebElement getAppiumElement() {
        return webElement;
    }

    @Override
    public Element findElement(Selector selector) {
        return new AppiumElementImpl(driver, webElement.findElement(AppiumDriverImpl.bySelector(selector)));
    }

    @Override
    public List<Element> findElements(Selector selector) {
        return webElement.findElements(AppiumDriverImpl.bySelector(selector))
                .stream()
                .map((e) -> new AppiumElementImpl(driver, e))
                .collect(Collectors.toList());
    }
}
