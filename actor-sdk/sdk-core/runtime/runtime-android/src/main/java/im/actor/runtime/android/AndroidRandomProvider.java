package im.actor.runtime.android;

import im.actor.runtime.android.crypto.PRNGFixes;
import im.actor.runtime.generic.GenericRandomProvider;

public class AndroidRandomProvider extends GenericRandomProvider {

    private static boolean isFixApplied = false;

    public AndroidRandomProvider() {
        if (isFixApplied) {
            isFixApplied = true;
            PRNGFixes.apply();
        }
    }
}
