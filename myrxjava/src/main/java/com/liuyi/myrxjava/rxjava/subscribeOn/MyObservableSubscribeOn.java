package com.liuyi.myrxjava.rxjava.subscribeOn;

import com.liuyi.myrxjava.rxjava.MyObservable;
import com.liuyi.myrxjava.rxjava.MyObserver;
import com.liuyi.myrxjava.rxjava.schedule.MyScheduler;

import java.util.concurrent.TimeUnit;

public class MyObservableSubscribeOn<T> extends MyObservable<T> {
    private MyObservable<T> myObservable;
    private MyScheduler scheduler;

    public MyObservableSubscribeOn(MyObservable<T> myObservable, MyScheduler scheduler) {
        this.myObservable = myObservable;
        this.scheduler = scheduler;
    }

    @Override
    protected void subscribeActual(MyObserver<? super T> observer) {
        SubscribeOnObserver<T> subscribeOnObserver = new SubscribeOnObserver<T>(observer);
        scheduler.scheduleDirect(new SubscribeTask(subscribeOnObserver), 0L, TimeUnit.NANOSECONDS);
    }

    static final class SubscribeOnObserver<T> implements MyObserver<T> {

        private MyObserver<? super T> mMyObserver;

        public SubscribeOnObserver(MyObserver<? super T> observer) {
            mMyObserver = observer;
        }

        @Override
        public void onNext(T s) {
            mMyObserver.onNext(s);
        }

        @Override
        public void onError(Exception e) {
            mMyObserver.onError(e);
        }

        @Override
        public void onComplete() {
            mMyObserver.onComplete();
        }
    }

    final class SubscribeTask implements Runnable {
        private SubscribeOnObserver mSubscribeOnObserver;

        public SubscribeTask(SubscribeOnObserver subscribeOnObserver) {
            mSubscribeOnObserver = subscribeOnObserver;
        }

        @Override
        public void run() {
            myObservable.subscribe(mSubscribeOnObserver);
        }
    }
}
