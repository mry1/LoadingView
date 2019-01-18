package com.liuyi.myrxjava.rxjava;

import android.support.annotation.NonNull;

public interface MyObservableSource<T> {

    /**
     * Subscribes the given Observer to this MyObservableSource instance.
     *
     * @param observer the Observer, not null
     * @throws NullPointerException if {@code observer} is null
     */
    void subscribe(@NonNull MyObserver<? super T> observer);
}