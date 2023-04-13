package io.vom.utils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class CollectionUtils {

    public static <E> E getAny(List<E> list) {
        if (list.isEmpty()) return null;
        var n = ThreadLocalRandom.current().nextInt(0, list.size());

        return list.get(n);
    }

    public static Object getAverageDuplicateUniqFromObjectList(List<Object> list) {
        Set<Object> uniqueSet = new HashSet<>(list);
        List<Map<String, Object>> mapList = new ArrayList<>();

        for (int i = 0; i < uniqueSet.size(); i++) {
            String it = (String) new ArrayList<>(uniqueSet).get(i);
            var count = Collections.frequency(list, it);
            HashMap<String, Object> map = new HashMap<>();
            map.put("count", count);
            map.put("value", it);
            mapList.add(map);
        }
        var a = mapList.stream()
                .mapToInt(value -> (Integer) value.get("count"))
                .max()
                .orElse(0);
        return mapList
                .stream()
                .filter(value -> (Integer) value.get("count") == a)
                .findAny()
                .map(value -> value.get("value"))
                .orElse(null);
    }
}