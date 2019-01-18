package com.liuyi.myrxjava.rxjava;

public class MyDisposableHelper {

    private static boolean isDisposable = false;

    public static boolean isIsDisposable() {
        return isDisposable;
    }

    public static void setDisposable() {
        isDisposable = true;
    }
}
