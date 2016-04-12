package com.limosys.linedisp;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class Config {

    public static final String APP_VERSION = "1.00";
    public static final int SOCKET_TIMEOUT = 0;//10000;
    public static final int CONNECTION_TIMEOUT = 2000;//2000;
    public static final int TARGET_HTTP_PORT = 7771;
    public static final int TARGET_HTTPS_PORT = 7771;

    public static int getMajorVersion() {
        return Integer.valueOf(APP_VERSION.split(".")[0]);
    }

    public static int getMinorVersion() {
        return Integer.valueOf(APP_VERSION.split(".")[1]);
    }

    private Config() {
    }

    private static boolean isInitialized = false;
    private final static Map<String, ConfigValue> paramMap = new HashMap<String, ConfigValue>();

    private static synchronized void init(Context context) {
        if (isInitialized)
            return;

        InputStream is = context.getResources().openRawResource(R.raw.config);
        String jsonString = null;
        try {
            jsonString = IOUtils.toString(is);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (jsonString != null) {
            try {
                JSONObject json = new JSONObject(jsonString);

                if (json.has(DEBUG)) paramMap.put(DEBUG, new ConfigValue(json.getBoolean(DEBUG)));
                if (json.has(SERVER_URL))
                    paramMap.put(SERVER_URL, new ConfigValue(json.getString(SERVER_URL)));
                if (json.has(SERVER_URL_TEST))
                    paramMap.put(SERVER_URL_TEST, new ConfigValue(json.getString(SERVER_URL_TEST)));
                if (json.has(ERROR_URL))
                    paramMap.put(ERROR_URL, new ConfigValue(json.getString(ERROR_URL)));
                if (json.has(ICON_URL))
                    paramMap.put(ICON_URL, new ConfigValue(json.getString(ICON_URL)));
                if (json.has(ABOUT_URL))
                    paramMap.put(ABOUT_URL, new ConfigValue(json.getString(ABOUT_URL)));
                if (json.has(SUPPORT_URL))
                    paramMap.put(SUPPORT_URL, new ConfigValue(json.getString(SUPPORT_URL)));
                if (json.has(ASSETS_ICON_PATH))
                    paramMap.put(ASSETS_ICON_PATH, new ConfigValue(json.getString(ASSETS_ICON_PATH)));
                if (json.has(STORAGE_ICON_PATH))
                    paramMap.put(STORAGE_ICON_PATH, new ConfigValue(json.getString(STORAGE_ICON_PATH)));
                if (json.has(INIT_FILES_PATH))
                    paramMap.put(INIT_FILES_PATH, new ConfigValue(json.getString(INIT_FILES_PATH)));
                if (json.has(GEOCODER_API_KEY))
                    paramMap.put(GEOCODER_API_KEY, new ConfigValue(json.getString(GEOCODER_API_KEY)));
                if (json.has(ETA_DISTANCE))
                    paramMap.put(ETA_DISTANCE, new ConfigValue(json.getInt(ETA_DISTANCE)));
                if (json.has(RESERVATION_MONITOR_INTERVAL))
                    paramMap.put(RESERVATION_MONITOR_INTERVAL, new ConfigValue(json.getInt(RESERVATION_MONITOR_INTERVAL)));
                if (json.has(RESERVATION_MONITOR_INTERVAL_BACKGROUND))
                    paramMap.put(RESERVATION_MONITOR_INTERVAL_BACKGROUND,
                            new ConfigValue(json.getInt(RESERVATION_MONITOR_INTERVAL_BACKGROUND)));
                if (json.has(TERMS_URL))
                    paramMap.put(TERMS_URL, new ConfigValue(json.getString(TERMS_URL)));
                if (json.has(SERVER_URL_BACKUP))
                    paramMap.put(SERVER_URL_BACKUP, new ConfigValue(json.getString(SERVER_URL_BACKUP)));
                if (json.has(AUTH_TOKEN))
                    paramMap.put(AUTH_TOKEN, new ConfigValue(json.getString(AUTH_TOKEN)));
                if (json.has(ANIMATE_CARS_ON_THE_MAP))
                    paramMap.put(ANIMATE_CARS_ON_THE_MAP, new ConfigValue(json.getBoolean(ANIMATE_CARS_ON_THE_MAP)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (!paramMap.containsKey(DEBUG)) paramMap.put(DEBUG, new ConfigValue(_DEBUG));
        if (!paramMap.containsKey(SERVER_URL))
            paramMap.put(SERVER_URL, new ConfigValue(_SERVER_URL));
        if (!paramMap.containsKey(SERVER_URL_TEST))
            paramMap.put(SERVER_URL_TEST, new ConfigValue(_SERVER_URL_TEST));
        if (!paramMap.containsKey(ERROR_URL)) paramMap.put(ERROR_URL, new ConfigValue(_ERROR_URL));
        if (!paramMap.containsKey(ICON_URL)) paramMap.put(ICON_URL, new ConfigValue(_ICON_URL));
        if (!paramMap.containsKey(ABOUT_URL)) paramMap.put(ABOUT_URL, new ConfigValue(_ABOUT_URL));
        if (!paramMap.containsKey(SUPPORT_URL))
            paramMap.put(SUPPORT_URL, new ConfigValue(_SUPPORT_URL));
        if (!paramMap.containsKey(ASSETS_ICON_PATH))
            paramMap.put(ASSETS_ICON_PATH, new ConfigValue(_ASSETS_ICON_PATH));
        if (!paramMap.containsKey(STORAGE_ICON_PATH))
            paramMap.put(STORAGE_ICON_PATH, new ConfigValue(_STORAGE_ICON_PATH));
        if (!paramMap.containsKey(INIT_FILES_PATH))
            paramMap.put(INIT_FILES_PATH, new ConfigValue(_INIT_FILES_PATH));
        if (!paramMap.containsKey(GEOCODER_API_KEY))
            paramMap.put(GEOCODER_API_KEY, new ConfigValue(_GEOCODER_API_KEY));
        if (!paramMap.containsKey(ETA_DISTANCE))
            paramMap.put(ETA_DISTANCE, new ConfigValue(_ETA_DISTANCE));
        if (!paramMap.containsKey(RESERVATION_MONITOR_INTERVAL))
            paramMap.put(RESERVATION_MONITOR_INTERVAL, new ConfigValue(_RESERVATION_MONITOR_INTERVAL));
        if (!paramMap.containsKey(RESERVATION_MONITOR_INTERVAL_BACKGROUND))
            paramMap.put(RESERVATION_MONITOR_INTERVAL_BACKGROUND, new ConfigValue(_RESERVATION_MONITOR_INTERVAL_BACKGROUND));
        if (!paramMap.containsKey(SERVER_URL_BACKUP))
            paramMap.put(SERVER_URL_BACKUP, new ConfigValue(_SERVER_URL_BACKUP));
        if (!paramMap.containsKey(TERMS_URL))
            paramMap.put(TERMS_URL, new ConfigValue(_TERMS_URL));
        if (!paramMap.containsKey(AUTH_TOKEN))
            paramMap.put(AUTH_TOKEN, new ConfigValue(_AUTH_TOKEN));
        if (!paramMap.containsKey(ANIMATE_CARS_ON_THE_MAP))
            paramMap.put(ANIMATE_CARS_ON_THE_MAP, new ConfigValue(_ANIMATE_CARS_ON_THE_MAP));
        isInitialized = true;
    }

    public static String getConfigString(Context context, String key) {
        init(context);
        ConfigValue value = paramMap.get(key);
        return value == null ? "" : value.getStrValue();
    }

    public static int getConfigInteger(Context context, String key) {
        init(context);
        ConfigValue value = paramMap.get(key);
        return value == null ? 0 : value.getIntValue();
    }

    public static boolean getConfigBoolean(Context context, String key) {
        init(context);
        ConfigValue value = paramMap.get(key);
        return value == null ? false : value.getBoolValue();
    }

    private static final boolean _DEBUG = true;
    private static final String _SERVER_URL = "http://xxx.com:yyyy/JlimoWSJson/jlimowsjson";
    private static final String _SERVER_URL_BACKUP = "";
    private static final String _SERVER_URL_TEST = "http://xxx.com:yyyy/JlimoWSJson/jlimowsjson";
    private static final String _ERROR_URL = "http://xxx.com:yyyy/JlimoWSJson/error";
    private static final String _ICON_URL = "http://xxx.com:yyyy/JlimoWSJson/icons";
    private static final String _ABOUT_URL = "http://xxx.com:yyyy/JlimoWSJson/about.html";
    private static final String _SUPPORT_URL = "http://xxx.com:yyyy/JlimoWSJson/support.html";
    private static final String _TERMS_URL = "https://xxx.com:yyyy/JlimoWSJson/terms.html";
    private static final String _ASSETS_ICON_PATH = "icons";
    private static final String _STORAGE_ICON_PATH = "icons";
    private static final String _INIT_FILES_PATH = "init";
    private static final String _GEOCODER_API_KEY = "AIzaSyByYBa_hNqvZcPc5gnKY7Un4baL8UqydQs";
    private static final int _ETA_DISTANCE = 200;
    private static final int _RESERVATION_MONITOR_INTERVAL = 5 * 1000;
    private static final int _RESERVATION_MONITOR_INTERVAL_BACKGROUND = 5 * 1000;
    private static final String _AUTH_TOKEN = null;
    private static final boolean _ANIMATE_CARS_ON_THE_MAP = true;

    public static final String DEBUG = "DEBUG";
    public static final String SERVER_URL = "SERVER_URL";
    public static final String SERVER_URL_BACKUP = "SERVER_URL_BACKUP";
    public static final String SERVER_URL_TEST = "SERVER_URL_TEST";
    public static final String ERROR_URL = "ERROR_URL";
    public static final String ICON_URL = "ICON_URL";
    public static final String ABOUT_URL = "ABOUT_URL";
    public static final String SUPPORT_URL = "SUPPORT_URL";
    public static final String ASSETS_ICON_PATH = "ASSETS_ICON_PATH";
    public static final String STORAGE_ICON_PATH = "STORAGE_ICON_PATH";
    public static final String INIT_FILES_PATH = "INIT_FILES_PATH";
    public static final String GEOCODER_API_KEY = "GEOCODER_API_KEY";
    public static final String ETA_DISTANCE = "ETA_DISTANCE";
    public static final String RESERVATION_MONITOR_INTERVAL = "RESERVATION_MONITOR_INTERVAL";
    public static final String RESERVATION_MONITOR_INTERVAL_BACKGROUND = "RESERVATION_MONITOR_INTERVAL_BACKGROUND";
    public static final String TERMS_URL = "TERMS_URL";
    public static final String AUTH_TOKEN = "AUTH_TOKEN";
    public static final String ANIMATE_CARS_ON_THE_MAP = "ANIMATE_CARS_ON_THE_MAP";

    static class ConfigValue {
        private final int intValue;
        private final String strValue;
        private final boolean boolValue;

        ConfigValue(int value) {
            intValue = value;
            strValue = String.valueOf(value);
            boolValue = value == 0 ? false : true;
        }

        ConfigValue(String value) {
            intValue = 0;
            strValue = value;
            boolValue = value == null || "".equals(value) ? false : true;
        }

        ConfigValue(boolean value) {
            intValue = value ? 1 : 0;
            strValue = String.valueOf(value);
            boolValue = value;
        }

        public int getIntValue() {
            return intValue;
        }

        public String getStrValue() {
            return strValue;
        }

        public boolean getBoolValue() {
            return boolValue;
        }
    }
}
