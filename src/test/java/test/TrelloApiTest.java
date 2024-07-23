package test;

import base.BaseTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Board;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.*;

import java.io.IOException;

public class TrelloApiTest extends BaseTest {

    private static final String BASE_URL = "https://api.trello.com/1/boards/";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static String boardId;
    private static String apiKey;
    private static String token;

    @BeforeClass
    public void setUp() {
        apiKey = System.getenv("API_KEY");
        token = System.getenv("TOKEN");

        if (apiKey == null || token == null) {
            throw new IllegalArgumentException("API_KEY or TOKEN environment variables are not set.");
        }
    }

    @Test
    public void testCreateBoard() throws IOException {
        String orgId = "668d6e6948dbc83abec52cc8"; // Your organization ID
        String url = BASE_URL + "?key=" + apiKey + "&token=" + token + "&name=NewBoard&idOrganization=" + orgId;
        StringEntity entity = new StringEntity("{\"name\": \"NewBoard\", \"idOrganization\": \"" + orgId + "\"}");
        CloseableHttpResponse response = SendRequest.sendRequest(BuildRequest.buildPostRequest(url, entity));

        // For debugging
        String responseContent = SendRequest.getResponseContent(response);
        System.out.println("Response: " + responseContent);

        // Validate response
        CheckResponseIsValid.checkStatusCode(response, 200);
        Board actualBoard = PrepareActualResponse.parseBoardResponse(responseContent);
        Board expectedBoard = PrepareExpectedResponse.createExpectedBoard(actualBoard.getId(), "NewBoard");
        CheckActualVsExpectedResponses.checkBoard(actualBoard, expectedBoard);

        boardId = actualBoard.getId(); // Save the board ID
        System.out.println("Created Board ID: " + boardId);
    }

    @Test(dependsOnMethods = {"testCreateBoard"})
    public void testGetBoardById() throws IOException {
        String url = BASE_URL + boardId + "?key=" + apiKey + "&token=" + token;
        System.out.println("Getting Board with URL: " + url);
        CloseableHttpResponse response = SendRequest.sendRequest(BuildRequest.buildGetRequest(url));

        // Store and log response content
        String responseContent = SendRequest.getResponseContent(response);
        System.out.println("Response Status: " + response.getStatusLine().getStatusCode());
        System.out.println("Response Content: " + responseContent);

        // Validate response
        CheckResponseIsValid.checkStatusCode(response, 200);
        Board actualBoard = PrepareActualResponse.parseBoardResponse(responseContent);
        Assert.assertNotNull(actualBoard);
    }

    @Test(dependsOnMethods = {"testCreateBoard"})
    public void testUpdateBoard() throws IOException, InterruptedException {
        String url = BASE_URL + boardId + "?key=" + apiKey + "&token=" + token;
        StringEntity entity = new StringEntity("{\"name\": \"UpdatedBoard\"}", "UTF-8");
        entity.setContentType("application/json");

        // Build request
        CloseableHttpResponse response = SendRequest.sendRequest(BuildRequest.buildPutRequest(url, entity));

        // Print response details
        String responseContent = SendRequest.getResponseContent(response);
        System.out.println("Update Response: " + responseContent);

        // Assert the update was successful
        CheckResponseIsValid.checkStatusCode(response, 200);

        // Fetch the updated board to verify the name change
        CloseableHttpResponse getResponse = SendRequest.sendRequest(BuildRequest.buildGetRequest(url));
        String updatedResponseContent = SendRequest.getResponseContent(getResponse);
        System.out.println("Fetched Updated Board: " + updatedResponseContent); // Log updated board content for debugging

        Board updatedBoard = PrepareActualResponse.parseBoardResponse(updatedResponseContent);
        Board expectedBoard = PrepareExpectedResponse.createExpectedBoard(updatedBoard.getId(), "UpdatedBoard");
        CheckActualVsExpectedResponses.checkBoard(updatedBoard, expectedBoard);
    }

    @Test(dependsOnMethods = {"testCreateBoard", "testGetBoardById", "testUpdateBoard"})
    public void testDeleteBoard() throws IOException {
        String url = BASE_URL + boardId + "?key=" + apiKey + "&token=" + token;
        CloseableHttpResponse response = SendRequest.sendRequest(BuildRequest.buildDeleteRequest(url));
        CheckResponseIsValid.checkStatusCode(response, 200);
    }
}
