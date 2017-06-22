package clc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;

import im.actor.core.ApiConfiguration;
import im.actor.core.AuthState;
import im.actor.core.ConfigurationBuilder;
import im.actor.core.DeviceCategory;
import im.actor.core.Messenger;
import im.actor.core.PlatformType;
import im.actor.core.api.ApiArrayValue;
import im.actor.core.api.ApiInt32Value;
import im.actor.core.api.ApiInt64Value;
import im.actor.core.api.ApiRawValue;
import im.actor.core.api.ApiStringValue;
import im.actor.core.api.rpc.ResponseRawRequest;
import im.actor.core.entity.Notification;
import im.actor.core.entity.Peer;
import im.actor.core.entity.PhoneBookContact;
import im.actor.core.entity.Sex;
import im.actor.core.providers.NotificationProvider;
import im.actor.core.providers.PhoneBookProvider;
import im.actor.core.viewmodel.Command;
import im.actor.core.viewmodel.CommandCallback;
import im.actor.core.viewmodel.UserVM;
import im.actor.runtime.Log;
import im.actor.runtime.json.JSONException;
import im.actor.runtime.json.JSONObject;
import im.actor.sdk.ClcMessenger;


public class ClcApplication {

   public static final String TAG = ClcApplication.class.getName();

    static ClcMessenger messenger;
    static int rate = 1000;
    static int myNumber = 1;
    private static int counter = 0;

    private static ArrayList<Long[]> states = new ArrayList<Long[]>();
    private static Timer senderTimer;
    private static int messagesCount = 5;
    private static int randomSeed;


    public static void sendMessage(String number, final String message) {
        messenger.findUsers(number).start(new CommandCallback<UserVM[]>() {
            @Override
            public void onResult(UserVM[] users) {

                if (users.length == 0)
                    return;

                final UserVM user = users[0];
                final Peer peer = Peer.fromUniqueId(user.getId());
                if (!user.isContact().get()) {
                    Command<Boolean> addContact = messenger.addContact(user.getId());

                    assert addContact != null;
                    addContact.start(new CommandCallback<Boolean>() {
                        @Override
                        public void onResult(Boolean res) {
                            if (res) {
                                messenger.sendMessage(peer, message);
                            }
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                } else {
                    messenger.sendMessage(peer, message);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, e);
            }
        });
    }

    public static void signUp(final String name, int sex, String avatarPath) {
        messenger.signUp(name, sex, avatarPath).start(new CommandCallback<Integer>() {
            @Override
            public void onResult(Integer res) {
                if (res == AuthState.LOGGED_IN) {
                    Log.d(TAG,"Logado");
                    sendMessage("+989150000" + (myNumber + 20), "seed: " + randomSeed + "," + myNumber);
                } else if (res == AuthState.SIGN_UP) {
                    Log.d(TAG,"Deve Entrar");
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    public static void sendCode(String code) {
        try {
            messenger.validateCode(code).start(new CommandCallback<Integer>() {
                @Override
                public void onResult(Integer res) {
                    randomSeed = new Random().nextInt();
                    if (res == AuthState.SIGN_UP) {
                        Log.d(TAG,"Vai Logar");
                        signUp("75550000" + (myNumber), Sex.MALE, null);
                    } else if (res == AuthState.LOGGED_IN) {
                       Log.d(TAG,"Ja esta logado, vai enviar msg");

                        // sendMessage("+556191520714", "seed: " + randomSeed + "," + myNumber);
                        //sendMessage("5564999663299", "Teste");

                        // List<ApiMapValueItem> items = new ArrayList<>();
                        //items.add(new ApiMapValueItem("id",new ApiInt32Value(1)));


                        ApiRawValue values = new ApiInt32Value(826965698);

                        messenger.rawRequestCommand("grupoExtService", "getPublicGroups", values).start(new CommandCallback<ResponseRawRequest>() {
                            @Override
                            public void onResult(ResponseRawRequest res) {
                                Log.d(TAG, "onResult: ");
                                Log.d(TAG, res.toString());
                                ApiArrayValue values = (ApiArrayValue) res.getResult();

                                for (ApiRawValue val : values.getArray()) {
                                    JSONObject g = null;
                                    try {
                                        g = new JSONObject(((ApiStringValue) val).getText());

                                        JSONObject obj = new JSONObject();
                                        obj.put("userId", messenger.myUid());
                                        obj.put("groupId", g.getInt("id"));

                                        ApiStringValue param = new ApiStringValue(obj.toString());
                                        messenger.rawRequestCommand("grupoExtService", "getInviteLink", param).start(new CommandCallback<ResponseRawRequest>() {
                                            @Override
                                            public void onResult(ResponseRawRequest res) {
                                                Log.d(TAG,((ApiStringValue) res.getResult()).getText());
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                Log.e(TAG, e);
                                            }
                                        });


                                        ApiInt64Value idGrupo = new ApiInt64Value(obj.getLong("groupId"));

                                        messenger.rawRequestCommand("grupoExtService", "getGroupAdmin", idGrupo).start(new CommandCallback<ResponseRawRequest>() {
                                            @Override
                                            public void onResult(ResponseRawRequest res) {
                                                Log.d(TAG,String.valueOf(((ApiInt64Value) res.getResult()).getValue()));
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                Log.e(TAG, e);
                                            }
                                        });

                                        Log.d(TAG, g.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e(TAG, e);
                            }
                        });
                    }

                }

                @Override
                public void onError(Exception e) {

                }
            });
        } catch (Exception e) {
            Log.e(TAG, e);
        }
    }

    public static void requestSms(int phone) {
        long res = Long.parseLong("75550000" + phone);
        messenger.requestStartPhoneAuth(res).start(new CommandCallback<Integer>() {
            @Override
            public void onResult(Integer res) {
                Log.d(TAG,res.toString());
                sendCode("0000");

            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, e);
            }
        });

//        messenger.requestStartPhoneAuth2(Long.parseLong("75550000" + phone))
//                .then(new Consumer<AuthStartRes>() {
//            @Override
//            public void apply(AuthStartRes res) {
//                logger.info(res.toString());
//
//                sendMessage("5564999663299", "Teste");
//
//            }
//        }).failure(new Consumer<Exception>() {
//            @Override
//            public void apply(Exception e) {
//             logger.error(e.getMessage(), e);
//            }
//        });
    }

    public static void main(String[] args) {

        ConfigurationBuilder builder = new ConfigurationBuilder();


        if (args.length > 0)
            builder.addEndpoint(args[0]);
        else
            builder.addEndpoint("tcp://localhost:9070");

        builder.setPhoneBookProvider(new PhoneBookProvider() {
            @Override
            public void loadPhoneBook(Callback callback) {
                callback.onLoaded(new ArrayList<PhoneBookContact>());
            }
        });

        builder.setNotificationProvider(new NotificationProvider() {
            @Override
            public void onMessageArriveInApp(Messenger messenger) {
                Log.d(TAG,"onMessageArriveInApp");
            }

            @Override
            public void onNotification(Messenger messenger, List<Notification> topNotifications, int messagesCount, int conversationsCount) {
                Log.d(TAG,"onNotification");

            }

            @Override
            public void onUpdateNotification(Messenger messenger, List<Notification> topNotifications, int messagesCount, int conversationsCount) {
                Log.d(TAG,"onUpdateNotification");
            }

            @Override
            public void hideAllNotifications() {
                Log.d(TAG,"hideAllNotifications");
            }
        });

        builder.setDeviceCategory(DeviceCategory.DESKTOP);
        builder.setPlatformType(PlatformType.GENERIC);
        builder.setApiConfiguration(new ApiConfiguration(
                "cli",
                1,
                "4295f9666fad3faf2d04277fe7a0c40ff39a85d313de5348ad8ffa650ad71855",
                "najva00000000000000000123-" + myNumber,
                "najva00000000000000000000-v1-" + myNumber));


        messenger = new ClcMessenger(builder.build(), String.valueOf(myNumber));

//        messenger.resetAuth();
        requestSms(myNumber);


    }

}
