package io.vom.utils;

import io.vom.core.Context;
import io.vom.core.Driver;

public interface ContextBuilder<F extends ContextBuilder<F,T>, T extends Context> {

    F setDriver(Driver driver);

    T build();
}
