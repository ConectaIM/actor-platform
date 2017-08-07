package im.actor.sdk.controllers.conversation.midia;

import android.app.Activity;

import im.actor.core.entity.Message;
import im.actor.runtime.android.view.BindedListAdapter;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.sdk.controllers.DisplayListFragment;
import im.actor.sdk.controllers.conversation.messages.content.AbsMessageViewHolder;

/**
 * Created by dsilv on 06/08/2017.
 */

public class SharedMidiaFragment extends DisplayListFragment<Message, AbsMessageViewHolder> {

    @Override
    protected BindedListAdapter<Message, AbsMessageViewHolder> onCreateAdapter(BindedDisplayList<Message> displayList, Activity activity) {
        return null;
    }

}
