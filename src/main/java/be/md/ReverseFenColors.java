package be.md;

public class ReverseFenColors {
    // Method to flip a FEN string vertically
    public static String flipFenVertically(String fen, boolean whiteOnTop) {
        ChessPosition position = new ChessPosition(fen);
        if (whiteOnTop) {
            return position.mirrorAndFlip().getFen();
        } else {
            return position.getFen();
        }
    }

    public static void main(String[] args) {
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        System.out.println("Original FEN: " + fen);

        // Example with white on top
        System.out.println("Flipped FEN (white on top): " + flipFenVertically(fen, true));

        // Example with white on bottom
        System.out.println("Flipped FEN (white on bottom): " + flipFenVertically(fen, false));
    }
}
