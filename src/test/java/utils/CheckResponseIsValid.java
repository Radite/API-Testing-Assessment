package utils;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.testng.Assert;
import okhttp3.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckResponseIsValid {

    public static void checkStatusCode(CloseableHttpResponse response, int expectedStatusCode) {
        int actualStatusCode = response.getStatusLine().getStatusCode();
        Assert.assertEquals(actualStatusCode, expectedStatusCode, "Status code mismatch");
        assertThat(actualStatusCode).isEqualTo(expectedStatusCode);
    }

    public static void checkStatusCode(Response response, int expectedStatusCode) {
        int actualStatusCode = response.code();
        Assert.assertEquals(actualStatusCode, expectedStatusCode, "Status code mismatch");
        assertThat(actualStatusCode).isEqualTo(expectedStatusCode);
    }

    public static void checkStatusCode(io.restassured.response.Response response, int expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, expectedStatusCode, "Status code mismatch");
        assertThat(actualStatusCode).isEqualTo(expectedStatusCode);
    }
}
