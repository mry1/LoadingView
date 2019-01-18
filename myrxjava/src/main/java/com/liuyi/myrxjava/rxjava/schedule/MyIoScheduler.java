package com.liuyi.myrxjava.rxjava.schedule;

import android.support.annotation.NonNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MyIoScheduler extends MyScheduler {
    @Override
    public MyWorker createWorker() {
        return new EventLoopWorker();
    }

    static final class EventLoopWorker extends MyWorker {
        private final ScheduledExecutorService executor =
                Executors.newScheduledThreadPool(1, new MyRxThreadFactory("Louis's Rx", Thread.NORM_PRIORITY));

        @Override
        public void schedule(Runnable run, long delay, @NonNull TimeUnit unit) {
            executor.schedule(run, delay, unit);
        }
    }

}
