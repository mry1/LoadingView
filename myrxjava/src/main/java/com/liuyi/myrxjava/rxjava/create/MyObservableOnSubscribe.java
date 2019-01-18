package com.liuyi.myrxjava.rxjava.create;

import com.liuyi.myrxjava.rxjava.create.MyObservableEmitter;

public interface MyObservableOnSubscribe<T> {

    void subscribe(MyObservableEmitter<T> emitter);

}
