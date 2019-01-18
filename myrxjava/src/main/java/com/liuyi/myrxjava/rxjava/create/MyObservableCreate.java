package com.liuyi.myrxjava.rxjava.create;

import com.liuyi.myrxjava.rxjava.MyDisposableHelper;
import com.liuyi.myrxjava.rxjava.MyObservable;
import com.liuyi.myrxjava.rxjava.MyObserver;

public class MyObservableCreate<T> extends MyObservable<T> {
    private MyObservableOnSubscribe<T> mObservableOnSubscribe;

    public MyObservableCreate(MyObservableOnSubscribe<T> observableOnSubscribe) {
        mObservableOnSubscribe = observableOnSubscribe;
    }

    @Override
    public void subscribeActual(MyObserver<? super T> observer) {
        CreateEmitter<T> createEmitter = new CreateEmitter<T>(observer);
        try {
            mObservableOnSubscribe.subscribe(createEmitter);
        } catch (Exception e) {
            createEmitter.onError(e);
        }
    }

    static final class CreateEmitter<T> implements MyObservableEmitter<T> {
        private MyObserver<? super T> mObserver;

        public CreateEmitter(MyObserver<? super T> observer) {
            mObserver = observer;
        }


        @Override
        public void onNext(T s) {
            if (!MyDisposableHelper.isIsDisposable()) {
                mObserver.onNext(s);
            }
        }

        @Override
        public void onError(Exception e) {
            mObserver.onError(e);
        }

        @Override
        public void onComplete() {
            try {
                mObserver.onComplete();
            } finally {
                MyDisposableHelper.setDisposable();
            }
        }
    }

}
