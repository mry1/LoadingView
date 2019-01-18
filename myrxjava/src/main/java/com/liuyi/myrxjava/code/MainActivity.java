package com.liuyi.myrxjava.code;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.liuyi.myrxjava.R;
import com.liuyi.myrxjava.rxjava.MyFunction;
import com.liuyi.myrxjava.rxjava.MyObservable;
import com.liuyi.myrxjava.rxjava.MyObserver;
import com.liuyi.myrxjava.rxjava.create.MyObservableEmitter;
import com.liuyi.myrxjava.rxjava.create.MyObservableOnSubscribe;
import com.liuyi.myrxjava.rxjava.schedule.MySchedulers;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private Toast mToast;

    public void showToast(String s) {
        if (mToast == null) {
            mToast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
            mToast.show();
        }
        mToast.setText(s);
        mToast.show();
    }

    public void test(View v) {
        MyObservable.create(new MyObservableOnSubscribe<String>() {
            @Override
            public void subscribe(MyObservableEmitter<String> emitter) {
                System.out.println(Thread.currentThread().getName() + "1");
                emitter.onNext("33");
//                emitter.onComplete();
//                emitter.onNext("22");
            }
        })
                .subscribeOn(MySchedulers.io())
                .map(new MyFunction<String, String>() {
                    @Override
                    public String apply(String s) {
                        System.out.println(Thread.currentThread().getName() + "2");
                        return s + "222";
                    }
                })
                .map(new MyFunction<String, Integer>() {
                    @Override
                    public Integer apply(String s) {
                        System.out.println(Thread.currentThread().getName() + "3");
                        return Integer.valueOf(s);
                    }
                })
                .subscribe(new MyObserver<Integer>() {
                    @Override
                    public void onNext(Integer s) {
                        System.out.println(Thread.currentThread().getName() + "4");
//                        System.out.println(s);
                    }

                    @Override
                    public void onError(Exception e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
