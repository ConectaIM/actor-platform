package im.actor.sdk.controllers.docs;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import im.actor.core.entity.Message;
import im.actor.core.entity.Peer;
import im.actor.core.entity.content.AbsContent;
import im.actor.core.entity.content.DocumentContent;
import im.actor.core.entity.content.PhotoContent;
import im.actor.core.entity.content.VideoContent;
import im.actor.runtime.android.view.BindedListAdapter;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.sdk.R;
import im.actor.sdk.controllers.ActorBinder;

/**
 * Created by diego on 23/08/17.
 */

public class DocsAdapter extends BindedListAdapter<Message, DocsAdapter.DocsViewHolder>{

    public static final int VIEW_TYPE_PHOTO = 1;
    public static final int VIEW_TYPE_DOCUMENT = 2;
    public static final int VIEW_TYPE_VIDEO = 3;

    private ActorBinder BINDER = new ActorBinder();
    private Context context;
    private Peer peer;

    public DocsAdapter(BindedDisplayList<Message> displayList, Context context) {
        super(displayList);
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        AbsContent content = getItem(position).getContent();

        if(content instanceof PhotoContent){
            return 1;
        }else if(content instanceof DocumentContent){
            return 2;
        }else if(content instanceof VideoContent){
            return 3;
        }

        return -1;
    }

    @Override
    public DocsViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {
        View itemView = null;
        switch (viewType){
            case VIEW_TYPE_PHOTO :
                itemView = LayoutInflater.from(context)
                        .inflate(R.layout.adapter_docs_photo, viewGroup, false);
                return new PhotoViewHolder(itemView);
            case VIEW_TYPE_DOCUMENT:
                itemView = LayoutInflater.from(context)
                        .inflate(R.layout.adapter_docs_document, viewGroup, false);
                return new DocumentViewHolder(itemView);
            case VIEW_TYPE_VIDEO:
                itemView = LayoutInflater.from(context)
                        .inflate(R.layout.adapter_docs_video, viewGroup, false);
                return new DocumentViewHolder(itemView);
            default:
                itemView = LayoutInflater.from(context)
                        .inflate(R.layout.adapter_docs_default, viewGroup, false);
                return new DefaultViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(DocsViewHolder docsViewHolder, int index, Message item) {

    }

    @Override
    public void onViewRecycled(DocsViewHolder holder) {
        holder.unbind();
    }

    public ActorBinder getBinder() {
        return BINDER;
    }

    //Holders
    abstract class DocsViewHolder extends RecyclerView.ViewHolder{
        public DocsViewHolder(View itemView) {
            super(itemView);
        }
        public abstract void unbind();
    }

    class DefaultViewHolder extends DocsViewHolder{
        public DefaultViewHolder(View itemView) {
            super(itemView);
        }
        @Override
        public void unbind() {

        }
    }

    class PhotoViewHolder extends DocsViewHolder{
        public PhotoViewHolder(View itemView) {
            super(itemView);
        }
        @Override
        public void unbind() {

        }
    }

    class DocumentViewHolder extends DocsViewHolder{
        public DocumentViewHolder(View itemView) {
            super(itemView);
        }
        @Override
        public void unbind() {

        }
    }

    class VideoViewHolder extends DocsViewHolder{
        public VideoViewHolder(View itemView) {
            super(itemView);
        }
        @Override
        public void unbind() {

        }
    }

}
