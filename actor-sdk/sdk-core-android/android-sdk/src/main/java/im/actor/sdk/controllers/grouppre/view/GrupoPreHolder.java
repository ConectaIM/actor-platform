package im.actor.sdk.controllers.grouppre.view;

import im.actor.core.entity.GrupoPre;
import im.actor.runtime.android.view.BindedViewHolder;
import im.actor.sdk.view.adapters.OnItemClickedListener;

/**
 * Created by diego on 06/06/17.
 */

public class GrupoPreHolder extends BindedViewHolder {

    private GrupoPre bindedItem;
    private GrupoPreView grupoPreView;

    public GrupoPreHolder(GrupoPreView grupoPreView, final OnItemClickedListener<GrupoPre> onClickListener) {
        super(grupoPreView);
        this.grupoPreView = grupoPreView;
        grupoPreView.setOnClickListener(v -> {
            if (bindedItem != null) {
                onClickListener.onClicked(bindedItem);
            }
        });
    }

    public void bind(GrupoPre data, boolean isLast) {
        this.bindedItem = data;
        this.grupoPreView.bind(data);
        this.grupoPreView.setDividerVisible(!isLast);
    }

    public void unbind() {
        this.bindedItem = null;
        this.grupoPreView.unbind();
    }

}
