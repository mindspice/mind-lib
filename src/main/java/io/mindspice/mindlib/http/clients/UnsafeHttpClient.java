package io.mindspice.mindlib.http.clients;

import io.mindspice.mindlib.http.clients.HttpJsonRequestBuilder;
import io.mindspice.mindlib.http.clients.HttpRequestBuilder;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;

import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;


public class UnsafeHttpClient {
    private final CloseableHttpClient client;

    public UnsafeHttpClient() { this(5_000, 60_000, 300_000); }

    public UnsafeHttpClient(int connTimeout, int reqTimeout, int socketTimeout) throws IllegalStateException {

        try {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(connTimeout)
                    .setConnectionRequestTimeout(reqTimeout)
                    .setSocketTimeout(socketTimeout).build();

            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(TrustAllStrategy.INSTANCE)
                    .build();

            client = HttpClients
                    .custom()
                    .setDefaultRequestConfig(requestConfig)
                    .setSSLContext(sslContext)
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .build();
        } catch (KeyManagementException |
                 NoSuchAlgorithmException | KeyStoreException e) {
            throw new IllegalStateException("Failed to construct HttpClient.", e);
        }
    }

    public HttpRequestBuilder requestBuilder() {
        return new HttpRequestBuilder(client);
    }

    public HttpJsonRequestBuilder jsonRequestBuilder() {
        return new HttpJsonRequestBuilder(client);
    }
}
