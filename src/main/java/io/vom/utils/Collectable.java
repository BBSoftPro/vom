package io.vom.utils;

import com.beust.jcommander.internal.Lists;

import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface Collectable<E extends ElementSupplier> {

    default Duration getScrollDuration() {
        return Properties.DEFAULT_SCROLL_DURATION;
    }

    default int getScrollLength() {
        return Properties.DEFAULT_SCROLL_LENGTH;
    }

    default ScrollDirection getScrollDirection() {
        return ScrollDirection.DOWN;
    }

    default E findAny(Predicate<E> predicate) {
        var pre = getViewList();
        do {
            var opt = pre.stream().filter(predicate).findFirst();

            if (opt.isPresent()) {
                return opt.get();
            }

            VomUtils.scroll(pre.stream().map(ElementSupplier::getElement).collect(Collectors.toList())
                    , getScrollDirection()
                    , getScrollDuration()
                    , getScrollLength());

            var post = getViewList();
            if (!pre.equals(post)) {
                pre = post;
            } else {
                return null;
            }
        } while (true);
    }

    default E findAny() {
        return CollectionUtils.getAny(getViewList());
    }


    default List<E> collect() {
        var l = Lists.<E>newArrayList();
        List<E> before = getViewList();
        List<E> after;

        while (true) {
            VomUtils.scroll(before.stream().map(ElementSupplier::getElement).collect(Collectors.toList())
                    , getScrollDirection()
                    , getScrollDuration()
                    , getScrollLength());
            l.addAll(before);
            after = getViewList();

            if (before.equals(after)) {
                break;
            } else {
                before = after;
            }
        }

        return l.stream().distinct().collect(Collectors.toList());
    }

    default List<E> collect(int count) {
        var l = Lists.<E>newArrayList();

        for (int i = 0; i < count; i++) {
            List<E> mList = getViewList();
            if (!l.isEmpty()) {
                VomUtils.scroll(mList.stream().map(ElementSupplier::getElement).collect(Collectors.toList())
                        , getScrollDirection()
                        , getScrollDuration()
                        , getScrollLength());
                l.addAll(mList);
            } else {
                l.addAll(mList);
            }
        }

        return l.stream().distinct().collect(Collectors.toList());
    }

    List<E> getViewList();
}
