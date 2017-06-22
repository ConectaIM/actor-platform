package im.actor.runtime.clc;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import im.actor.runtime.LogRuntime;

/**
 * Created by amir on 3/12/16.
 */
public class ClcLogProvider implements LogRuntime {
   // private static final Logger logger = LoggerFactory.getLogger(ClcLogProvider.class);

    @Override
    public void w(String tag, String message) {
       // logger.warn(tag + ":" + message);
        System.out.println(tag + ":" + message);
    }

    @Override
    public void e(String tag, Throwable throwable) {
        //logger.error(tag, throwable);
        System.err.println(tag);
        throwable.printStackTrace();
    }

    @Override
    public void d(String tag, String message) {
        //logger.debug(tag + ":" + message);
        System.out.println(tag + ":" + message);
    }

    @Override
    public void v(String tag, String message) {
       // logger.warn(tag + ":" + message);
        System.out.println(tag + ":" + message);
    }
}
