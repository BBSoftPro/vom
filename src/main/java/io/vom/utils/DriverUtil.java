package io.vom.utils;

import io.vom.core.Element;
import io.vom.exceptions.ElementNotFoundException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

public class DriverUtil {

    public static Element waitUntil(Duration duration, Supplier<Element> supplier) {
        Element[] result = new Element[1];
        TimeoutException[] supplierTimeout = new TimeoutException[1];
        try {
            new FluentWait<>(supplier)
                    .withTimeout(duration)
                    .pollingEvery(Duration.ofMillis(200))
                    .ignoring(ElementNotFoundException.class)
                    .ignoring(StaleElementReferenceException.class)
                    .until(input -> {
                        try {
                            result[0] = input.get();
                        } catch (TimeoutException e) {
                            supplierTimeout[0] = e;
                            throw e;
                        }
                        return true;
                    });
            return result[0];
        } catch (TimeoutException e) {
            if (e == supplierTimeout[0]) {
                throw e;
            }
            if (e.getCause() instanceof ElementNotFoundException) {
                throw (ElementNotFoundException) e.getCause();
            }
            throw e;
        }
    }

    public static List<Element> waitListUntil(Duration duration, Supplier<List<Element>> supplier) {
        StaleElementReferenceException[] lastStale = new StaleElementReferenceException[1];
        TimeoutException[] supplierTimeout = new TimeoutException[1];
        try {
            return new FluentWait<>(supplier)
                    .withTimeout(duration)
                    .pollingEvery(Duration.ofMillis(200))
                    .ignoring(ElementNotFoundException.class)
                    .ignoring(StaleElementReferenceException.class)
                    .until(input -> {
                        try {
                            List<Element> result = input.get();
                            return result == null || result.isEmpty() ? null : result;
                        } catch (StaleElementReferenceException e) {
                            lastStale[0] = e;
                            throw e;
                        } catch (TimeoutException e) {
                            supplierTimeout[0] = e;
                            throw e;
                        }
                    });
        } catch (TimeoutException e) {
            if (e == supplierTimeout[0]) {
                throw e;
            }
            if (lastStale[0] != null) {
                System.err.println("waitListUntil timed out after stale element reference: " + lastStale[0].getMessage());
            }
            return List.of();
        }
    }
}
