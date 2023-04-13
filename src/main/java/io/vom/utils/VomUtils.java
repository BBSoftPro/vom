package io.vom.utils;

import com.github.pemistahl.lingua.api.IsoCode639_1;
import com.github.pemistahl.lingua.api.Language;
import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;
import io.vom.core.Driver;
import io.vom.core.Element;
import org.openqa.selenium.StaleElementReferenceException;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class VomUtils {


    public static void scroll(Driver driver, ScrollDirection direction, Duration duration, int length, Selector scrollContainer) {
        List<Element> elements = null;
        try {
            elements = driver.findElement(scrollContainer).findElements(Selector.from("xpath", "./*"));
        } catch (StaleElementReferenceException ignore) {
            scroll(driver, direction, duration, length, scrollContainer);
        }
        Objects.requireNonNull(elements, "it seems that the given view is not scrollable or selector 'scroll_container' does not works in this case");
        scroll(elements, direction, duration, length);
    }

    public static void scroll(List<Element> elements, ScrollDirection direction, Duration duration, int length) {
        if (elements.size() == 0) return;

        int target = elements.size() / 2;

        var element = elements.get(target);
        var driver = element.getDriver();

        var centerPoint = element.getCenterPoint();
//        System.out.println(centerPoint);
        var d = createScrollDirection(centerPoint, element.getSize(), length, direction);

        driver.slipFinger(d.getStartPoint(), d.getEndPoint(), duration);

        try {
            Thread.sleep(length);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Direction createScrollDirection(Point startPoint, Size size, int length, ScrollDirection scrollDirection) {
        Point endPoint = null;
        switch (scrollDirection) {
            case UP:
                startPoint.move(0, -(size.getHeight() / 2 - 1));
                endPoint = startPoint.clone()
                        .move(0, length);
                break;
            case DOWN:
                startPoint.move(0, size.getHeight() / 2 - 1);
                endPoint = startPoint.clone()
                        .move(0, -length);
                break;
            case LEFT:
                startPoint.move(-(size.getWidth() / 2 - 1), 0);
                endPoint = startPoint.clone()
                        .move(length, 0);
                break;
            case RIGHT:
                startPoint.move(size.getWidth() / 2 - 1, 0);
                endPoint = startPoint.clone()
                        .move(-length, 0);
                break;
        }
//        System.out.println("startPoint: " + startPoint + ", " + "endPoint: " + endPoint);

        return new Direction(startPoint.clone(), endPoint);
    }

    public static Locale getLocale(List<String> list) {
        String[] arr = Properties.getInstance().getProperty("locale_languages", "")
                .split(";");
        LanguageDetector detector;
        if (arr.length <= 1) {
            detector = LanguageDetectorBuilder.fromAllLanguages().build();
        } else {
            var languages = Arrays.stream(arr)
                    .map(String::toUpperCase)
                    .map(Language::valueOf)
                    .toArray(Language[]::new);

            detector = LanguageDetectorBuilder.fromLanguages(languages).build();
        }

        var grouped = list.stream()
                .map(detector::detectLanguageOf)
                .map(Language::getIsoCode639_1)
                .map(IsoCode639_1::toString)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return grouped.entrySet().stream().max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .map(Locale::new)
                .orElse(Locale.ENGLISH);
    }
}