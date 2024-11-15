package be.md;

public class MirrorFenVertically {
    public static String mirrorFenVertically(String fen) {
        String[] parts = fen.split(" ");
        String[] rows = parts[0].split("/");

        var mirroredFen = new StringBuilder();
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
        for (char c : row.toCharArray()) {
            if (Character.isDigit(c)) {
                mirroredRow.append(c);
            } else {
                mirroredRow.append(c);
            }
        }
        return mirroredRow.reverse().toString();
    }
}
