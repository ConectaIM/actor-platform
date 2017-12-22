package im.actor.sdk.controllers.grouppre.view;

import android.content.Context;
import android.view.ViewGroup;

import im.actor.core.entity.GroupPre;
import im.actor.runtime.android.view.BindedListAdapter;
import im.actor.runtime.android.view.SimpleBindedListAdapter;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.runtime.generic.mvvm.SimpleBindedDisplayList;
import im.actor.sdk.view.adapters.OnItemClickedListener;

public class GrupoPreSimpleAdapter extends SimpleBindedListAdapter<GroupPre, GrupoPreHolder> {

    private OnItemClickedListener<GroupPre> onItemClicked;
    private Context context;

    public GrupoPreSimpleAdapter(SimpleBindedDisplayList<GroupPre> displayList,
                                 OnItemClickedListener<GroupPre> onItemClicked,
                                 Context context) {
        super(displayList);
        this.context = context;
        this.onItemClicked = onItemClicked;
    }

    @Override
    public GrupoPreHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new GrupoPreHolder(new GrupoPreView(context), onItemClicked);
    }

    @Override
    public void onBindViewHolder(GrupoPreHolder dialogHolder, int index, GroupPre item) {
        dialogHolder.bind(item, index == getItemCount() - 1);
    }

    @Override
    public void onViewRecycled(GrupoPreHolder holder) {
        holder.unbind();
    }
}
