package com.liuyi.myrxjava.rxjava;

public interface MyObserver<T> {

    void onNext(T s);

    void onError(Exception e);

    void onComplete();

}
