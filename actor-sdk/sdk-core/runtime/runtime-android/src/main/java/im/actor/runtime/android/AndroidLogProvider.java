/*
 * Copyright (C) 2015 Actor LLC. <https://actor.im>
 */

package im.actor.runtime.android;

import android.util.Log;

import im.actor.runtime.LogRuntime;

public class AndroidLogProvider implements LogRuntime {

//    public static final int SEND_LOGS_EXPIRES = 1000 * 60 * 60 * 3;
//    private static FileHandler fileHandler;
//    private static boolean writeLogs;
//    private static boolean sendLogs;
//    private static long sendLogsTime;
//    private static String sendLogsUrl = null;
//    private static SharedPreferences shp;
//    private static final OkHttpClient client;

//    static {
//        String name;
//        shp = AndroidContext.getContext().getSharedPreferences("log_props.ini", Context.MODE_PRIVATE);
//        sendLogs = shp.getBoolean("sendLogs", false);
//        sendLogsTime = shp.getLong("sendLogsTime", 0);
//        sendLogsUrl = shp.getString("sendLogsUrl", null);
//        writeLogs = shp.getBoolean("writeLogs", false);
//        if (0 == Environment.getExternalStorageState().compareTo(Environment.MEDIA_MOUNTED)) {
//            name = Environment.getExternalStorageDirectory().getAbsolutePath();
//        } else {
//            name = Environment.getDataDirectory().getAbsolutePath();
//        }
//
//        name += "/actorlogs%g_%u.log";
//
//        try {
//            fileHandler = new FileHandler(name, 256 * 1024, 10, true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        client = new OkHttpClient();
//    }


    @Override
    public void w(String tag, String message) {
        Log.w(tag, message);
//        writeToFile(Level.WARNING, tag, message);
//        sendLogs("w", tag, message);
    }

    @Override
    public void e(String tag, Throwable throwable) {
        Log.e(tag, "", throwable);
//        writeToFile(Level.WARNING, tag, throwable.getMessage());
//        sendLogs("e", tag, throwable.getMessage());
    }

    @Override
    public void d(String tag, String message) {
        Log.d(tag, message);
//        writeToFile(Level.INFO, tag, message);
//        sendLogs("d", tag, message);

    }

    @Override
    public void v(String tag, String message) {
        Log.v(tag, message);
//        writeToFile(Level.ALL, tag, message);
//        sendLogs("v", tag, message);
    }

//    private static int i = 0;
//    private static String s = "";
//
//    private static void sendLogs(String level, String tag, String msg) {
//        if (sendLogs && sendLogsUrl != null) {
//            if (i == 0) {
//                s = s.concat("```");
//            }
//
//            s = s.concat("\n").concat(level).concat(" ").concat(tag).concat(":").concat(msg);
//
//            if (i == 19) {
//                if (System.currentTimeMillis() - sendLogsTime > SEND_LOGS_EXPIRES) {
//                    setSendLogs(null);
//                    return;
//                }
//                s = s.concat("```");
//                JSONObject json = new JSONObject();
//                try {
//                    json.put("text", s);
//                    final Request request = new Request.Builder()
//                            .url(sendLogsUrl)
//                            .post(RequestBody.create(null, json.toString()))
//                            .build();
//                    client.newCall(request).enqueue(new Callback() {
//                        @Override
//                        public void onFailure(Request request, IOException e) {
//
//                        }
//
//                        @Override
//                        public void onResponse(Response response) throws IOException {
//
//                        }
//                    });
//                } catch (JSONException e) {
//                }
//
//                s = "";
//                i = 0;
//            } else {
//                i++;
//            }
//
//        }
//    }
//
//    private static void writeToFile(java.util.logging.Level level, String tag, String msg) {
//        if (writeLogs) {
//            try {
//                fileHandler.setFormatter(new SimpleFormatter());
//                fileHandler.publish(new LogRecord(level, tag + ": " + msg));
//            } catch (Exception e) {
//            }
//        }
//
//    }
//
//    public static boolean toggleWriteLogs() {
//        AndroidLogProvider.writeLogs = !writeLogs;
//        shp.edit().putBoolean("writeLogs", writeLogs).apply();
//        return writeLogs;
//    }
//
//    public static void setSendLogs(String sendLogsUrl) {
//        AndroidLogProvider.sendLogs = sendLogsUrl != null;
//        AndroidLogProvider.sendLogsUrl = sendLogsUrl;
//        AndroidLogProvider.sendLogsTime = sendLogsUrl == null ? 0 : System.currentTimeMillis();
//        shp.edit().putString("sendLogsUrl", sendLogsUrl).apply();
//        shp.edit().putLong("sendLogsTime", sendLogsTime).apply();
//        shp.edit().putBoolean("sendLogs", sendLogs).apply();
//    }
//
//    public static boolean isSendLogsEnabled() {
//        return sendLogs;
//    }
}
