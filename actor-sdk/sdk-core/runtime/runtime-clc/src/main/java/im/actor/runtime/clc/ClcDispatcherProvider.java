package im.actor.runtime.clc;

import im.actor.runtime.DispatcherRuntime;

/**
 * Created by diego on 18/06/17.
 */

public class ClcDispatcherProvider implements DispatcherRuntime {
    @Override
    public void dispatch(Runnable runnable) {
        new Thread(runnable).start();
    }
}
