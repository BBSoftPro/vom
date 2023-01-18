package io.vom.appium;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.vom.core.Driver;
import io.vom.core.Element;
import io.vom.core.Selector;
import io.vom.exceptions.PlatformNotFoundException;
import io.vom.utils.FileUtil;
import io.vom.utils.Point;
import io.vom.utils.Properties;
import io.vom.utils.Size;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class AppiumDriverImpl implements Driver {

    AppiumDriver appiumDriver;

    private final Duration scrollDuration = Duration.ofMillis(Integer.parseInt(Properties.getInstance().getProperty("scroll_default_duration_in_millis", "100")));
    private final int scrollLength = Integer.parseInt(Properties.getInstance().getProperty("scroll_length", "300"));

    public AppiumDriverImpl(URL remoteAddress, Capabilities desiredCapabilities) {
        appiumDriver = new AppiumDriver(remoteAddress, desiredCapabilities);
    }

    public AppiumDriverImpl() {
        var prop = Properties.getInstance();

        try {
            var url = new URL(prop.getProperty("appium_url"));
            var reader = new FileReader(FileUtil.getFullPath(prop.getProperty("appium_caps_json_file")));

            Gson gson = new Gson();
            Type type = new TypeToken<List<Map<String, Object>>>() {
            }.getType();
            List<Map<String, Object>> listPlatforms = gson.fromJson(reader, type);

            var platform = prop.getProperty("appium_platform");
            Objects.requireNonNull(platform, "appium_platform is not prepared on the properties file");
            var map = listPlatforms
                    .stream()
                    .filter((list) -> platform.equalsIgnoreCase((String) list.get("platformName")))
                    .findAny()
                    .orElseThrow(() -> new PlatformNotFoundException("Platform: '" + platform + "' was not found on appium json file"));

            var caps = new DesiredCapabilities(map);
            appiumDriver = new AppiumDriver(url, caps);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Element findElement(Selector selector) {
        return new AppiumElementImpl(this, appiumDriver.findElement(bySelector(selector)));
    }

    @Override
    public List<Element> findElements(Selector selector) {
        return appiumDriver.findElements(bySelector(selector))
                .stream()
                .map((e) -> new AppiumElementImpl(this, e))
                .collect(Collectors.toList());
    }

    static By bySelector(Selector selector) {
        Objects.requireNonNull(selector, "Selector must not be null");
        String value = selector.getValue();

        switch (selector.getType().toLowerCase()) {
            case "xpath":
                return AppiumBy.xpath(value);
            case "id":
                return AppiumBy.id(value);
            case "class_name":
                return AppiumBy.className(value);
            default:
                throw new UnsupportedOperationException("Unsupported selector type");
        }
    }

    @Override
    public String getPlatform() {
        return appiumDriver.getCapabilities()
                .getCapability("platformName")
                .toString()
                .toLowerCase();
    }

    @Override
    public void slipFinger(Point from, Point to, Duration duration) {

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence sequence = new Sequence(finger, 1);

        sequence.addAction(finger.createPointerMove(Duration.ofMillis(0),
                PointerInput.Origin.viewport(), from.getX(), from.getY()));

        sequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));

        sequence.addAction(finger.createPointerMove(duration,
                PointerInput.Origin.viewport(), to.getX(), to.getY()));

        sequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        appiumDriver.perform(List.of(sequence));


    }

    @Override
    public Size getWindowSize() {
        var dim = appiumDriver.manage().window().getSize();

        return new Size(dim.getWidth(), dim.getHeight());
    }

    @Override
    public void removeFocus() {
        var element = findElement(Selector.from("xpath", "//*[@focused='true']"));
        if (element == null) return;

        element.removeFocus();
    }

    @Override
    public void click(int x, int y) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");

        Sequence sequence = new Sequence(finger, 1);

        sequence.addAction(finger.createPointerMove(Duration.ofMillis(0),
                PointerInput.Origin.viewport(), x, y));

        sequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        sequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        appiumDriver.perform(List.of(sequence));
    }

    @Override
    public void scrollDown() {
        scrollDown(scrollDuration, scrollLength);
    }

    @Override
    public void scrollDown(Duration duration, int length) {

    }

    @Override
    public void scrollDownTo(String text) {
        scrollDownTo(text, scrollDuration, scrollLength);
    }

    @Override
    public void scrollDownTo(String text, Duration duration, int length) {

    }

    @Override
    public void scrollUp() {
        scrollUp(scrollDuration, scrollLength);
    }

    @Override
    public void scrollUp(Duration duration, int length) {

    }

    @Override
    public void scrollUpTo(String text) {
        scrollUpTo(text, scrollDuration, scrollLength);
    }

    @Override
    public void scrollUpTo(String text, Duration duration, int length) {

    }

    @Override
    public void scrollLeft() {
        scrollLeft(scrollDuration, scrollLength);
    }

    @Override
    public void scrollLeft(Duration duration, int length) {

    }

    @Override
    public void scrollLeftTo(String text) {
        scrollLeftTo(text, scrollDuration, scrollLength);
    }

    @Override
    public void scrollLeftTo(String text, Duration duration, int length) {

    }

    @Override
    public void scrollRight() {
        scrollRight(scrollDuration, scrollLength);
    }

    @Override
    public void scrollRight(Duration duration, int length) {

    }

    @Override
    public void scrollRightTo(String text) {
        scrollRightTo(text, scrollDuration, scrollLength);
    }

    @Override
    public void scrollRightTo(String text, Duration duration, int length) {

    }

    @Override
    public void scrollDownToEnd() {

    }

    @Override
    public void scrollUpToStart() {

    }

    @Override
    public void scrollLeftToStart() {

    }

    @Override
    public void scrollRightToEnd() {

    }
}
