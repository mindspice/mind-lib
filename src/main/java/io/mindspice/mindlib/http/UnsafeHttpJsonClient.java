package io.mindspice.mindlib.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.mindspice.mindlib.util.JsonUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;


public class UnsafeHttpJsonClient {
    private final CloseableHttpClient client;

    public UnsafeHttpJsonClient() { this(5_000, 60_000, 300_000); }

    public UnsafeHttpJsonClient(int connTimeout, int reqTimeout, int socketTimeout) throws IllegalStateException {

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
        return new HttpRequestBuilder();
    }

    private class HttpRequestBuilder {
        private String address;
        private int port = -1;
        private String username;
        private String password;
        private String requestString;
        private byte[] requestBytes;
        private boolean returnAsBytes;
        private boolean asPost;
        private boolean asGet;

        public HttpRequestBuilder address(String address) {
            this.address = address;
            return this;
        }

        public HttpRequestBuilder port(int port) {
            this.port = port;
            return this;
        }

        public HttpRequestBuilder credentials(String username, String password) {
            this.username = username;
            this.password = password;
            return this;
        }

        public HttpRequestBuilder request(String request) {
            this.requestString = request;
            return this;
        }

        public HttpRequestBuilder request(JsonNode json) throws JsonProcessingException {
            this.requestString = JsonUtils.writeString(json);
            return this;
        }

        public HttpRequestBuilder request(byte[] request) {
            this.requestBytes = request;
            return this;
        }

        public HttpRequestBuilder asPost() {
            this.asPost = true;
            return this;
        }

        public HttpRequestBuilder asGet() {
            this.asGet = true;
            return this;
        }

        private byte[] executeRequest() {
            if (address == null || port == -1) { throw new IllegalStateException("Must specify host and port"); }
            HttpUriRequest request;

            try {
                if (asPost) {
                    HttpEntity entity = requestBytes != null
                            ? new ByteArrayEntity(requestBytes, ContentType.APPLICATION_OCTET_STREAM)
                            : new StringEntity(requestString, ContentType.APPLICATION_JSON);
                    request = RequestBuilder.post()
                            .setUri(new URIBuilder()
                                            .setHost(address)
                                            .setPort(port)
                                            .build())
                            .addHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8)))
                            .setEntity(entity)
                            .build();
                } else if (asGet) {
                    request = RequestBuilder.get()
                            .setUri(new URIBuilder()
                                            .setHost(address)
                                            .setPort(port)
                                            .build())
                            .addHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8)))
                            .build();
                } else {
                    { throw new IllegalStateException("Failed to set POST or GET"); }
                }

                try (CloseableHttpResponse response = client.execute(request)) {
                    return EntityUtils.toByteArray(response.getEntity());
                }
            } catch (Exception e) {
                throw new IllegalStateException("Request Failed. Reason: " + e.getMessage() + " " + Arrays.toString(e.getStackTrace()));
            }
        }

        public byte[] makeAndGetBytes() {
            return executeRequest();
        }

        public JsonNode makeAndGetJson() throws IOException {
            return JsonUtils.readTree(executeRequest());
        }

        public String makeAndGetString() {
            return new String(executeRequest(), StandardCharsets.UTF_8);
        }
    }
}
