package com.app.kit.annotation;

import java.io.Serializable;
import java.util.function.Supplier;

/**
* @author qiangt
* @date 2023/10/5
* @apiNote MethodSupplier用来接收Class::method，比如User::getName
*/
@FunctionalInterface
public interface MethodSupplier<T, P1> extends Serializable {
    T get(P1 var1);

    default Supplier<T> toSupplier(P1 p1) {
        return () -> {
            return this.get(p1);
        };
    }
}
