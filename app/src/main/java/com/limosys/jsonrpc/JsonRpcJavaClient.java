package com.limosys.jsonrpc;

/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;

import com.limosys.linedisp.Config;
import com.limosys.linedisp.DeviceUtils;

/**
 * An Android JSON-RPC client, unaware of authentication (left up to extending classes).
 */
public class JsonRpcJavaClient implements JsonRpcClient {
    public static final String TAG = JsonRpcJavaClient.class.getSimpleName();
    public static final boolean DEBUG = true;

    private static Map<String, String> goodUrlCache = new ConcurrentHashMap<String, String>();

    protected HttpClient httpClient;

    private final String rpcUrl;
    private final String rpcUrlBackup;
    private final String authToken;

    public JsonRpcJavaClient(String authToken, String rpcUrl, String rpcUrlBackup, int connectionTimeout,
                             int socketTimeout) {
        this.rpcUrl = rpcUrl;
        this.rpcUrlBackup = rpcUrlBackup;
        this.authToken = authToken;

        httpClient = HttpClientFactory.getNewHttpClient(connectionTimeout, socketTimeout, Config.TARGET_HTTP_PORT,
                Config.TARGET_HTTPS_PORT, DEBUG);

    }

    public JsonRpcJavaClient(String authToken, String rpcUrl, String rpcUrlBackup) {
        this(authToken, rpcUrl, rpcUrlBackup, Config.CONNECTION_TIMEOUT, Config.SOCKET_TIMEOUT);
    }

    public void call(String methodName, Object params, final JsonRpcClient.Callback callback) {
        callBatch(Arrays.asList(new JsonRpcClient.Call[]{
                new JsonRpcClient.Call(methodName, params)
        }), new JsonRpcClient.BatchCallback() {
            public void onError(int callIndex, JsonRpcException caught) {
                callback.onError(caught);
            }

            public void onData(Object[] data) {
                if (data[0] != null)
                    callback.onSuccess(data[0]);
            }
        });
    }

    public class NetworkTask extends AsyncTask<Void, Void, Result> {
        private JsonRpcClient.Call call;
        private JsonRpcClient.BatchCallback callback;
        private String requestJson;

        public NetworkTask(final String requestJson, final JsonRpcClient.Call call,
                           final JsonRpcClient.BatchCallback callback,
                           final String url, final String urlBackup) {
            this.call = call;
            this.callback = callback;
            this.requestJson = requestJson;
        }

        private Result provideCall(String url, String requestJson, JsonRpcClient.Call call) {
            HttpResponse httpResponse;
            Result res = new Result(1);

            try {
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new StringEntity(requestJson, "UTF-8"));
                httpResponse = httpClient.execute(httpPost);
                final int responseStatusCode = httpResponse.getStatusLine().getStatusCode();
                if (200 <= responseStatusCode && responseStatusCode < 300) {
                    InputStreamReader ir = new InputStreamReader(httpResponse
                            .getEntity().getContent(),
                            "UTF-8");
                    BufferedReader reader = new BufferedReader(ir, 8 * 1024);

                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }

                    if (ir != null)
                        ir.close();

                    if (reader != null)
                        reader.close();

                    if (DEBUG) {
                        Log.i(TAG, "POST response: " + sb.toString());
                    }
                    JSONTokener tokener = new JSONTokener(sb.toString());
                    JSONObject responseJson = new JSONObject(tokener);
                    JSONArray resultsJson = responseJson.getJSONArray("results");
                    //Object[] resultData = new Object[calls.size()];

                    JSONObject result = resultsJson.getJSONObject(0);
                    if (result.has("error")) {
                        res.addException(0, new JsonRpcException((int) result.getInt("error"),
                                call.getMethodName(), result.getString("message")));
                        res.addResult(0, null);
                    }
                    if (result.has("comps")) {
                        res.addResult(0, result);
                    } else {
                        res.addResult(0, result.get("data"));
                    }

                } else {
                    res.addException(-1, new JsonRpcException(-1,
                            "Received HTTP status code other than HTTP 2xx: "
                                    + httpResponse.getStatusLine().getReasonPhrase()));
                }
            } catch (ClientProtocolException e) {
                res.addException(-1, new JsonRpcException(-2,
                        "ClientProtocolException: "
                                + e.toString()));
                e.printStackTrace();
            } catch (IOException e) {
                if (e instanceof SocketTimeoutException || e instanceof ConnectTimeoutException)
                    res.addException(-1,
                            new JsonRpcException(-3,
                                    "It seems we've got connection or server problems. Try to make you request again"));
                else
                    res.addException(-1, new JsonRpcException(-4,
                            "IOException: "
                                    + e.toString()));
                e.printStackTrace();
            } catch (JSONException e) {
                res.addException(-1, new JsonRpcException(-1,
                        "JSONException: "
                                + e.toString()));
                e.printStackTrace();
            } finally {

            }
            return res;
        }

        @Override
        protected Result doInBackground(Void... params) {
            String url = goodUrlCache.get(rpcUrl);
            if (url == null)
                url = rpcUrl;

            Result res = provideCall(url, requestJson, call);

            if (res.getException(-1) != null
                    && (res.getException(-1).getHttpCode() == -2
                    || res.getException(-1).getHttpCode() == -3 || res.getException(-1).getHttpCode() == -4)
                    && rpcUrlBackup != null && !rpcUrlBackup.isEmpty()) {
                res = provideCall(rpcUrlBackup, requestJson, call);
                if (res.getException(-1) == null
                        || !(res.getException(-1) != null && res.getException(-1).getHttpCode() == -2
                        || res.getException(-1).getHttpCode() == -3 || res.getException(-1).getHttpCode() == -4))
                    goodUrlCache.put(rpcUrl, rpcUrlBackup);
            } else {
                goodUrlCache.put(rpcUrl, url);
            }

            return res;
        }

        @Override
        public void onPostExecute(Result result) {
            if (result == null) {
                callback.onError(-1, new JsonRpcException(-1,
                        "Unknown error: "));
            } else if (result.getException(-1) != null) {
                callback.onError(-1, result.getException(-1));
            } else {
                Object[] resObj = result.getResultData();
                for (int i = 0; i < resObj.length; i++) {
                    if (resObj[i] == null) {
                        JsonRpcException e = result.getException(i);
                        if (e != null) {
                            callback.onError(i, e);
                        }
                    }
                }
                callback.onData(resObj);
            }
        }

    }

    class Result {
        private SparseArray<Object> mResultData = new SparseArray<Object>();
        private SparseArray<JsonRpcException> mExceptions = new SparseArray<JsonRpcException>();
        private int mSize;

        public Result(int size) {
            mSize = size;
        }

        public void addResult(int index, Object o) {
            mResultData.put(index, o);
        }

        public void addException(int index, JsonRpcException e) {
            mExceptions.put(index, e);
        }

        public Object[] getResultData() {
            Object[] res = new Object[mSize];
            for (int i = 0; i < mSize; i++) {
                res[i] = mResultData.get(i, null);
            }
            return res;
        }

        public JsonRpcException getException(int index) {
            return mExceptions.get(index, null);
        }
    }

    public void callBatch(final List<JsonRpcClient.Call> calls,
                          final JsonRpcClient.BatchCallback callback) {
        JSONObject requestJson = new JSONObject();
        JSONArray callsJson = new JSONArray();
        try {
            JSONObject callJson = new JSONObject();
            callJson.put("method", calls.get(0).getMethodName());
            if (calls.get(0).getParams() != null) {
                JSONObject callParams = (JSONObject) calls.get(0).getParams();
                Iterator<String> keysIterator = callParams.keys();
                String key;
                while (keysIterator.hasNext()) {
                    key = keysIterator.next();
                    callJson.put(key, callParams.get(key));
                }

                callsJson.put(0, callJson);
            }

            requestJson.put("calls", callsJson);
            if (authToken != null && !authToken.isEmpty())
                requestJson.put("authToken", authToken);

            if (DEBUG) {
                Log.i(TAG, "POST request: " + requestJson.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NetworkTask nt = new NetworkTask(requestJson.toString(), calls.get(0), callback, rpcUrl, rpcUrlBackup);
        Void param = null;
        DeviceUtils.executeAsyncTask(nt, param);
        //nt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, param);
    }
}
