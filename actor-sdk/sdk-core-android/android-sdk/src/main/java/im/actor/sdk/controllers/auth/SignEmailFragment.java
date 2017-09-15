package im.actor.sdk.controllers.auth;

import android.app.AlertDialog;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import im.actor.sdk.ActorSDK;
import im.actor.sdk.R;
import im.actor.sdk.util.Fonts;
import im.actor.sdk.util.KeyboardHelper;
import im.actor.sdk.view.SelectorFactory;

import static im.actor.sdk.util.ActorSDKMessenger.messenger;

public class SignEmailFragment extends BaseAuthFragment {

    private EditText emailEditText;
    private KeyboardHelper keyboardHelper;
    private String rawEmail;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sign_email, container, false);

        TextView buttonCotinueText = (TextView) v.findViewById(R.id.button_continue_text);
        StateListDrawable states = SelectorFactory.get(ActorSDK.sharedActor().style.getMainColor(), getActivity());
        buttonCotinueText.setBackgroundDrawable(states);
        buttonCotinueText.setTypeface(Fonts.medium());
        buttonCotinueText.setTextColor(ActorSDK.sharedActor().style.getTextPrimaryInvColor());

        keyboardHelper = new KeyboardHelper(getActivity());

        initView(v);

        v.findViewById(R.id.divider).setBackgroundColor(style.getDividerColor());
        setTosAndPrivacy((TextView) v.findViewById(R.id.disclaimer));

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        //TODO track email auth open
        //messenger().trackAuthPhoneOpen();

        setTitle(R.string.auth_email_title);

        focusEmail();

        keyboardHelper.setImeVisibility(emailEditText, true);
    }

    private void initView(View v) {

        ((TextView) v.findViewById(im.actor.sdk.R.id.button_why)).setTypeface(Fonts.medium());
        ((TextView) v.findViewById(im.actor.sdk.R.id.button_why)).setTextColor(ActorSDK.sharedActor().style.getMainColor());
        v.findViewById(im.actor.sdk.R.id.button_why).setOnClickListener(v1 -> new AlertDialog.Builder(getActivity())
                .setMessage(R.string.auth_email_why_description)
                .setPositiveButton(R.string.auth_phone_why_done, null)
                .show()
                .setCanceledOnTouchOutside(true));

        ((TextView) v.findViewById(R.id.email_login_hint)).setTextColor(ActorSDK.sharedActor().style.getTextSecondaryColor());
        emailEditText = (EditText) v.findViewById(R.id.tv_email);
        emailEditText.setTextColor(ActorSDK.sharedActor().style.getTextPrimaryColor());
        String savedAuthId = messenger().getPreferences().getString("sign_in_auth_id");
        boolean useSaved = savedAuthId != null && !savedAuthId.isEmpty() && savedAuthId.contains("@");
        if (useSaved) {
            emailEditText.setText(savedAuthId);
        } else {
            setSuggestedEmail(emailEditText);

        }

        emailEditText.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_GO) {
                requestCode();
                return true;
            }
            return false;
        });


        TextView switchToPhone = (TextView) v.findViewById(R.id.button_switch_to_phone);
        switchToPhone.setTextColor(ActorSDK.sharedActor().style.getMainColor());

        onClick(switchToPhone, v12 -> switchToPhone());

        if ((ActorSDK.sharedActor().getAuthType() & AuthActivity.AUTH_TYPE_PHONE) == AuthActivity.AUTH_TYPE_PHONE) {
            switchToPhone.setVisibility(View.VISIBLE);
        } else {
            switchToPhone.setVisibility(View.GONE);
        }

        Button singIn = (Button) v.findViewById(R.id.button_sign_in);
        singIn.setTextColor(ActorSDK.sharedActor().style.getTextSecondaryColor());

        onClick(singIn, v13 -> startAuth());

        onClick(v, R.id.button_continue, view -> requestCode());

    }

    private void requestCode() {
        final String ACTION = "Request code email";

        if (emailEditText.getText().toString().trim().length() == 0) {
            new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.auth_error_empty_email)
                    .setPositiveButton(R.string.dialog_ok, null)
                    .show();
            return;
        }

        rawEmail = emailEditText.getText().toString();

        startEmailAuth(rawEmail);
    }

    private void focusEmail() {
        focus(emailEditText);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.sign_in_email, menu);
    }


}
