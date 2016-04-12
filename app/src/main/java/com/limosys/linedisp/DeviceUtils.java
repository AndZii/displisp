package com.limosys.linedisp;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

@SuppressLint("NewApi")
public class DeviceUtils {

    public static final  String TAG = DeviceUtils.class.getCanonicalName();
    private static final String PREF_FILE = "mobile.app.device.preferences";
    private static final String PREF_DEVICE_ID = "device_id";
    private static final String PREF_LINE_ID = "line_id";
    private static final String PREF_EMPLOYEE_ID = "employee_id";
    private static final String PREF_COMPANY_NAME = "company_name";
    private static final String PREF_CAR_ID = "car_id";


    private DeviceUtils() {
    }

    /**
     * TODO: change to server generated
     *
     * @param context
     * @return
     */

    public static String getCompanyName(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        String res = pref.getString(PREF_COMPANY_NAME, null);
        return res;
    }

    public static String getCarId(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        String res = pref.getString(PREF_CAR_ID, null);
        return res;
    }

    public static String getDeviceId(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        String res = pref.getString(PREF_DEVICE_ID, null);
        return res;
    }

    public static String getLineId(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        String res = pref.getString(PREF_LINE_ID, null);
        //		if (res == null) {
        //			res = RandomStringUtils.random(50, true, true);
        //			pref.edit().putString(PREF_DEVICE_ID, res).apply();
        //		}
        return res;
    }


    public static String getEmployeeId(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        String res = pref.getString(PREF_EMPLOYEE_ID, null);
        //		if (res == null) {
        //			res = RandomStringUtils.random(50, true, true);
        //			pref.edit().putString(PREF_DEVICE_ID, res).apply();
        //		}
        return res;
    }

    public static void setEmployeeId(Context context, String deviceId) {
        SharedPreferences pref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        pref.edit().putString(PREF_EMPLOYEE_ID, deviceId).apply();
    }

    public static void setLineId(Context context, String deviceId) {
        SharedPreferences pref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        pref.edit().putString(PREF_LINE_ID, deviceId).apply();
    }

    public static void setDeviceId(Context context, String deviceId) {
        SharedPreferences pref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        pref.edit().putString(PREF_DEVICE_ID, deviceId).apply();
    }

    public static void setCompanyName(Context context, String companyName) {
        SharedPreferences pref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        pref.edit().putString(PREF_COMPANY_NAME, companyName).apply();
    }

    public static void setCarId(Context context, String carId) {
        SharedPreferences pref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        pref.edit().putString(PREF_CAR_ID, carId).apply();
    }

    @SuppressLint("NewApi")
    static public <T> void executeAsyncTask(AsyncTask<T, ?, ?> task,
                                            T... params) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            task.execute(params);
        }
    }

    public static String getStringFromBundle(Bundle bundle, String key, String defaultValue) {
        if (bundle == null)
            return defaultValue;

        if (Build.VERSION.SDK_INT < 12) {
            String returns = bundle.getString(key);
            if (returns == null) returns = defaultValue;
            return returns;
        } else
            return bundle.getString(key, defaultValue);
    }
}
