package be.md;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PGNFileReader {

    public static List<Fen> loadPgn(Path filePath) {
        try {
            // Read file content and remove BOM if present
            String content = new String(java.nio.file.Files.readAllBytes(filePath), java.nio.charset.StandardCharsets.UTF_8);
            if (content.startsWith("\uFEFF")) {
                content = content.substring(1);
            }
            
            // Write content back to a temporary file without BOM
            Path tempFile = java.nio.file.Files.createTempFile("pgn", ".pgn");
            java.nio.file.Files.write(tempFile, content.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            
            try {
                PgnHolder pgnHolder = new PgnHolder(tempFile.toString());
                pgnHolder.loadPgn();
                
                List<Fen> fenPositions = new ArrayList<>();
                
                for (Game game : pgnHolder.getGames()) {
                    // Add initial FEN if present
                    if (game.getFen() != null) {
                        fenPositions.add(new Fen(game.getFen()));
                    }
                    
                    // Play through the moves and look for {[#]} annotations
                    Board board = new Board();
                    if (game.getFen() != null) {
                        board.loadFromFen(game.getFen());
                    }
                    
                    AtomicInteger moveNumber = new AtomicInteger(0);
                    game.getHalfMoves().forEach(move -> {
                        // Check if the move has the {[#]} annotation
                        String comment = game.getCommentary().get(moveNumber.get());
                        if (comment != null && comment.contains("[#]")) {
                            fenPositions.add(new Fen(board.getFen()));
                        }
                        board.doMove(move);
                        moveNumber.incrementAndGet();
                    });
                }
                
                return fenPositions;
            } finally {
                // Clean up temporary file
                java.nio.file.Files.deleteIfExists(tempFile);
            }
        } catch (Exception e) {
            System.err.println("Error processing PGN file: " + e.getMessage());
            return List.of();
        }
    }
}
