package be.md;
import java.util.*;

public class FenToText {

        public static String fenToText(String fen, SupportedLanguage lang) {
            ChessPosition position = new ChessPosition(fen);
            return position.getTextDescription(lang);
        }

    record Translation(String lang, Map<Character, String> pieceNames, String white, String black, String pawns, String pieces,String toMove){
    }

    private static Translation translation(SupportedLanguage lang) {

        switch (lang) {
            case nl :
                return new Translation("nl",dutchPieces(),"Wit","Zwart","Pionnen","Stukken","aan zet");
            case en :
                return new Translation("en",englishPieces(),"White","Black","Pawns","Pieces","to move");
            case fr :
                return new Translation("fr", frenchPieces(), "Blanc", "Noir", "Pions", "Pièces", "à jouer");
            default:
                throw new RuntimeException("Language not supported " + lang);
        }
    }
    private static Map<Character, String> frenchPieces() {
        Map<Character, String> pieceNames = new HashMap<>();
        pieceNames.put('K', "R");
        pieceNames.put('Q', "D");
        pieceNames.put('R', "T");
        pieceNames.put('B', "F");
        pieceNames.put('N', "C");
        pieceNames.put('P', "p");
        pieceNames.put('k', "R");
        pieceNames.put('q', "D");
        pieceNames.put('r', "T");
        pieceNames.put('b', "F");
        pieceNames.put('n', "C");
        pieceNames.put('p', "p");
        return pieceNames;
    }
    private static Map<Character, String> englishPieces() {
        Map<Character, String> pieceNames = new HashMap<>();
        pieceNames.put('K', "K");
        pieceNames.put('Q', "Q");
        pieceNames.put('R', "R");
        pieceNames.put('B', "B");
        pieceNames.put('N', "N");
        pieceNames.put('P', "p");
        pieceNames.put('k', "K");
        pieceNames.put('q', "Q");
        pieceNames.put('r', "R");
        pieceNames.put('b', "B");
        pieceNames.put('n', "N");
        pieceNames.put('p', "p");
        return pieceNames;
    }
    private static Map<Character, String> dutchPieces() {
        Map<Character, String> pieceNames = new HashMap<>();
        pieceNames.put('K', "K");
        pieceNames.put('Q', "D");
        pieceNames.put('R', "T");
        pieceNames.put('B', "L");
        pieceNames.put('N', "P");
        pieceNames.put('P', "p");
        pieceNames.put('k', "K");
        pieceNames.put('q', "D");
        pieceNames.put('r', "T");
        pieceNames.put('b', "L");
        pieceNames.put('n', "P");
        pieceNames.put('p', "p");
        return pieceNames;
    }

    public static void main(String[] args) {
            String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
            System.out.println(fenToText(fen,SupportedLanguage.nl));
        }
}

