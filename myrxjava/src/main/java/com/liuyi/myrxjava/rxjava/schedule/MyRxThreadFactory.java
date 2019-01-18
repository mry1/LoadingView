package com.liuyi.myrxjava.rxjava.schedule;

import android.support.annotation.NonNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class MyRxThreadFactory extends AtomicLong implements ThreadFactory {
    final String prefix;

    final int priority;


    public MyRxThreadFactory(String prefix, int priority) {
        this.prefix = prefix;
        this.priority = priority;
    }

    @Override
    public Thread newThread(@NonNull Runnable r) {
        StringBuilder nameBuilder = new StringBuilder(prefix).append('-').append(incrementAndGet());

        String name = nameBuilder.toString();
        Thread t = new Thread(r, name);
        t.setPriority(priority);
        t.setDaemon(true);
        return t;
    }

    @Override
    public String toString() {
        return "RxThreadFactory[" + prefix + "]";
    }

}
