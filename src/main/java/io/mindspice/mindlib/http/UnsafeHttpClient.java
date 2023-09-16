package io.mindspice.mindlib.http;

import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


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
        return new HttpRequestBuilder();
    }

    public class HttpRequestBuilder {
        private String address;
        private int port = -1;
        private int maxResponseSize = Integer.MAX_VALUE;
        private String username;
        private String password;
        private String path = "";
        private byte[] request;
        ContentType contentType = ContentType.APPLICATION_OCTET_STREAM;
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

        public HttpRequestBuilder path(String path) {
            this.path = path;
            return this;
        }

        public HttpRequestBuilder maxResponseSize(int maxSize) {
            this.maxResponseSize = maxSize;
            return this;
        }


        public HttpRequestBuilder credentials(String username, String password) {
            this.username = username;
            this.password = password;
            return this;
        }

        public HttpRequestBuilder contentType(ContentType contentType) {
            this.contentType = contentType;
            return this;
        }

        public HttpRequestBuilder request(String request) {
            this.request = request.getBytes();
            return this;
        }

        public HttpRequestBuilder request(byte[] request) {
            this.request = request;
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

        private byte[] executeRequest() throws IOException {
            if (address == null) { throw new IllegalStateException("Must specify host"); }
            URI uri = null;
            try {
                uri = new URI(address + (port != -1 ? ":" + port : "") + path);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Invalid URI: " + uri);
            }

            HttpRequestBase httpReq = asPost ? new HttpPost(uri) : new HttpGet(uri);

            if (httpReq instanceof HttpPost post && request != null) {
                post.setEntity(new ByteArrayEntity(request));
            }

            httpReq.addHeader(HttpHeaders.CONTENT_TYPE, contentType.getMimeType());
            if (username != null && password != null) {
                httpReq.addHeader("Authorization", "Basic "
                        + Base64.getEncoder().encodeToString((username
                        + ":" + password).getBytes(StandardCharsets.UTF_8)));
            }

            try (CloseableHttpResponse response = client.execute(httpReq)) {
                InputStream content =  new LimitedInputStream(response.getEntity().getContent(), maxResponseSize);
                return content.readAllBytes();
            }
        }

        public byte[] makeAndGetBytes() throws IOException {
            return executeRequest();
        }

        public String makeAndGetString() throws IOException {
            return new String(executeRequest(), StandardCharsets.UTF_8);
        }
    }
}