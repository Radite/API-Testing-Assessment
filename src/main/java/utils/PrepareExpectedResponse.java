package utils;

import models.Board;

public class PrepareExpectedResponse {

    public static Board createExpectedBoard(String id, String name) {
        Board board = new Board();
        board.setId(id);
        board.setName(name);
        return board;
    }
}
