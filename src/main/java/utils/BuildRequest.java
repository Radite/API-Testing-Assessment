package utils;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class BuildRequest {

    public static CloseableHttpClient client = HttpClients.createDefault();

    public static HttpGet buildGetRequest(String uri) {
        return new HttpGet(uri);
    }

    public static HttpPost buildPostRequest(String uri, StringEntity entity) {
        HttpPost post = new HttpPost(uri);
        post.setEntity(entity);
        return post;
    }

    public static HttpPut buildPutRequest(String uri, StringEntity entity) {
        HttpPut put = new HttpPut(uri);
        put.setEntity(entity);
        return put;
    }

    public static HttpDelete buildDeleteRequest(String uri) {
        return new HttpDelete(uri);
    }
}
