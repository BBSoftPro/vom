package io.vom.utils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CollectionUtils {

    public static <E> E getAny(List<E> list) {
        if (list.isEmpty()) return null;
        var n = ThreadLocalRandom.current().nextInt(0, list.size());

        return list.get(n);
    }
}
