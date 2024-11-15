package be.md;

import org.junit.jupiter.api.Test;

import static be.md.MirrorFenVertically.mirrorFenVertically;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MirrorFenVerticallyTest {
    @Test
    public void shouldMirrorAndFlip() {
        var fen = "r3k2r/pppqppbp/2n2np1/4P3/3P4/2N2NP1/PPPQPPBP/R3K2R w KQkq - 0 1";

        var maf = "r3k2r/" +
                        "pppqppbp/" +
                        "2n2np1/" +
                        "3p4/" +
                        "4p3/" +
                        "2N2NP1/" +
                        "PPPQPPBP/" +
                        "R3K2R" +
                        " b KQkq - 0 1";

        assertEquals(new Fen(maf), new Fen(fen).mirrorAndFlip());
    }
    @Test
    public void shouldMirror1Line() {
        var fen = "r3k2r w KQkq - 0 1";
        var maf = "R3K2R b KQkq - 0 1";
        assertEquals(new Fen(maf), new Fen(fen).mirrorAndFlip());
    }
    @Test
    public void shouldMirrorMultipleLines() {
        var fen = new Fen("r3k2r/pppqppbp/2n2np1/4P3/3P4/2N2NP1/PPPQPPBP/R3K2R w KQkq - 0 1");
        var maf = new Fen("r3k2r/" +
                "pppqppbp/" +
                "2n2np1/" +
                "3p4/" +
                "4p3/" +
                "2N2NP1/" +
                "PPPQPPBP/" +
                "R3K2R" +
                " b KQkq - 0 1");

        assertEquals(maf, fen.mirrorAndFlip());
    }
}