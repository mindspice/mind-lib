package io.mindspice.mindlib.http.clients;

import org.apache.http.client.config.RequestConfig;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


public class HttpClient {
    private final CloseableHttpClient client;

    public HttpClient() { this(5_000, 60_000, 300_000); }

    public HttpClient(int connTimeout, int reqTimeout, int socketTimeout) throws IllegalStateException {
        try {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(connTimeout)
                    .setConnectionRequestTimeout(reqTimeout)
                    .setSocketTimeout(socketTimeout)
                    .build();

            client = HttpClients
                    .custom()
                    .setDefaultRequestConfig(requestConfig)
                    .build();
        } catch (Exception e) {
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