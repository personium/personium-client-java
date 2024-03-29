/**
 * Personium
 * Copyright 2014-2021 Personium Project Authors
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

import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;

///**
// * SSLの証明書警告を一切無視するSSLSocketFactory.
// */
/**
 * This is the SSLSocketFactory to ignore any certificate warning of SSL.
 */
public class InsecureSSLSocketFactory extends SSLSocketFactory {
    SSLContext sslContext = SSLContext.getInstance("TLSv1.2");

    // /**
    // * コンストラクタ.
    // * @param truststore キーストア.
    // * @throws NoSuchAlgorithmException NoSuchAlgorithmException
    // * @throws KeyManagementException KeyManagementException
    // * @throws KeyStoreException KeyStoreException
    // * @throws UnrecoverableKeyException UnrecoverableKeyException
    // */
    /**
     * This is the parameterized constructor used to initialize fields.
     * @param truststore キーストア.
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws KeyManagementException KeyManagementException
     * @throws KeyStoreException KeyStoreException
     * @throws UnrecoverableKeyException UnrecoverableKeyException
     */
    public InsecureSSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException,
            KeyStoreException, UnrecoverableKeyException {
        super(truststore);
        TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        sslContext.init(null, new TrustManager[] {tm}, null);
    }

    /**
     * This method is used to create a socket for specified details.
     * @param socket Socket
     * @param host Host Name
     * @param port Port Number
     * @param autoClose Value
     * @return Socket
     * @throws IOException Exception thrown
     */
    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    /**
     * This method is used to create a socket.
     * @return Socket
     * @throws IOException Exception thrown
     */
    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }
}
