package com.liuyi.myrxjava.rxjava.schedule;

import android.support.annotation.NonNull;

import com.liuyi.myrxjava.plugin.RxJavaPlugins;

import java.util.concurrent.TimeUnit;

public abstract class MyScheduler {
    public void scheduleDirect(@NonNull Runnable run, long delay, @NonNull TimeUnit unit) {
        final MyWorker w = createWorker();
        w.schedule(run, delay, unit);
    }

    public abstract MyWorker createWorker();

    public static abstract class MyWorker {
        public abstract void schedule(Runnable run, long delay, @NonNull TimeUnit unit);
    }

}
