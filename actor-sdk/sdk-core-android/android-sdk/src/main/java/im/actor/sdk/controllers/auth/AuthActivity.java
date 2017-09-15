package im.actor.sdk.controllers.auth;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v13.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.util.Observable;
import java.util.Observer;

import im.actor.core.entity.AuthCodeRes;
import im.actor.core.entity.AuthMode;
import im.actor.core.entity.AuthRes;
import im.actor.core.entity.AuthStartRes;
import im.actor.core.entity.Sex;
import im.actor.core.network.RpcException;
import im.actor.core.network.RpcInternalException;
import im.actor.core.network.RpcTimeoutException;
import im.actor.runtime.Log;
import im.actor.runtime.actors.Actor;
import im.actor.runtime.actors.ActorCreator;
import im.actor.runtime.actors.ActorRef;
import im.actor.runtime.actors.ActorSystem;
import im.actor.runtime.actors.Props;
import im.actor.runtime.promise.Promise;
import im.actor.runtime.storage.PreferencesStorage;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.R;
import im.actor.sdk.controllers.activity.BaseFragmentActivity;

import static im.actor.core.AuthState.AUTH_EMAIL;
import static im.actor.core.AuthState.AUTH_NAME;
import static im.actor.core.AuthState.AUTH_PHONE;
import static im.actor.core.AuthState.AUTH_START;
import static im.actor.core.AuthState.CODE_VALIDATION_EMAIL;
import static im.actor.core.AuthState.CODE_VALIDATION_PHONE;
import static im.actor.core.AuthState.LOGGED_IN;
import static im.actor.sdk.util.ActorSDKMessenger.messenger;

public class AuthActivity extends BaseFragmentActivity implements Observer {

    private static final String TAG = AuthActivity.class.getSimpleName();

    private static final int PERMISSIONS_REQUEST_READ_SMS = 1;

    public static final String AUTH_STATE_KEY = "auth_state";

    public static final int AUTH_TYPE_PHONE = 1;
    public static final int AUTH_TYPE_EMAIL = 2;

    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;
    private int authState;
    private int availableAuthType = AUTH_TYPE_PHONE;
    private int currentAuthType = AUTH_TYPE_PHONE;
    private long currentPhone;
    private String currentEmail;
    private String transactionHash;
    private String currentCode;
    private boolean isRegistered = false;
    private String currentName;
    private int currentSex;
    private ActorRef authActor;
    private boolean codeValidated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authActor = ActorSystem.system().actorOf(Props.create(new ActorCreator() {
            @Override
            public Actor create() {
                return new Actor();
            }
        }), "actor/auth_promises_actor");


        PreferencesStorage preferences = messenger().getPreferences();
        currentPhone = preferences.getLong("currentPhone", 0);
        currentEmail = preferences.getString("currentEmail");
        transactionHash = preferences.getString("transactionHash");
        isRegistered = preferences.getBool("isRegistered", false);
        codeValidated = preferences.getBool("codeValidated", false);
        currentName = preferences.getString("currentName");
        authState = preferences.getInt("auth_state", AUTH_START);

        updateState(authState, true);

        SMSActivationObservable.getInstance().addObserver(this);
    }


    private void updateState(int state) {
        updateState(state, false);
    }

    private void updateState(int state, boolean force) {
        if (this.authState != 0 && (this.authState == state && !force)) {
            return;
        }

        PreferencesStorage preferences = messenger().getPreferences();
        preferences.putLong("currentPhone", currentPhone);
        preferences.putString("currentEmail", currentEmail);
        preferences.putString("transactionHash", transactionHash);
        preferences.putBool("isRegistered", isRegistered);
        preferences.putBool("codeValidated", codeValidated);
        preferences.putString("currentName", currentName);
        preferences.putInt("auth_state", state);

        if (state != LOGGED_IN && getIsResumed() == false) {
            return;
        }

        this.authState = state;

        switch (state) {
            case AUTH_START:
                startAuth();
                break;
            case AUTH_NAME:
                showFragment(new SignUpFragment(), false);
                break;
            case AUTH_PHONE:
                currentAuthType = AUTH_TYPE_PHONE;
                currentCode = "";
                showFragment(ActorSDK.sharedActor().getDelegatedFragment(ActorSDK.sharedActor().getDelegate().getAuthStartIntent(), new SignPhoneFragment(), BaseAuthFragment.class), false);
                break;
            case AUTH_EMAIL:
                currentCode = "";
                currentAuthType = AUTH_TYPE_EMAIL;
                showFragment(ActorSDK.sharedActor().getDelegatedFragment(ActorSDK.sharedActor().getDelegate().getAuthStartIntent(), new SignEmailFragment(), BaseAuthFragment.class), false);
                break;
            case CODE_VALIDATION_PHONE:
            case CODE_VALIDATION_EMAIL:
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_SMS},
                            PERMISSIONS_REQUEST_READ_SMS);
                }

                Fragment validateCodeFragment = new ValidateCodeFragment();
                Bundle args = new Bundle();

                args.putString("authType", state == CODE_VALIDATION_EMAIL ? ValidateCodeFragment.AUTH_TYPE_EMAIL : ValidateCodeFragment.AUTH_TYPE_PHONE);
                args.putString("authId", state == CODE_VALIDATION_EMAIL ? currentEmail : Long.toString(currentPhone));
                validateCodeFragment.setArguments(args);
                showFragment(validateCodeFragment, false);
                break;
            case LOGGED_IN:
                finish();
                ActorSDK.sharedActor().startAfterLoginActivity(this);
                break;
        }
    }

    public void startAuth() {
        availableAuthType = ActorSDK.sharedActor().getAuthType();
        int authState = AUTH_PHONE;

        if ((availableAuthType & AUTH_TYPE_PHONE) == AUTH_TYPE_PHONE) {
            authState = AUTH_PHONE;
        } else if ((availableAuthType & AUTH_TYPE_EMAIL) == AUTH_TYPE_EMAIL) {
            authState = AUTH_EMAIL;
        }
        updateState(authState);
    }

    public void startAuth(String name) {
        currentName = name;
        currentSex = Sex.UNKNOWN;
        availableAuthType = ActorSDK.sharedActor().getAuthType();
        int authState;
        if (!codeValidated) {
            if ((availableAuthType & AUTH_TYPE_PHONE) == AUTH_TYPE_PHONE) {
                authState = AUTH_PHONE;
            } else if ((availableAuthType & AUTH_TYPE_EMAIL) == AUTH_TYPE_EMAIL) {
                authState = AUTH_EMAIL;
            } else {
                return;
            }

            updateState(authState);

        } else {
            signUp(messenger().doSignup(currentName, currentSex != 0 ? currentSex : Sex.UNKNOWN, transactionHash), currentName, currentSex);
        }
    }

    public void startPhoneAuth(Promise<AuthStartRes> promise, long phone) {
        currentAuthType = AUTH_TYPE_PHONE;
        currentPhone = phone;
        startAuth(promise);
    }

    public void startEmailAuth(Promise<AuthStartRes> promise, String email) {
        currentAuthType = AUTH_TYPE_EMAIL;
        currentEmail = email;
        startAuth(promise);
    }

    private void startAuth(Promise<AuthStartRes> res) {
        showProgress();
        res.then(authStartRes -> {
            if (dismissProgress()) {
                transactionHash = authStartRes.getTransactionHash();
                isRegistered = authStartRes.isRegistered();
                switch (authStartRes.getAuthMode()) {
                    case AuthMode.OTP:
                        switch (currentAuthType) {
                            case AUTH_TYPE_PHONE:
                                updateState(CODE_VALIDATION_PHONE);
                                break;
                            case AUTH_TYPE_EMAIL:
                                updateState(CODE_VALIDATION_EMAIL);
                                break;
                        }
                        break;
                    default:
                }
            }
        }).failure(e -> handleAuthError(e));
    }

    public void validateCode(Promise<AuthCodeRes> promise, String code) {
        currentCode = code;
        showProgress();
        promise.then(authCodeRes -> {
            if (dismissProgress()) {
                codeValidated = true;
                transactionHash = authCodeRes.getTransactionHash();
                if (!authCodeRes.isNeedToSignup()) {
                    messenger().doCompleteAuth(authCodeRes.getResult()).then(aBoolean -> updateState(LOGGED_IN)).failure(e -> handleAuthError(e));
                } else {
                    if (currentName == null || currentName.isEmpty()) {
                        updateState(AUTH_NAME);
                    } else {
                        signUp(messenger().doSignup(currentName, currentSex != 0 ? currentSex : Sex.UNKNOWN, transactionHash), currentName, currentSex);
                    }
                }
            }
        }).failure(e -> handleAuthError(e));
    }

    public void signUp(Promise<AuthRes> promise, String name, int sex) {
        currentName = name;
        currentSex = sex;
        promise.then(authRes -> {
            dismissProgress();
            messenger().doCompleteAuth(authRes).then(aBoolean -> updateState(LOGGED_IN)).failure(e -> handleAuthError(e));

        }).failure(e -> handleAuthError(e));
    }

    public void handleAuthError(final Exception e) {
        runOnUiThread(() -> {
            if (dismissProgress()) {
                boolean canTryAgain = false;
                boolean keepState = false;
                String message = getString(R.string.error_unknown);
                String tag = "UNKNOWN";
                if (e instanceof RpcException) {
                    RpcException re = (RpcException) e;
                    if (re instanceof RpcInternalException) {
                        message = getString(R.string.error_unknown);
                        canTryAgain = true;
                    } else if (re instanceof RpcTimeoutException) {
                        message = getString(R.string.error_connection);
                        canTryAgain = true;
                    } else {
                        if ("PHONE_CODE_EXPIRED".equals(re.getTag()) || "EMAIL_CODE_EXPIRED".equals(re.getTag())) {
                            currentCode = "";
                            message = getString(R.string.auth_error_code_expired);
                            canTryAgain = false;
                        } else if ("PHONE_CODE_INVALID".equals(re.getTag()) || "EMAIL_CODE_INVALID".equals(re.getTag())) {
                            message = getString(R.string.auth_error_code_invalid);
                            canTryAgain = false;
                            keepState = true;
                        } else if ("FAILED_GET_OAUTH2_TOKEN".equals(re.getTag())) {
                            message = getString(R.string.auth_error_failed_get_oauth2_token);
                            canTryAgain = false;
                        } else {
                            message = re.getMessage();
                            canTryAgain = re.isCanTryAgain();
                        }
                    }
                }

                try {
                    if (canTryAgain) {
                        new AlertDialog.Builder(AuthActivity.this)
                                .setMessage(message)
                                .setPositiveButton(R.string.dialog_try_again, (dialog, which) -> {
                                    dismissAlert();
                                    switch (authState) {
                                        case AUTH_EMAIL:
                                        case AUTH_PHONE:
                                            switch (currentAuthType) {
                                                case AUTH_TYPE_PHONE:
                                                    startAuth(messenger().doStartPhoneAuth(currentPhone));
                                                    break;
                                                case AUTH_TYPE_EMAIL:
                                                    startAuth(messenger().doStartEmailAuth(currentEmail));
                                                    break;
                                            }
                                            break;
                                        case CODE_VALIDATION_EMAIL:
                                        case CODE_VALIDATION_PHONE:
                                            validateCode(messenger().doValidateCode(currentCode, transactionHash), currentCode);
                                            break;
                                    }

                                })
                                .setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                                    dismissAlert();
                                    updateState(AUTH_START);
                                }).setCancelable(false)
                                .show()
                                .setCanceledOnTouchOutside(false);
                    } else {
                        final boolean finalKeepState = keepState;
                        new AlertDialog.Builder(AuthActivity.this)
                                .setMessage(message)
                                .setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
                                    dismissAlert();
                                    if (finalKeepState) {
                                        updateState(authState, true);
                                    } else {
                                        if (currentAuthType == AUTH_TYPE_EMAIL) {
                                            switchToEmailAuth();
                                        } else if (currentAuthType == AUTH_TYPE_PHONE) {
                                            switchToPhoneAuth();
                                        } else {
                                            updateState(AUTH_START);
                                        }
                                    }

                                })
                                .setCancelable(false)
                                .show()
                                .setCanceledOnTouchOutside(false);
                    }
                } catch (Exception ex) {
                    Log.e(TAG, ex);
                }
            }
        });

    }

    public void switchToEmailAuth() {
        updateState(AUTH_EMAIL);
    }

    public void switchToPhoneAuth() {
        updateState(AUTH_PHONE);
    }

    public void showProgress() {
        dismissProgress();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(R.string.progress_common));
        progressDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();

        dismissProgress();
        dismissAlert();
    }

    private boolean dismissProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
            return true;
        }
        return false;
    }

    private void dismissAlert() {
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }

    public String getCurrentCode() {
        return currentCode;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    @Override
    public void update(Observable o, Object data) {
        if (data instanceof String) {
            validateCode(messenger().doValidateCode(data.toString(), getTransactionHash()), data.toString());
        }
    }
}



