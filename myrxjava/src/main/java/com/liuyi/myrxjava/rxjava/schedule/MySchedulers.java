package com.liuyi.myrxjava.rxjava.schedule;

import com.liuyi.myrxjava.rxjava.subscribeOn.MyExceptionHelper;

import java.util.concurrent.Callable;

public final class MySchedulers {
    static final MyScheduler IO;

    static {
        IO = initIoSchedule(new IOTask());
    }

    public static MyScheduler initIoSchedule(IOTask ioTask) {
        try {
            return ioTask.call();
        } catch (Throwable e) {
            throw MyExceptionHelper.wrapOrThrow(e);
        }
    }

    public static MyScheduler io() {
        return IO;
    }

    static final class IOTask implements Callable<MyScheduler> {
        @Override
        public MyScheduler call() throws Exception {
            return IoHolder.DEFAULT;
        }
    }

    static final class IoHolder {
        static final MyScheduler DEFAULT = new MyIoScheduler();
    }
}
