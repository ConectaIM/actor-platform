package im.actor.core.modules;

import com.google.j2objc.annotations.ObjectiveCName;

import im.actor.core.modules.ModuleContext;

/**
 * Created by diego on 24/05/17.
 */

public interface ModuleStartListener {

    /**
     * Called when module construct is called
     *
     * @param context Modules Context
     */
    @ObjectiveCName("onModuleCreateWithContext:")
    void onModuleCreate(ModuleContext context);

    /**
     * Called when module run
     *
     * @param context Modules Context
     */
    @ObjectiveCName("onRunWithContext:")
    void onRun(ModuleContext context);

    /**
     * Called when loggedin
     *
     * @param context Modules Context
     * @param first First time login
     */
    @ObjectiveCName("onLoggedInWithContext:withFirst")
    void onLoggedIn(ModuleContext context, boolean first);
}
