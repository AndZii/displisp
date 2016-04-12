package com.limosys.jsonrpc;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpClientFactory {

    private HttpClientFactory() {
    }

    /**
     * gives adapted for uncertified HTTPS servers HttpClient. For test use only!!!
     *
     * @param connectionTimeout
     * @param socketTimeout
     * @param targetHttpPort
     * @param targetHttpsPort
     * @return
     */
    public static HttpClient getNewHttpClient(int connectionTimeout, int socketTimeout, int targetHttpPort,
                                              int targetHttpsPort, boolean debug) {

        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore
                    .getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            HttpConnectionParams.setConnectionTimeout(params, connectionTimeout < 0 ? 0 : connectionTimeout);
            HttpConnectionParams.setSoTimeout(params, socketTimeout < 0 ? 0 : socketTimeout);

            SchemeRegistry registry = new SchemeRegistry();
            if (targetHttpPort > 0)
                registry.register(new Scheme("http", PlainSocketFactory
                        .getSocketFactory(), targetHttpPort));

            if (targetHttpsPort > 0)
                registry.register(new Scheme("https", sf, targetHttpsPort));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(
                    params, registry);

            return debug ? new DefaultHttpClient(ccm, params) : new DefaultHttpClient(params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    /**
     * test ssl socket factory for ignoring unknown or missed certificates IN
     * TEST PURPOSE ONLY!!!!
     *
     * @author nik
     */
    static class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory(KeyStore truststore)
                throws NoSuchAlgorithmException, KeyManagementException,
                KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port,
                                   boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host,
                    port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }

}
