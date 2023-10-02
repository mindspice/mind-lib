package io.mindspice.mindlib.http.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.mindspice.mindlib.util.JsonUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


public class HttpJsonRequestBuilder {
    private final CloseableHttpClient client;
    private String address;
    private int port = -1;
    private int maxResponseSize = Integer.MAX_VALUE;
    private String username;
    private String password;
    private String path = "";
    protected byte[] request;
    private List<String> getParams;
    ContentType contentType = ContentType.APPLICATION_OCTET_STREAM;
    private boolean returnAsBytes;
    private boolean asPost;
    private boolean asGet;

    public HttpJsonRequestBuilder(CloseableHttpClient client) {
        this.client = client;
    }

    public HttpJsonRequestBuilder address(String address) {
        this.address = address;
        return this;
    }

    public HttpJsonRequestBuilder port(int port) {
        this.port = port;
        return this;
    }

    public HttpJsonRequestBuilder path(String path) {
        this.path = path;
        return this;
    }

    public HttpJsonRequestBuilder maxResponseSize(int maxSize) {
        this.maxResponseSize = maxSize;
        return this;
    }

    public HttpJsonRequestBuilder credentials(String username, String password) {
        this.username = username;
        this.password = password;
        return this;
    }

    public HttpJsonRequestBuilder contentType(ContentType contentType) {
        this.contentType = contentType;
        return this;
    }

    public HttpJsonRequestBuilder request(String request) {
        this.request = request.getBytes();
        return this;
    }

    public HttpJsonRequestBuilder request(byte[] request) {
        this.request = request;
        return this;
    }

    public HttpJsonRequestBuilder addGetParam(String key, String value) {
        if (getParams == null) { getParams = new ArrayList<>(); }
        getParams.add(key);
        getParams.add(value);
        return this;
    }

    public HttpJsonRequestBuilder asPost() {
        this.asPost = true;
        return this;
    }

    public HttpJsonRequestBuilder asGet() {
        this.asGet = true;
        return this;
    }

    protected byte[] executeRequest() throws IOException {
        if (address == null) { throw new IllegalStateException("Must specify host"); }
        URI uri = null;
        try {
            uri = new URI(address + (port != -1 ? ":" + port : "") + path);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URI: " + uri);
        }

        HttpRequestBase httpReq;

        if (asPost && request != null) {
            HttpPost post = new HttpPost(uri);
            post.setEntity(new ByteArrayEntity(request));
            httpReq = post;
        } else if (asGet && getParams != null) {
            URIBuilder uriBuilder = new URIBuilder(uri);
            for (int i = 0; i < getParams.size(); i += 2) {
                uriBuilder.addParameter(getParams.get(i), getParams.get(i + 1));
            }
            try {
                uri = uriBuilder.build();
                httpReq = new HttpGet(uri);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Invalid URI: " + uri);
            }
        } else {
            httpReq = asPost ? new HttpPost(uri) : new HttpGet(uri);
        }

        httpReq.addHeader(HttpHeaders.CONTENT_TYPE, contentType.getMimeType());
        if (username != null && password != null) {
            httpReq.addHeader("Authorization", "Basic "
                    + Base64.getEncoder().encodeToString((username
                    + ":" + password).getBytes(StandardCharsets.UTF_8)));
        }

        if (maxResponseSize == Integer.MAX_VALUE) {
            try (CloseableHttpResponse response = client.execute(httpReq)) {
                InputStream content = response.getEntity().getContent();
                return content.readAllBytes();
            }
        }

        try (CloseableHttpResponse response = client.execute(httpReq)) {
            InputStream content = new LimitedInputStream(response.getEntity().getContent(), maxResponseSize);
            return content.readAllBytes();
        }
    }

    public byte[] makeAndGetBytes() throws IOException {
        return executeRequest();
    }

    public String makeAndGetString() throws IOException {
        return new String(executeRequest(), StandardCharsets.UTF_8);
    }

    // Json Methods

    public JsonNode makeAndGetJson() throws IOException {
        return JsonUtils.readTree(executeRequest());
    }

    public HttpJsonRequestBuilder request(JsonNode json) throws JsonProcessingException {
        this.request = JsonUtils.writeBytes(json);
        return this;
    }

    public HttpJsonRequestBuilder request(Object object) throws JsonProcessingException {
        this.request = JsonUtils.writeBytes(object);
        return this;
    }


}
