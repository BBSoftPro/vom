package io.vom.core;

import io.vom.utils.Selector;

import java.time.Duration;
import java.util.List;

public interface Searchable {

    Element findElement(Selector selector);

    Element findElement(Selector selector, Duration waitUntil);

    Element findNullableElement(Selector selector);

    Element findNullableElement(Selector selector, Duration duration);

    List<Element> findElements(Selector selector);

}
