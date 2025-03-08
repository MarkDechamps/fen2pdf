package be.md;

import org.junit.jupiter.api.Test;

import static be.md.FenToText.fenToText;
import static org.junit.jupiter.api.Assertions.*;

class FenToTextTest {
    @Test
    public void shouldConvertFENToDutchString(){
        String expected = """
                Wit:
                Pionnen: a2,b2,c2,d2,e2,f2,g2,h2
                Stukken: Ta1,Pb1,Lc1,Dd1,Ke1,Lf1,Pg1,Th1
                                
                Zwart:
                Pionnen: a7,b7,c7,d7,e7,f7,g7,h7
                Stukken Ta8,Pb8,Lc8,Dd8,Ke8,Lf8,Pg8,Th8
                                
                Wit aan zet.""";
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        String result = fenToText(fen,SupportedLanguage.nl);
        assertEquals(expected,result);
    }
    @Test
    public void shouldConvertFENToEnglishString(){
        String expected = """
                White:
                Pawns: a2,b2,c2,d2,e2,f2,g2,h2
                Pieces: Ra1,Nb1,Bc1,Qd1,Ke1,Bf1,Ng1,Rh1
                            
                Black:
                Pawns: a7,b7,c7,d7,e7,f7,g7,h7
                Pieces Ra8,Nb8,Bc8,Qd8,Ke8,Bf8,Ng8,Rh8
                            
                White to move.""";
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        String result = fenToText(fen,SupportedLanguage.en);
        assertEquals(expected,result);
    }
    @Test
    public void shouldConvertFENToFrenchString(){
        String expected = """
                Blanc:
                Pions: a2,b2,c2,d2,e2,f2,g2,h2
                Pièces: Ta1,Cb1,Fc1,Dd1,Re1,Ff1,Cg1,Th1
                                   
                Noir:
                Pions: a7,b7,c7,d7,e7,f7,g7,h7
                Pièces Ta8,Cb8,Fc8,Dd8,Re8,Ff8,Cg8,Th8
                                   
                Blanc à jouer.""";
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        String result = fenToText(fen,SupportedLanguage.fr);
        assertEquals(expected,result);
    }
}