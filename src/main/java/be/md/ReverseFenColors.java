package be.md;

public class ReverseFenColors {
    // Method to flip a FEN string vertically
    public static String flipFenVertically(String fen, boolean whiteOnTop) {
        String[] parts = fen.split(" ");
        String[] rows = parts[0].split("/");

        StringBuilder flippedFen = new StringBuilder();

        // Reverse the rows and swap the piece colors based on the whiteOnTop flag
        for (int i = 0; i < rows.length; i++) {
            int index = whiteOnTop ? (rows.length - 1 - i) : i; // Adjust index based on the whiteOnTop flag
            String row = rows[index];

            // Swap the piece colors: lowercase becomes uppercase and vice versa
            StringBuilder swappedRow = new StringBuilder();
            for (char piece : row.toCharArray()) {
                if (Character.isLowerCase(piece)) {
                    swappedRow.append(Character.toUpperCase(piece)); // Swap black to white
                } else if (Character.isUpperCase(piece)) {
                    swappedRow.append(Character.toLowerCase(piece)); // Swap white to black
                } else {
                    swappedRow.append(piece); // Keep numbers as is
                }
            }
            flippedFen.append(swappedRow).append("/");
        }

        // Remove the last '/'
        flippedFen.deleteCharAt(flippedFen.length() - 1);

        // Flip the turn: 'w' -> 'b', 'b' -> 'w'
        String flippedTurn = parts[1].equals("w") ? "b" : "w";

        // Reconstruct the FEN string with flipped board, turn, and keep other details unchanged
        return flippedFen.toString() + " " + flippedTurn + " " + parts[2] + " " + parts[3] + " " + parts[4] + " " + parts[5];
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
