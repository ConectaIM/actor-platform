package im.actor.sdk.controllers.media;

import android.app.Activity;

import im.actor.core.entity.Message;
import im.actor.runtime.android.view.BindedListAdapter;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.sdk.controllers.DisplayListFragment;
import im.actor.sdk.controllers.conversation.messages.MessagesAdapter;
import im.actor.sdk.controllers.conversation.messages.content.AbsMessageViewHolder;
import im.actor.sdk.controllers.conversation.messages.content.preprocessor.ChatListProcessor;

import static im.actor.sdk.util.ActorSDKMessenger.messenger;

/**
 * Created by dsilv on 06/08/2017.
 */

public class SharedMediaFragment extends DisplayListFragment<Message, AbsMessageViewHolder> {

    @Override
    protected BindedListAdapter<Message, AbsMessageViewHolder> onCreateAdapter(BindedDisplayList<Message> displayList, Activity activity) {
        messagesAdapter = new MessagesAdapter(displayList, this, activity);
        if (firstUnread != -1 && messagesAdapter.getFirstUnread() == -1) {
            messagesAdapter.setFirstUnread(firstUnread);
        }
        return messagesAdapter;
    }

    protected BindedDisplayList<Message> onCreateDisplayList() {
        BindedDisplayList<Message> displayList = messenger().getMessageDisplayList(peer);
        if (displayList.getListProcessor() == null) {
            displayList.setListProcessor(new ChatListProcessor(peer, this.getContext()));
        }
        notifyNewMessage(displayList);
        return displayList;
    }

}
