package im.actor.core.modules;

import com.google.j2objc.annotations.ObjectiveCName;

import im.actor.core.Configuration;
import im.actor.core.Messenger;

/**
 * Created by diego on 24/05/17.
 */

public interface ModuleCreateListener {

    ModuleCreateListener stub = (messenger, configuration) -> new Modules(messenger, configuration);

    /**
     * Called to create module
     *
     * @param messenger     Messenger
     * @param configuration Configuration
     */
    @ObjectiveCName("createModules:withConfiguration:")
    Modules createModules(Messenger messenger, Configuration configuration);
}
