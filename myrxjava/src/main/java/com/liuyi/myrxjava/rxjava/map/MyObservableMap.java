package com.liuyi.myrxjava.rxjava.map;

import com.liuyi.myrxjava.rxjava.MyFunction;
import com.liuyi.myrxjava.rxjava.MyObservable;
import com.liuyi.myrxjava.rxjava.MyObserver;

public class MyObservableMap<T, M> extends MyObservable<T> {
    MyFunction<? super T, ? extends M> mapper;
    MyObservable<T> myObservable;

    public MyObservableMap(MyObservable<T> myObservable, MyFunction<? super T, ? extends M> mapper) {
        this.mapper = mapper;
        this.myObservable = myObservable;
    }

    @Override
    protected void subscribeActual(MyObserver/*<? super M>*/ observer) {
        MapObserver<T, M> observer1 = new MapObserver<T, M>(observer, mapper);
        try {
            myObservable.subscribe(observer1);
        }catch (Exception e){
            observer1.onError(e);
        }
    }

    static final class MapObserver<T, M> implements MyObserver<T> {

        private MyObserver<? super M> observer;
        private MyFunction<? super T, ? extends M> mapper;

        public MapObserver(MyObserver<? super M> observer, MyFunction<? super T, ? extends M> mapper) {
            this.observer = observer;
            this.mapper = mapper;
        }

        @Override
        public void onNext(T t) {
            observer.onNext(mapper.apply(t));
        }

        @Override
        public void onError(Exception e) {
            observer.onError(e);
        }

        @Override
        public void onComplete() {
            observer.onComplete();
        }
    }
}
