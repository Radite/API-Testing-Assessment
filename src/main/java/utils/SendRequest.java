package utils;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SendRequest {

    public static CloseableHttpResponse sendRequest(HttpUriRequest request) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            return client.execute(request);
        }
    }

    public static String getResponseContent(CloseableHttpResponse response) throws IOException {
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        }
        return result.toString();
    }
}
