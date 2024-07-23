package test;

import base.BaseTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Board;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.CheckResponseIsValid;
import utils.PrepareActualResponse;
import utils.PrepareExpectedResponse;
import utils.CheckActualVsExpectedResponses;

import java.io.IOException;

public class HttpClientTest extends BaseTest {

    private static final String BASE_URL = "https://api.trello.com/1/boards/";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private CloseableHttpClient client;

    private static String boardId;
    private static String apiKey;
    private static String token;

    @BeforeClass
    public void setUpHttpClient() {
        client = HttpClients.createDefault();
        apiKey = System.getenv("API_KEY");
        token = System.getenv("TOKEN");

        if (apiKey == null || token == null) {
            throw new IllegalArgumentException("API_KEY or TOKEN environment variables are not set.");
        }
    }

    @Test
    public void testCreateBoard() throws IOException {
        HttpPost post = new HttpPost(BASE_URL + "?key=" + apiKey + "&token=" + token);
        StringEntity entity = new StringEntity("{\"name\": \"NewBoard\"}");
        post.setEntity(entity);
        post.setHeader("Content-Type", "application/json");

        try (CloseableHttpResponse response = client.execute(post)) {
            CheckResponseIsValid.checkStatusCode(response, 200);

            String responseContent = EntityUtils.toString(response.getEntity());
            Board actualBoard = PrepareActualResponse.parseBoardResponse(responseContent);
            Board expectedBoard = PrepareExpectedResponse.createExpectedBoard(null, "NewBoard");

            CheckActualVsExpectedResponses.checkBoard(actualBoard, expectedBoard);

            boardId = actualBoard.getId(); // Save the board ID
        }
    }

    @Test(dependsOnMethods = {"testCreateBoard"})
    public void testGetBoardById() throws IOException {
        HttpGet get = new HttpGet(BASE_URL + boardId + "?key=" + apiKey + "&token=" + token);
        try (CloseableHttpResponse response = client.execute(get)) {
            CheckResponseIsValid.checkStatusCode(response, 200);

            String responseContent = EntityUtils.toString(response.getEntity());
            Board actualBoard = PrepareActualResponse.parseBoardResponse(responseContent);
            Assert.assertNotNull(actualBoard);
        }
    }

    @Test(dependsOnMethods = {"testCreateBoard"})
    public void testUpdateBoard() throws IOException {
        HttpPut put = new HttpPut(BASE_URL + boardId + "?key=" + apiKey + "&token=" + token);
        StringEntity entity = new StringEntity("{\"name\": \"UpdatedBoard\"}");
        put.setEntity(entity);
        put.setHeader("Content-Type", "application/json");

        try (CloseableHttpResponse response = client.execute(put)) {
            CheckResponseIsValid.checkStatusCode(response, 200);

            String responseContent = EntityUtils.toString(response.getEntity());
            Board actualBoard = PrepareActualResponse.parseBoardResponse(responseContent);
            Board expectedBoard = PrepareExpectedResponse.createExpectedBoard(boardId, "UpdatedBoard");

            CheckActualVsExpectedResponses.checkBoard(actualBoard, expectedBoard);
        }
    }

    @Test(dependsOnMethods = {"testCreateBoard", "testGetBoardById", "testUpdateBoard"})
    public void testDeleteBoard() throws IOException {
        HttpDelete delete = new HttpDelete(BASE_URL + boardId + "?key=" + apiKey + "&token=" + token);
        try (CloseableHttpResponse response = client.execute(delete)) {
            CheckResponseIsValid.checkStatusCode(response, 200);
        }
    }
}
