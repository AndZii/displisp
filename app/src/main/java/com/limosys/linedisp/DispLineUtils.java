package com.limosys.linedisp;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.limosys.jsonrpc.JsonRpcClient;
import com.limosys.jsonrpc.JsonRpcException;
import com.limosys.jsonrpc.JsonRpcJavaClient;
import com.limosys.ws.obj.displine.Ws_DispLoginParam;
import com.limosys.ws.obj.displine.Ws_DispLoginResult;
import com.limosys.ws.obj.displine.Ws_GetDispLineParam;
import com.limosys.ws.obj.displine.Ws_GetDispLineParamResult;
import com.limosys.ws.obj.displine.Ws_GetLineInfoDispatcherParam;
import com.limosys.ws.obj.displine.Ws_GetLineInfoDispatcherResult;
import com.limosys.ws.obj.displine.Ws_GetLineStatusListParam;
import com.limosys.ws.obj.displine.Ws_GetLineStatusListResult;
import com.limosys.ws.obj.displine.Ws_GetLinesByEmployeeParam;
import com.limosys.ws.obj.displine.Ws_GetLinesByEmployeeResult;
import com.limosys.ws.obj.displine.Ws_SetLineParametersDispatchParam;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Andrii on 3/30/2016.
 */
public class DispLineUtils {

    private static String SERVICE_URL = "http://riverside.limosys.com:91/JlimoWSJson/displines";

    private static JsonRpcJavaClient getJsonRpcJavaClient () {
        return new JsonRpcJavaClient(null,SERVICE_URL,SERVICE_URL);
    }

    public static void getDispLineParams(final Context context, final GetDispLineParamCallback c) {

        try {
            Ws_GetDispLineParam param = new Ws_GetDispLineParam(DeviceUtils.getDeviceId(context));
            JSONObject params = new JSONObject();
            params.put("params", new Gson().toJson(param));
            getJsonRpcJavaClient().call("DLGetDispParam", params, new JsonRpcClient.Callback() {

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

    public static void getLinesByEmployee(int employeeID , final GetLinesByEmployeeId c) {

        try {
            Ws_GetLinesByEmployeeParam param = new Ws_GetLinesByEmployeeParam(employeeID);
            JSONObject params = new JSONObject();
            params.put("params", new Gson().toJson(param));
            getJsonRpcJavaClient().call("DLGetLinesByEmployee", params, new JsonRpcClient.Callback() {

                @Override
                public void onSuccess(Object data) {
                    if (data != null) {

                        Ws_GetLinesByEmployeeResult res = new Gson().fromJson(data.toString(), Ws_GetLinesByEmployeeResult.class);
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

    public interface GetLinesByEmployeeId {
        void onSuccess(Ws_GetLinesByEmployeeResult res);
        void onError(String error);
    }

    public static void getLineStatusList(int employeeID , String deviceId, int lineId, final getLineStatusListCallback c) {

        try {
            Ws_GetLineStatusListParam param = new Ws_GetLineStatusListParam(employeeID, deviceId, lineId);
            JSONObject params = new JSONObject();
            params.put("params", new Gson().toJson(param));
            getJsonRpcJavaClient().call("DLGetLineStatusList", params, new JsonRpcClient.Callback() {

                @Override
                public void onSuccess(Object data) {
                    if (data != null) {

                        Ws_GetLineStatusListResult res = new Gson().fromJson(data.toString(), Ws_GetLineStatusListResult.class);
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

    public interface getLineStatusListCallback {
        void onSuccess(Ws_GetLineStatusListResult res);
        void onError(String error);
    }


       public static void setLineParametersDispatcher(int employeeID , String deviceId, int lineId,
                                                   String statusCode, int reqCars, double amountOfMoney, String description,
                                                   final setLineParametersDispatcherCallback c) {

        try {
            Ws_SetLineParametersDispatchParam param = new Ws_SetLineParametersDispatchParam(employeeID, deviceId, lineId, description);
            param.setStatusCode(statusCode);
            param.setReqCarsCounter(reqCars);
            param.setIncentiveAmount(amountOfMoney);

            JSONObject params = new JSONObject();
            params.put("params", new Gson().toJson(param));
            getJsonRpcJavaClient().call("DLSetLineParametersDispatcher", params, new JsonRpcClient.Callback() {

                @Override
                public void onSuccess(Object data) {
                    if (data != null) {

                        Ws_GetLineInfoDispatcherResult res = new Gson().fromJson(data.toString(), Ws_GetLineInfoDispatcherResult.class);
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

    public interface setLineParametersDispatcherCallback {
        void onSuccess(Ws_GetLineInfoDispatcherResult res);
        void onError(String error);
    }

    public static void dispLineLogin(int employeeID , String password ,String deviceId, int lineId,
                                                   final dispLineLoginCallback c) {

        try {
            Ws_DispLoginParam param = new Ws_DispLoginParam(employeeID, password, deviceId, lineId);


            JSONObject params = new JSONObject();
            params.put("params", new Gson().toJson(param));
            getJsonRpcJavaClient().call("DLDispLogin", params, new JsonRpcClient.Callback() {

                @Override
                public void onSuccess(Object data) {
                    if (data != null) {

                        Ws_DispLoginResult res = new Gson().fromJson(data.toString(), Ws_DispLoginResult.class);
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

    public interface dispLineLoginCallback {
        void onSuccess(Ws_DispLoginResult res);
        void onError(String error);
    }


    public static void getLineInfoDispatcher(int employeeID,String deviceId, int lineId,
                                     final GetLineInfoDispatcher c) {

        try {

            Ws_GetLineInfoDispatcherParam param = new Ws_GetLineInfoDispatcherParam(employeeID,deviceId, lineId);


            JSONObject params = new JSONObject();
            params.put("params", new Gson().toJson(param));
            getJsonRpcJavaClient().call("DLGetLineInfoDispatcher", params, new JsonRpcClient.Callback() {

                @Override
                public void onSuccess(Object data) {
                    if (data != null) {

                        Ws_GetLineInfoDispatcherResult res = new Gson().fromJson(data.toString(), Ws_GetLineInfoDispatcherResult.class);
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

    public interface GetLineInfoDispatcher {
        void onSuccess(Ws_GetLineInfoDispatcherResult res);
        void onError(String error);
    }


}
