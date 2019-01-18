package com.liuyi.myrxjava.rxjava;

import android.support.annotation.NonNull;

import com.liuyi.myrxjava.plugin.RxJavaPlugins;
import com.liuyi.myrxjava.rxjava.create.MyObservableCreate;
import com.liuyi.myrxjava.rxjava.create.MyObservableOnSubscribe;
import com.liuyi.myrxjava.rxjava.map.MyObservableMap;
import com.liuyi.myrxjava.rxjava.observeOn.MyObservableObserveOn;
import com.liuyi.myrxjava.rxjava.schedule.MyScheduler;
import com.liuyi.myrxjava.rxjava.subscribeOn.MyObservableSubscribeOn;


public abstract class MyObservable<T> implements MyObservableSource<T> {

    public static <T> MyObservable<T> create(MyObservableOnSubscribe<T> observableOnSubscribe) {
        return new MyObservableCreate<>(observableOnSubscribe);
    }

    protected abstract void subscribeActual(MyObserver<? super T> observer);

    public <R> MyObservable<R> map(MyFunction<? super T, ? extends R> mapper) {
        return (MyObservable<R>) RxJavaPlugins.onAssembly(new MyObservableMap<>(this, mapper));
    }

    public MyObservable<T> subscribeOn(MyScheduler scheduler) {
        return new MyObservableSubscribeOn<T>(this, scheduler);
    }

    public MyObservable<T> observeOn(MyScheduler scheduler){
        return new MyObservableObserveOn<>(this, scheduler);
    }

    @Override
    public void subscribe(@NonNull MyObserver<? super T> observer) {
        subscribeActual(observer);
    }
}
