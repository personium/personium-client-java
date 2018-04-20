/**
 * Personium
 * Copyright 2014 - 2018 FUJITSU LIMITED
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.personium.client.http;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.ssl.SSLContextBuilder;

///**
// * HttpClientの実装を切り替えてNewする.
// */
/**
 * This class is used for switching the implementation of HttpClient.
 */
public class HttpClientFactory {
    // /** HTTP通信のタイプ. */
    /** Default Type of HTTP communication. */
    public static final String TYPE_DEFAULT = "default";
    // /** HTTP通信のタイプ. */
    /** Insecure Type of HTTP communication. */
    public static final String TYPE_INSECURE = "insecure";
    // /** HTTP通信のタイプ. */
    /** Android Type of HTTP communication. */
    public static final String TYPE_ANDROID = "android";

    /** PORT SSL. */
    private static final int PORTHTTPS = 443;
    /** PORT HTTP. */
    private static final int PORTHTTP = 80;

    // /** デフォルトの接続タイムアウト値(0の場合はタイムアウトしない). */
    /** (No time-out in the case of 0) connection timeout value of default. */
    private static final int TIMEOUT = 0;

    /** Constructor. */
    private HttpClientFactory() {
    }

    // /**
    // * HTTPClientオブジェクトを作成.
    // * @param type 通信タイプ
    // * @param connectionTimeout タイムアウト値(ミリ秒)。0の場合はデフォルト値を利用する。
    // * @return 作成したHttpClientクラスインスタンス
    // */
    /**
     * This method is used to create a HTTPClient object.
     * @param type Type of communication
     * @param connectionTimeout Iime-out value (in milliseconds). Use the default value of 0.
     * @return HttpClient class instance that is created
     */
    public static HttpClient create(final String type, final int connectionTimeout) {
        if (TYPE_ANDROID.equalsIgnoreCase(type)) {
            return createForAndroid(connectionTimeout);
        }

        int timeout = TIMEOUT;
        if (connectionTimeout != 0) {
            timeout = connectionTimeout;
        }
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setSocketTimeout(timeout)
                .setRedirectsEnabled(false)
                .build();

        if (TYPE_DEFAULT.equalsIgnoreCase(type)) {
            return HttpClientBuilder.create()
                    .setDefaultRequestConfig(config)
                    .useSystemProperties()
                    .build();
        } else if (!TYPE_INSECURE.equalsIgnoreCase(type)) {
            return null;
        }

        SSLConnectionSocketFactory sf = null;
        try {
            sf = createInsecureSSLConnectionSocketFactory();
        } catch (Exception e) {
            return null;
        }

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sf)
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .build();
        HttpClientConnectionManager cm = new BasicHttpClientConnectionManager(registry);

        HttpClient hc = HttpClientBuilder.create()
                .setDefaultRequestConfig(config)
                .setConnectionManager(cm)
                .useSystemProperties()
                .build();

        return hc;
    }

    @SuppressWarnings("deprecation")
    private static HttpClient createForAndroid(final int connectionTimeout) {
        SSLSocketFactory sf = null;
        Scheme httpScheme = null;
        Scheme httpsScheme = null;

        try {
            sf = new InsecureSSLSocketFactory(null);
        } catch (KeyManagementException e) {
            return null;
        } catch (UnrecoverableKeyException e) {
            return null;
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (KeyStoreException e) {
            return null;
        }
        httpScheme = new Scheme("https", sf, PORTHTTPS);
        httpsScheme = new Scheme("http", PlainSocketFactory.getSocketFactory(), PORTHTTP);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(httpScheme);
        schemeRegistry.register(httpsScheme);
        HttpParams params = new BasicHttpParams();
        ClientConnectionManager cm = new SingleClientConnManager(params, schemeRegistry);
        HttpClient hc = new DefaultHttpClient(cm, params);

        HttpParams params2 = hc.getParams();
        int timeout = TIMEOUT;
        if (connectionTimeout != 0) {
            timeout = connectionTimeout;
        }
        // 接続のタイムアウト
        /** Connection timed out. */
        HttpConnectionParams.setConnectionTimeout(params2, timeout);
        // データ取得のタイムアウト
        /** Time-out of the data acquisition. */
        HttpConnectionParams.setSoTimeout(params2, timeout);
        // リダイレクトしない
        /** Do Not redirect. */
        HttpClientParams.setRedirecting(params2, false);
        return hc;
    }

    /**
     * This method is used to generate SSLConnectionSocketFactory.
     * @return SSLConnectionSocketFactory that is generated
     */
    private static SSLConnectionSocketFactory createInsecureSSLConnectionSocketFactory()
            throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException {
        SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial(TrustSelfSignedStrategy.INSTANCE)
                .build();

        return new SSLConnectionSocketFactory(
                sslContext,
                NoopHostnameVerifier.INSTANCE);
    }
}
