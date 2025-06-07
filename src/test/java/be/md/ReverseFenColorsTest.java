package be.md;

import org.junit.jupiter.api.Test;

import static be.md.ReverseFenColors.flipFenVertically;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ReverseFenColorsTest {


    @Test
    void testFlipFenVerticallyWhiteOnTop() {
        String initialFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        String expectedFlippedFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1";

        // Call the method to flip the FEN with white on top
        String actualFlippedFen = flipFenVertically(initialFen, true);

        // Assert that the flipped FEN matches the expected FEN
        assertEquals(expectedFlippedFen, actualFlippedFen, "The FEN string was not flipped correctly with white on top.");
    }

    @Test
    void testFlipFenVerticallyWhiteOnBottom() {
        String initialFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        String expectedFlippedFen = "RNBQKBNR/PPPPPPPP/8/8/8/8/pppppppp/rnbqkbnr b KQkq - 0 1";

        // Call the method to flip the FEN with white on bottom
        String actualFlippedFen = flipFenVertically(initialFen, false);

        // Assert that the flipped FEN matches the expected FEN
        assertEquals(expectedFlippedFen, actualFlippedFen, "The FEN string was not flipped correctly with white on bottom.");
    }

}
