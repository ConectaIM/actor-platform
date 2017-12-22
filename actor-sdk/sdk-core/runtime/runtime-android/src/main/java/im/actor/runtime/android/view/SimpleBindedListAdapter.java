/*
 * Copyright (C) 2015 Actor LLC. <https://actor.im>
 */

package im.actor.runtime.android.view;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import im.actor.runtime.bser.BserObject;
import im.actor.runtime.generic.mvvm.AndroidListUpdate;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.runtime.generic.mvvm.ChangeDescription;
import im.actor.runtime.generic.mvvm.DisplayList;
import im.actor.runtime.generic.mvvm.SimpleBindedDisplayList;
import im.actor.runtime.storage.ListEngineDisplayExt;
import im.actor.runtime.storage.ListEngineDisplayListener;
import im.actor.runtime.storage.ListEngineItem;

public abstract class SimpleBindedListAdapter<V extends BserObject & ListEngineItem,
        T extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<T> {


    private SimpleBindedDisplayList<V> displayList;

    public SimpleBindedListAdapter(SimpleBindedDisplayList<V> displayList) {
        this(displayList, true);
    }

    public SimpleBindedListAdapter(SimpleBindedDisplayList<V> displayList, boolean autoConnect) {
        this.displayList = displayList;
        setHasStableIds(true);
        if (autoConnect) {
            resume();
        }
    }

    @Override
    public int getItemCount() {
//        if (displayList != null) {
//            return displayList.getCount();
//        }
        return 0;
    }

    protected V getItem(int position) {
//        if (displayList != null) {
//            return displayList.getValue(position);
//        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getEngineId();
    }

    @Override
    public abstract T onCreateViewHolder(ViewGroup viewGroup, int viewType);

    @Override
    public final void onBindViewHolder(T dialogHolder, int i) {
        onBindViewHolder(dialogHolder, i, getItem(i));
    }

    public abstract void onBindViewHolder(T dialogHolder, int index, V item);


    public void resume() {
//        displayList.subscribe(listener);
        notifyDataSetChanged();
    }

    public void pause() {
//        displayList.unsubscribe(listener);
    }

    public void dispose() {
        pause();
    }
}
