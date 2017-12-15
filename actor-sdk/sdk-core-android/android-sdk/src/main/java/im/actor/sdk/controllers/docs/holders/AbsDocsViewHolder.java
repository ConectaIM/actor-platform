package im.actor.sdk.controllers.docs.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import im.actor.core.entity.Message;
import im.actor.core.viewmodel.FileVM;
import im.actor.sdk.controllers.docs.AbsDocsAdapter;

/**
 * Created by diego on 14/09/17.
 */

public abstract class AbsDocsViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    protected FileVM downloadFileVM;
    protected Message currentMessage;
    protected AbsDocsAdapter adapter;

    public AbsDocsViewHolder(View itemView, AbsDocsAdapter adapter) {
        super(itemView);
        this.adapter = adapter;
        itemView.setOnClickListener(this);
    }

    public final void bindData(Message message) {
        boolean isUpdated = currentMessage == null || currentMessage.getRid() != message.getRid();
        currentMessage = message;
        bindData(message, isUpdated);
    }

    protected abstract void bindData(Message message, boolean isUpdated);

    public abstract void unbind();

    @Override
    public void onClick(View view) {

    }
}