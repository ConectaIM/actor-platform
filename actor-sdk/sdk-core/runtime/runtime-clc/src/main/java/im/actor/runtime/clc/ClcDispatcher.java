package im.actor.runtime.clc;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import im.actor.runtime.threading.DispatchCancel;
import im.actor.runtime.threading.Dispatcher;

/**
 * Created by elenoon on 3/1/16.
 */
public class ClcDispatcher implements Dispatcher {

    final ScheduledThreadPoolExecutor executor;

    public ClcDispatcher(String name) {
        executor = new ScheduledThreadPoolExecutor(10);
    }

    @Override
    public DispatchCancel dispatch(Runnable message, long delay) {
        ScheduledFuture<?> t = executor.schedule(message, delay, TimeUnit.MILLISECONDS);
        return ()->{
            t.cancel(false);
        };
    }
}
