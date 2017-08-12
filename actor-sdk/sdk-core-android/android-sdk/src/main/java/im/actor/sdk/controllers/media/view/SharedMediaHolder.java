package im.actor.sdk.controllers.media.view;

import android.view.View;

import im.actor.core.entity.Dialog;
import im.actor.core.entity.Message;
import im.actor.runtime.android.view.BindedViewHolder;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.ActorStyle;
import im.actor.sdk.controllers.dialogs.view.DialogView;

/**
 * Created by diego on 11/08/17.
 */

public class SharedMediaHolder extends BindedViewHolder {

    protected ActorStyle style = ActorSDK.sharedActor().style;
    private Message bindedItem;



    public SharedMediaHolder(View itemView) {
        super(itemView);

        dialogView.setOnClickListener(v -> {
            if (bindedItem != null) {
                onClickListener.onClicked(bindedItem);
            }
        });
        dialogView.setOnLongClickListener(v -> {
            if (bindedItem != null) {
                return onClickListener.onLongClicked(bindedItem);
            }
            return false;
        });
    }

    public void bind(Message data) {
        this.bindedItem = data;

    }

    public void unbind() {
        this.bindedItem = null;

    }


}
