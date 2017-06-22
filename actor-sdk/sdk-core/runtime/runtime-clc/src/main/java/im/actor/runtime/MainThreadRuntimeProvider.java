package im.actor.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import im.actor.runtime.actors.messages.Void;
import im.actor.runtime.os.OSType;
import javafx.concurrent.Task;

import static javafx.application.Platform.runLater;

/**
 * Created by ex3ndr on 07.08.15.
 */

public class MainThreadRuntimeProvider implements MainThreadRuntime {

    private static final Logger logger = LoggerFactory.getLogger(MainThreadRuntimeProvider.class);


    public static final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
    public Thread main;

    public MainThreadRuntimeProvider() {
        main = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        queue.take().run();
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        });

        main.start();
    }


    @Override
    public void postToMainThread(Runnable runnable) {
        new Thread(new Task<Void>() {
           @Override
           protected Void call() throws Exception {
               runnable.run();
               return null;
           }
       }).start();
    }

    @Override
    public boolean isMainThread() {
        String threadName = Thread.currentThread().getName();
        return threadName.contains("JavaFX Application Thread");
    }

    @Override
    public boolean isSingleThread() {
        return false;
    }

    @Override
    public OSType getOSType() {
        return OSType.OTHER;
    }
}
