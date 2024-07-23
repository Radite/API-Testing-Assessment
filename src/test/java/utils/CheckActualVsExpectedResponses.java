package utils;

import models.Board;
import org.testng.Assert;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckActualVsExpectedResponses {

    public static void checkBoard(Board actualBoard, Board expectedBoard) {
        Assert.assertEquals(actualBoard.getName(), expectedBoard.getName(), "Board name mismatch");
        assertThat(actualBoard.getName()).isEqualTo(expectedBoard.getName());
    }
}
