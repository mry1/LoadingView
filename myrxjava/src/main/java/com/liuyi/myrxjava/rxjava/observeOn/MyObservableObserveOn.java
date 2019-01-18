package com.liuyi.myrxjava.rxjava.observeOn;

import com.liuyi.myrxjava.rxjava.MyObservable;
import com.liuyi.myrxjava.rxjava.MyObserver;
import com.liuyi.myrxjava.rxjava.schedule.MyScheduler;

public class MyObservableObserveOn<T> extends MyObservable<T> {
    private MyObservable<T> myObservable;
    private MyScheduler scheduler;

    public MyObservableObserveOn(MyObservable<T> myObservable, MyScheduler scheduler) {
        this.myObservable = myObservable;
        this.scheduler = scheduler;
    }

    @Override
    protected void subscribeActual(MyObserver<? super T> observer) {

    }
}
