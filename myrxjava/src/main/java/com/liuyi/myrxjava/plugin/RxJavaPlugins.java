package com.liuyi.myrxjava.plugin;

import android.support.annotation.NonNull;

import com.liuyi.myrxjava.rxjava.MyFunction;
import com.liuyi.myrxjava.rxjava.MyObservable;

public class RxJavaPlugins {
    static volatile MyFunction<? super MyObservable, ? extends MyObservable> onObservableAssembly;

    public static <T> MyObservable<T> onAssembly(@NonNull MyObservable<T> source) {
        MyFunction<? super MyObservable, ? extends MyObservable> f = onObservableAssembly;
        if (f != null) {
            return apply(f, source);
        }
        return source;
    }

    @NonNull
    static <T, R> R apply(@NonNull MyFunction<T, R> f, @NonNull T t) {
        try {
            return f.apply(t);
        } catch (Throwable ex) {
            throw wrapOrThrow(ex);
        }
    }

    public static RuntimeException wrapOrThrow(Throwable error) {
        if (error instanceof Error) {
            throw (Error) error;
        }
        if (error instanceof RuntimeException) {
            return (RuntimeException) error;
        }
        return new RuntimeException(error);
    }
}
