package io.vom.core;

import java.util.List;

public interface Searchable {

    Element findElement(Selector selector);

    List<Element> findElements(Selector selector);

}
