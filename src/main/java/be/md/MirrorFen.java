package be.md;

public class MirrorFen {
    public static String mirrorFenVertically(String fen) {
        String[] parts = fen.split(" ");
        String[] rows = parts[0].split("/");

        StringBuilder mirroredFen = new StringBuilder();
        for (String row : rows) {
            mirroredFen.append(mirrorRow(row)).append("/");
        }

        // Remove the last '/'
        mirroredFen.deleteCharAt(mirroredFen.length() - 1);

        // Reconstruct the FEN with the unchanged parts (turn, castling rights, etc.)
        StringBuilder resultFen = new StringBuilder(mirroredFen);
        for (int i = 1; i < parts.length; i++) {
            resultFen.append(" ").append(parts[i]);
        }

        return resultFen.toString();
    }

    // Helper method to mirror a single row of the chessboard
    private static String mirrorRow(String row) {
        StringBuilder mirroredRow = new StringBuilder();

        // Traverse the row from left to right and append mirrored characters
        for (char c : row.toCharArray()) {
            if (Character.isDigit(c)) {
                mirroredRow.append(c); // Append empty squares as they are
            } else {
                mirroredRow.append(c); // Pieces remain unchanged
            }
        }

        // Reverse the row to achieve the vertical mirroring
        return mirroredRow.reverse().toString();
    }
}
