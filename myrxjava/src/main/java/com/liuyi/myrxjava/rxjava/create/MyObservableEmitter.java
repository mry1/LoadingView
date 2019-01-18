package com.liuyi.myrxjava.rxjava.create;


public interface MyObservableEmitter<T> {

    void onNext(T s);

    void onError(Exception e);

    void onComplete();

}
