/*
 * Copyright (C) 2015 Actor LLC. <https://actor.im>
 */

package im.actor.core.viewmodel.generics;

import im.actor.core.viewmodel.UserPresence;
import im.actor.runtime.mvvm.ValueModel;

public class ValueModelUserPresence extends ValueModel<UserPresence> {
    /**
     * Create ValueModel
     *
     * @param name         name of variable
     * @param defaultValue default value
     */
    public ValueModelUserPresence(String name, UserPresence defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public UserPresence get() {
        return super.get();
    }
}
