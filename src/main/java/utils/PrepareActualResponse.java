package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.Board;

import java.io.IOException;

public class PrepareActualResponse {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Board parseBoardResponse(String responseContent) throws IOException {
        return objectMapper.readValue(responseContent, Board.class);
    }
}
