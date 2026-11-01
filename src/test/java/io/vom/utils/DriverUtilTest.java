package io.vom.utils;

import io.vom.core.Element;
import io.vom.exceptions.ElementNotFoundException;
import org.junit.Test;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;

import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class DriverUtilTest {

    @Test
    public void waitUntilRetriesExpectedFailures() {
        AtomicInteger attempts = new AtomicInteger();
        Element expected = element();

        Element actual = DriverUtil.waitUntil(Duration.ofSeconds(2), () -> {
            int attempt = attempts.incrementAndGet();
            if (attempt == 1) throw new StaleElementReferenceException("rerender");
            if (attempt == 2) throw new ElementNotFoundException("missing");
            return expected;
        });

        assertSame(expected, actual);
        assertEquals(3, attempts.get());
    }

    @Test
    public void waitUntilRethrowsElementNotFoundOnTimeout() {
        ElementNotFoundException expected = new ElementNotFoundException("missing");
        try {
            DriverUtil.waitUntil(Duration.ZERO, () -> { throw expected; });
        } catch (ElementNotFoundException actual) {
            assertSame(expected, actual);
            return;
        }
        throw new AssertionError("Expected ElementNotFoundException");
    }

    @Test
    public void waitListUntilRetriesEmptyAndStaleResults() {
        AtomicInteger attempts = new AtomicInteger();
        List<Element> expected = List.of(element());

        List<Element> actual = DriverUtil.waitListUntil(Duration.ofSeconds(2), () -> {
            int attempt = attempts.incrementAndGet();
            if (attempt == 1) return null;
            if (attempt == 2) return List.of();
            if (attempt == 3) throw new StaleElementReferenceException("rerender");
            return expected;
        });

        assertSame(expected, actual);
        assertEquals(4, attempts.get());
    }

    @Test
    public void waitListUntilReturnsEmptyOnTimeout() {
        assertTrue(DriverUtil.waitListUntil(Duration.ZERO, List::of).isEmpty());
    }

    @Test
    public void waitListUntilPropagatesUnexpectedFailure() {
        IllegalStateException expected = new IllegalStateException("driver failure");
        try {
            DriverUtil.waitListUntil(Duration.ZERO, () -> { throw expected; });
        } catch (IllegalStateException actual) {
            assertSame(expected, actual);
            return;
        }
        throw new AssertionError("Expected driver failure");
    }

    @Test
    public void waitListUntilPropagatesSupplierTimeout() {
        TimeoutException expected = new TimeoutException("driver timeout");
        try {
            DriverUtil.waitListUntil(Duration.ZERO, () -> { throw expected; });
        } catch (TimeoutException actual) {
            assertSame(expected, actual);
            return;
        }
        throw new AssertionError("Expected supplier timeout");
    }

    private static Element element() {
        return (Element) Proxy.newProxyInstance(
                Element.class.getClassLoader(),
                new Class<?>[]{Element.class},
                (proxy, method, args) -> null
        );
    }
}
