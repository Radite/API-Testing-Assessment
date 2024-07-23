package test;

import base.BaseTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Board;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import utils.CheckResponseIsValid;
import utils.PrepareActualResponse;
import utils.PrepareExpectedResponse;
import utils.CheckActualVsExpectedResponses;

import java.io.IOException;

public class RetrofitTest extends BaseTest {

    private static final String BASE_URL = "https://api.trello.com/1/boards/";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private OkHttpClient client;
    private Retrofit retrofit;

    private static String boardId;
    private static String apiKey;
    private static String token;

    @BeforeClass
    public void setUpRetrofit() {
        client = new OkHttpClient.Builder().build();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();

        apiKey = System.getenv("API_KEY");
        token = System.getenv("TOKEN");

        if (apiKey == null || token == null) {
            throw new IllegalArgumentException("API_KEY or TOKEN environment variables are not set.");
        }
    }

    @Test
    public void testCreateBoard() throws IOException {
        RequestBody body = RequestBody.create("{\"name\": \"NewBoard\"}", okhttp3.MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(BASE_URL + "?key=" + apiKey + "&token=" + token)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        CheckResponseIsValid.checkStatusCode(response, 200);

        Board actualBoard = PrepareActualResponse.parseBoardResponse(response.body().string());
        Board expectedBoard = PrepareExpectedResponse.createExpectedBoard(actualBoard.getId(), "NewBoard");

        CheckActualVsExpectedResponses.checkBoard(actualBoard, expectedBoard);
        boardId = actualBoard.getId(); // Save the board ID
    }

    @Test(dependsOnMethods = {"testCreateBoard"})
    public void testGetBoardById() throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + boardId + "?key=" + apiKey + "&token=" + token)
                .get()
                .build();
        Response response = client.newCall(request).execute();
        CheckResponseIsValid.checkStatusCode(response, 200);

        Board actualBoard = PrepareActualResponse.parseBoardResponse(response.body().string());
        Board expectedBoard = PrepareExpectedResponse.createExpectedBoard(boardId, "NewBoard");

        CheckActualVsExpectedResponses.checkBoard(actualBoard, expectedBoard);
    }

    @Test(dependsOnMethods = {"testCreateBoard"})
    public void testUpdateBoard() throws IOException {
        RequestBody body = RequestBody.create("{\"name\": \"UpdatedBoard\"}", okhttp3.MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(BASE_URL + boardId + "?key=" + apiKey + "&token=" + token)
                .put(body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        CheckResponseIsValid.checkStatusCode(response, 200);

        Board actualBoard = PrepareActualResponse.parseBoardResponse(response.body().string());
        Board expectedBoard = PrepareExpectedResponse.createExpectedBoard(boardId, "UpdatedBoard");

        CheckActualVsExpectedResponses.checkBoard(actualBoard, expectedBoard);
    }

    @Test(dependsOnMethods = {"testCreateBoard", "testGetBoardById", "testUpdateBoard"})
    public void testDeleteBoard() throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + boardId + "?key=" + apiKey + "&token=" + token)
                .delete()
                .build();
        Response response = client.newCall(request).execute();
        CheckResponseIsValid.checkStatusCode(response, 200);
    }
}
