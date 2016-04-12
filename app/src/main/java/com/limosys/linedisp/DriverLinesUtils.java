package com.limosys.linedisp;

import android.content.Context;

import com.google.gson.Gson;
import com.limosys.jsonrpc.JsonRpcClient;
import com.limosys.jsonrpc.JsonRpcException;
import com.limosys.jsonrpc.JsonRpcJavaClient;
import com.limosys.ws.obj.Ws_ETAMatrixResult;
import com.limosys.ws.obj.car.Ws_CarGps;
import com.limosys.ws.obj.displine.Ws_DispLoginParam;
import com.limosys.ws.obj.displine.Ws_GetDispLineParamResult;
import com.limosys.ws.obj.displine.Ws_GetDriverDispLinesResult;
import com.limosys.ws.obj.param.Ws_ETAMatrixParam;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Andrii on 4/11/2016.
 */
public class DriverLinesUtils {

    private static String SERVICE_URL = "http://riverside.limosys.com:91/JlimoWSJson/displines";

    private static JsonRpcJavaClient getJsonRpcJavaClient () {
        return new JsonRpcJavaClient(null,SERVICE_URL,SERVICE_URL);
    }

    public static void driverLineLogin(final Context context, String companyName, String carId, String password ,final GetDispLineParamCallback c) {

        try {
            Ws_DispLoginParam param = new Ws_DispLoginParam(DeviceUtils.getDeviceId(context), companyName, carId, password);
            JSONObject params = new JSONObject();
            params.put("params", new Gson().toJson(param));
            getJsonRpcJavaClient().call("DLDriverLogin", params, new JsonRpcClient.Callback() {

                @Override
                public void onSuccess(Object data) {
                    if (data != null) {

                        Ws_GetDispLineParamResult res = new Gson().fromJson(data.toString(), Ws_GetDispLineParamResult.class);
                        if (res.getError() == null || "".equals(res.getError())) {
                            c.onSuccess(res);
                        } else {
                            c.onError(res.getError());
                        }

                    } else {
                        c.onError("Unknown error - data is null");
                    }
                }

                @Override
                public void onError(JsonRpcException caught) {
                    c.onError(caught.getMessage());
                }
            });
        } catch (JSONException e) {
            c.onError(e.getMessage());
            e.printStackTrace();
        }
    }

    public interface GetDispLineParamCallback {
        void onSuccess(Ws_GetDispLineParamResult res);
        void onError(String error);
    }

    public static void getLinesInfoForDriver(final Context context, String companyName, String carId, String password ,final GetDispLinesForDriverCallback c) {

        try {
            Ws_DispLoginParam param = new Ws_DispLoginParam(DeviceUtils.getDeviceId(context), companyName, carId, password);
            JSONObject params = new JSONObject();
            params.put("params", new Gson().toJson(param));
            getJsonRpcJavaClient().call("DLDriverGetLines", params, new JsonRpcClient.Callback() {

                @Override
                public void onSuccess(Object data) {
                    if (data != null) {

                        Ws_GetDriverDispLinesResult res = new Gson().fromJson(data.toString(), Ws_GetDriverDispLinesResult.class);
                        if (res.getError() == null || "".equals(res.getError())) {
                            c.onSuccess(res);
                        } else {
                            c.onError(res.getError());
                        }

                    } else {
                        c.onError("Unknown error - data is null");
                    }
                }

                @Override
                public void onError(JsonRpcException caught) {
                    c.onError(caught.getMessage());
                }
            });
        } catch (JSONException e) {
            c.onError(e.getMessage());
            e.printStackTrace();
        }
    }

    public interface GetDispLinesForDriverCallback {
        void onSuccess(Ws_GetDriverDispLinesResult res);
        void onError(String error);
    }


    public static void getETAToLines(double lat, double lon, List<Ws_CarGps> listOfGPS, final getETAToLinesCallback c) {

        try {
            Ws_ETAMatrixParam param = new Ws_ETAMatrixParam(lat, lon, listOfGPS);
            JSONObject params = new JSONObject();
            params.put("params", new Gson().toJson(param));
            getJsonRpcJavaClient().call("GetETAMatrix", params, new JsonRpcClient.Callback() {

                @Override
                public void onSuccess(Object data) {
                    if (data != null) {

                        Ws_ETAMatrixResult res = new Gson().fromJson(data.toString(), Ws_ETAMatrixResult.class);
                        if (res.getError() == null || "".equals(res.getError())) {
                            c.onSuccess(res);
                        } else {
                            c.onError(res.getError());
                        }

                    } else {
                        c.onError("Unknown error - data is null");
                    }
                }

                @Override
                public void onError(JsonRpcException caught) {
                    c.onError(caught.getMessage());
                }
            });
        } catch (JSONException e) {
            c.onError(e.getMessage());
            e.printStackTrace();
        }
    }

    public interface getETAToLinesCallback {
        void onSuccess(Ws_ETAMatrixResult res);
        void onError(String error);
    }
}
