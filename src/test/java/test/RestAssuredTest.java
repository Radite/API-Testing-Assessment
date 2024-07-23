package test;

import base.BaseTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.Board;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.CheckResponseIsValid;
import utils.PrepareActualResponse;
import utils.PrepareExpectedResponse;
import utils.CheckActualVsExpectedResponses;

import java.io.IOException;

import static io.restassured.RestAssured.given;

public class RestAssuredTest extends BaseTest {

    private static final String BASE_URL = "https://api.trello.com/1/boards/";

    private static String boardId;
    private static String apiKey;
    private static String token;

    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        apiKey = System.getenv("API_KEY");
        token = System.getenv("TOKEN");

        if (apiKey == null || token == null) {
            throw new IllegalArgumentException("API_KEY or TOKEN environment variables are not set.");
        }
    }

    @Test
    public void testCreateBoard() throws IOException {
        Response response = given()
                .queryParam("key", apiKey)
                .queryParam("token", token)
                .queryParam("name", "NewBoard")
                .header("Content-Type", "application/json")
                .post();
        CheckResponseIsValid.checkStatusCode(response, 200);

        Board actualBoard = PrepareActualResponse.parseBoardResponse(response.asString());
        Board expectedBoard = PrepareExpectedResponse.createExpectedBoard(actualBoard.getId(), "NewBoard");

        CheckActualVsExpectedResponses.checkBoard(actualBoard, expectedBoard);
        boardId = actualBoard.getId(); // Save the board ID
    }

    @Test(dependsOnMethods = {"testCreateBoard"})
    public void testGetBoardById() throws IOException {
        Response response = given()
                .queryParam("key", apiKey)
                .queryParam("token", token)
                .header("Content-Type", "application/json")
                .get(boardId);
        CheckResponseIsValid.checkStatusCode(response, 200);

        Board actualBoard = PrepareActualResponse.parseBoardResponse(response.asString());
        Board expectedBoard = PrepareExpectedResponse.createExpectedBoard(boardId, "NewBoard");

        CheckActualVsExpectedResponses.checkBoard(actualBoard, expectedBoard);
    }

    @Test(dependsOnMethods = {"testCreateBoard"})
    public void testUpdateBoard() throws IOException {
        Response response = given()
                .queryParam("key", apiKey)
                .queryParam("token", token)
                .queryParam("name", "UpdatedBoard")
                .header("Content-Type", "application/json")
                .put(boardId);
        CheckResponseIsValid.checkStatusCode(response, 200);

        Board actualBoard = PrepareActualResponse.parseBoardResponse(response.asString());
        Board expectedBoard = PrepareExpectedResponse.createExpectedBoard(boardId, "UpdatedBoard");

        CheckActualVsExpectedResponses.checkBoard(actualBoard, expectedBoard);
    }

    @Test(dependsOnMethods = {"testCreateBoard", "testGetBoardById", "testUpdateBoard"})
    public void testDeleteBoard() {
        Response response = given()
                .queryParam("key", apiKey)
                .queryParam("token", token)
                .header("Content-Type", "application/json")
                .delete(boardId);
        CheckResponseIsValid.checkStatusCode(response, 200);
    }
}
