package im.actor.core.modules;

import com.google.j2objc.annotations.ObjectiveCName;

import im.actor.core.Configuration;
import im.actor.core.Messenger;

/**
 * Created by diego on 24/05/17.
 */

public interface ModuleCreateListener {

    ModuleCreateListener stub = new ModuleCreateListener() {
        @Override
        public Modules createModules(Messenger messenger, Configuration configuration) {
            return new Modules(messenger, configuration);
        }
    };
    /**
     * Called to create module
     *
     * @param messenger     Messenger
     * @param configuration Configuration
     */
    @ObjectiveCName("onLoggedInWithContext:withFirst")
    Modules createModules(Messenger messenger, Configuration configuration);
}
