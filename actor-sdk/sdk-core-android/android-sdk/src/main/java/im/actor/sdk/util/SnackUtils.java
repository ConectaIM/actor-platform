package im.actor.sdk.util;

/**
 * Created by 98379720172 on 23/08/2016.
 */

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Diego on 16/10/2015.
 */
public class SnackUtils {

    public static Snackbar showError(View view, String msg, View.OnClickListener clickListener, String actionText, int duration) {
        Snackbar snackbar = Snackbar.make(view, msg, duration);

        if (clickListener != null) {
            snackbar.setAction(actionText, clickListener);
            snackbar.setActionTextColor(Color.WHITE);
        }

        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.DKGRAY);

        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.RED);
        snackbar.show();
        return snackbar;
    }

    public static Snackbar showSuccess(View view, String msg, View.OnClickListener clickListener, String actionText, int duration) {
        Snackbar snackbar = Snackbar.make(view, msg, duration);

        if (clickListener != null) {
            snackbar.setAction(actionText, clickListener);
            snackbar.setActionTextColor(Color.WHITE);
        }

        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.DKGRAY);

        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.GREEN);
        snackbar.show();
        return snackbar;
    }

    public static Snackbar showWarn(View view, String msg, View.OnClickListener clickListener, String actionText, int duration) {
        Snackbar snackbar = Snackbar.make(view, msg, duration);

        if (clickListener != null) {
            snackbar.setAction(actionText, clickListener);
            snackbar.setActionTextColor(Color.WHITE);
        }

        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.DKGRAY);

        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
        return snackbar;
    }

}