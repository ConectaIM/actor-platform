package im.actor.sdk.controllers.docs.holders;

import android.view.View;

import im.actor.core.entity.Message;
import im.actor.sdk.controllers.docs.AbsDocsAdapter;

/**
 * Created by diego on 14/09/17.
 */

public class DefaultViewHolder extends AbsDocsViewHolder {
    public DefaultViewHolder(View itemView, AbsDocsAdapter adapter) {
        super(itemView, adapter);
    }

    @Override
    protected void bindData(Message message, boolean isUpdated) {

    }


    @Override
    public void unbind() {

    }
}
