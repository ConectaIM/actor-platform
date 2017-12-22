package im.actor.sdk.controllers.grouppre.admin;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import im.actor.core.entity.GroupPre;
import im.actor.core.entity.GroupType;
import im.actor.core.viewmodel.GroupPreVM;
import im.actor.core.viewmodel.GroupVM;
import im.actor.runtime.android.view.SimpleBindedListAdapter;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.runtime.generic.mvvm.SimpleBindedDisplayList;
import im.actor.runtime.storage.ListEngineDisplayExt;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.R;
import im.actor.sdk.controllers.SimpleDisplayListFragment;
import im.actor.sdk.controllers.grouppre.view.GrupoPreHolder;
import im.actor.sdk.util.Screen;

import static im.actor.sdk.util.ActorSDKMessenger.messenger;

/**
 * Created by diego on 20/12/2017.
 */

public class GroupPreSelectParentFragment2 extends SimpleDisplayListFragment<GroupPre, GrupoPreHolder> {

    private GroupVM groupVM;
    private Integer parentId = GroupPre.DEFAULT_ID;
    private View emptyGroups;


    public static GroupPreSelectParentFragment2 create(int groupId) {
        Bundle bundle = new Bundle();
        bundle.putInt("groupId", groupId);
        GroupPreSelectParentFragment2 editFragment = new GroupPreSelectParentFragment2();
        editFragment.setArguments(bundle);
        return editFragment;
    }


    @Override
    public void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);
        groupVM = messenger().getGroup(getArguments().getInt("groupId"));
        setTitle(groupVM.getGroupType() == GroupType.CHANNEL ? R.string.select_channel_parent : R.string.select_group_parent);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        SimpleBindedDisplayList<GroupPre> displayList = ActorSDK.sharedActor().getMessenger().getGroupsPreDisplayList(parentId);


        View res = inflate(inflater, container, R.layout.fragment_group_pre_select_parent, displayList);
        res.setBackgroundColor(ActorSDK.sharedActor().style.getMainBackgroundColor());

        // Footer

        FrameLayout footer = new FrameLayout(getActivity());
        footer.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Screen.dp(160)));
        footer.setBackgroundColor(ActorSDK.sharedActor().style.getMainBackgroundColor());
        addFooterView(footer);

        // Header

        View header = new View(getActivity());
        header.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Screen.dp(ActorSDK.sharedActor().style.getDialogsPaddingTopDp())));
        header.setBackgroundColor(ActorSDK.sharedActor().style.getMainBackgroundColor());
        addHeaderView(header);

        // Empty View
        emptyGroups = res.findViewById(R.id.emptyGroups);

        ((TextView) emptyGroups.findViewById(R.id.empty_groups_text)).setTextColor(ActorSDK.sharedActor().style.getMainColor());

        emptyGroups.findViewById(R.id.empty_groups_bg).setBackgroundColor(ActorSDK.sharedActor().style.getMainColor());
        emptyGroups.setVisibility(View.GONE);

        return res;
    }


    @Override
    protected SimpleBindedListAdapter onCreateAdapter(SimpleBindedDisplayList displayList, Activity activity) {
        return null;
    }
}
