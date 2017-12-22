package im.actor.sdk.controllers.grouppre;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;


import im.actor.core.entity.GroupPre;
import im.actor.core.viewmodel.GroupVM;
import im.actor.runtime.Log;
import im.actor.runtime.android.view.BindedListAdapter;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.R;
import im.actor.sdk.controllers.DisplayListFragment;
import im.actor.sdk.controllers.Intents;
import im.actor.sdk.controllers.grouppre.view.GrupoPreAdapter;
import im.actor.sdk.controllers.grouppre.view.GrupoPreHolder;
import im.actor.sdk.util.Screen;
import im.actor.sdk.util.SnackUtils;
import im.actor.sdk.view.adapters.OnItemClickedListener;

import static im.actor.sdk.util.ActorSDKMessenger.groups;
import static im.actor.sdk.util.ActorSDKMessenger.messenger;

/**
 * Created by diego on 13/05/17.
 */

public class GruposPreFragment extends DisplayListFragment<GroupPre, GrupoPreHolder> {

    private View emptyDialogs;
    private static String TAG = GruposPreFragment.class.getName();
    private Integer idGrupoPai = GroupPre.DEFAULT_ID;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        BindedDisplayList<GroupPre> displayList = ActorSDK.sharedActor().getMessenger().getGroupPreDisplayList(idGrupoPai, 1,1);

        View res = inflate(inflater, container, R.layout.fragment_grupos_pre, displayList);
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
        emptyDialogs = res.findViewById(R.id.emptyGroups);
        
        ((TextView) emptyDialogs.findViewById(R.id.empty_groups_text)).setTextColor(ActorSDK.sharedActor().style.getMainColor());

        emptyDialogs.findViewById(R.id.empty_groups_bg).setBackgroundColor(ActorSDK.sharedActor().style.getMainColor());
        emptyDialogs.setVisibility(View.GONE);

        return res;
    }

    protected void onItemClick(GroupPre grupo) {
        entrarNoGrupo(grupo);
    }

    private void entrarNoGrupo(GroupPre grupo) {

        GroupVM groupVM = groups().get(grupo.getGroupId());

        if (groupVM.isMember().get()) {
            startActivity(Intents.openGroupDialog(grupo.getGroupId(), true, getActivity()));
        } else {
            final ProgressDialog dialog = ProgressDialog.show(getContext(), "", "Entrando", true, false);
            messenger().joinGroupById(grupo.getGroupId()).then((val) -> {
                dialog.dismiss();
                startActivity(Intents.openGroupDialog(grupo.getGroupId(), true, getActivity()));
            }).failure((ex) -> {
                dialog.dismiss();
                Log.e(TAG, ex);
                SnackUtils.showError(getView(), "Você não pode entrar neste grupo", Snackbar.LENGTH_INDEFINITE, (v) -> {
                    entrarNoGrupo(grupo);
                }, "Tentar Novamente");
            });
        }
    }

    @Override
    protected BindedListAdapter<GroupPre, GrupoPreHolder> onCreateAdapter(BindedDisplayList<GroupPre> displayList,
                                                                          Activity activity) {
        return new GrupoPreAdapter(displayList, new OnItemClickedListener<GroupPre>() {
            @Override
            public void onClicked(GroupPre item) {
                onItemClick(item);
            }

            @Override
            public boolean onLongClicked(GroupPre item) {
                return false;
            }
        }, activity);
    }


    @Override
    public void onResume() {
        super.onResume();
       // ActorSDK.sharedActor().getMessenger().onGruposPreOpen();
    }

    @Override
    public void onPause() {
        super.onPause();
       // ActorSDK.sharedActor().getMessenger().onGruposPreClosed();
    }

}
