package io.vom.utils;

import io.vom.core.Element;
import io.vom.exceptions.ElementNotFoundException;

import java.time.Duration;
import java.time.LocalTime;
import java.util.function.Supplier;

public class DriverUtil {

    public static Element waitUntil(Duration duration, Supplier<Element> supplier) {
        var endTime = LocalTime.now().plus(duration);
        ElementNotFoundException error;
        do{
            try {
                return supplier.get();
            } catch (ElementNotFoundException e) {
                error = e;
            }
        }while (LocalTime.now().isBefore(endTime));

        throw error;
    }
}
