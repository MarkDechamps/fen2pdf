package be.md;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Rank;
import com.github.bhlangonijr.chesslib.File;
import com.github.bhlangonijr.chesslib.CastleRight;

public class ChessPosition {
    private final Board board;

    public ChessPosition(String fen) {
        this.board = new Board();
        this.board.loadFromFen(fen);
    }

    public String getFen() {
        return board.getFen();
    }

    public ChessPosition mirrorAndFlip() {
        Board mirroredBoard = new Board();
        mirroredBoard.clear(); // Clear the board before setting new pieces
        
        // Mirror the board
        for (Square square : Square.values()) {
            if (square == Square.NONE) continue; // Skip the NONE square
            
            // Get original position
            File originalFile = square.getFile();
            Rank originalRank = square.getRank();
            
            // Calculate mirrored position
            File mirroredFile = File.values()[7 - originalFile.ordinal()];
            Rank mirroredRank = Rank.values()[7 - originalRank.ordinal()];
            Square mirroredSquare = Square.encode(mirroredRank, mirroredFile);
            
            // Get and set the piece with flipped case
            Piece piece = board.getPiece(square);
            if (piece != Piece.NONE) {
                // Flip the piece's side
                Side originalSide = piece.getPieceSide();
                Side flippedSide = originalSide == Side.WHITE ? Side.BLACK : Side.WHITE;
                Piece flippedPiece = Piece.make(flippedSide, piece.getPieceType());
                mirroredBoard.setPiece(flippedPiece, mirroredSquare);
            }
        }
        
        // Flip the side to move
        mirroredBoard.setSideToMove(board.getSideToMove() == Side.WHITE ? Side.BLACK : Side.WHITE);
        
        // Handle castling rights
        String castlingRights = board.getCastleRight(Side.WHITE).toString() + board.getCastleRight(Side.BLACK).toString();
        if (!castlingRights.equals("-")) {
            if (castlingRights.contains("K")) mirroredBoard.getCastleRight().put(Side.WHITE, CastleRight.KING_SIDE);
            if (castlingRights.contains("Q")) mirroredBoard.getCastleRight().put(Side.WHITE, CastleRight.QUEEN_SIDE);
            if (castlingRights.contains("k")) mirroredBoard.getCastleRight().put(Side.BLACK, CastleRight.KING_SIDE);
            if (castlingRights.contains("q")) mirroredBoard.getCastleRight().put(Side.BLACK, CastleRight.QUEEN_SIDE);
        }
        
        // Handle en passant square
        if (board.getEnPassant() != Square.NONE) {
            File originalFile = board.getEnPassant().getFile();
            Rank originalRank = board.getEnPassant().getRank();
            File mirroredFile = File.values()[7 - originalFile.ordinal()];
            Rank mirroredRank = Rank.values()[7 - originalRank.ordinal()];
            mirroredBoard.setEnPassant(Square.encode(mirroredRank, mirroredFile));
        }
        
        // Copy halfmove counter and fullmove number
        mirroredBoard.setHalfMoveCounter(board.getHalfMoveCounter());
        mirroredBoard.setMoveCounter(board.getMoveCounter());
        
        return new ChessPosition(mirroredBoard.getFen());
    }

    public String getTextDescription(SupportedLanguage language) {
        StringBuilder description = new StringBuilder();
        String whiteLabel = language == SupportedLanguage.nl ? "Wit" : 
                          language == SupportedLanguage.fr ? "Blanc" : "White";
        String blackLabel = language == SupportedLanguage.nl ? "Zwart" : 
                          language == SupportedLanguage.fr ? "Noir" : "Black";
        String pawnsLabel = language == SupportedLanguage.nl ? "Pionnen" : 
                          language == SupportedLanguage.fr ? "Pions" : "Pawns";
        String piecesLabel = language == SupportedLanguage.nl ? "Stukken" : 
                           language == SupportedLanguage.fr ? "Pièces" : "Pieces";
        String toMoveLabel = language == SupportedLanguage.nl ? "aan zet" : 
                           language == SupportedLanguage.fr ? "à jouer" : "to move";

        // Get white pieces
        description.append(whiteLabel).append(":\n");
        description.append(pawnsLabel).append(": ");
        description.append(getPawnPositions(Side.WHITE, language)).append("\n");
        description.append(piecesLabel).append(": ");
        description.append(getPiecePositions(Side.WHITE, language)).append("\n\n");

        // Get black pieces
        description.append(blackLabel).append(":\n");
        description.append(pawnsLabel).append(": ");
        description.append(getPawnPositions(Side.BLACK, language)).append("\n");
        description.append(piecesLabel).append(" ");
        description.append(getPiecePositions(Side.BLACK, language)).append("\n\n");

        // Add side to move
        description.append(board.getSideToMove() == Side.WHITE ? whiteLabel : blackLabel)
                  .append(" ").append(toMoveLabel).append(".");

        return description.toString();
    }

    private String getPawnPositions(Side side, SupportedLanguage language) {
        StringBuilder positions = new StringBuilder();
        boolean first = true;
        
        for (Square square : Square.values()) {
            if (board.getPiece(square).getPieceSide() == side && 
                board.getPiece(square).getPieceType() == com.github.bhlangonijr.chesslib.PieceType.PAWN) {
                if (!first) {
                    positions.append(",");
                }
                positions.append(square.toString().toLowerCase());
                first = false;
            }
        }
        
        return positions.toString();
    }

    private String getPiecePositions(Side side, SupportedLanguage language) {
        StringBuilder positions = new StringBuilder();
        boolean first = true;
        
        for (Square square : Square.values()) {
            if (board.getPiece(square).getPieceSide() == side && 
                board.getPiece(square).getPieceType() != com.github.bhlangonijr.chesslib.PieceType.PAWN) {
                if (!first) {
                    positions.append(",");
                }
                positions.append(getPieceSymbol(board.getPiece(square), language))
                        .append(square.toString().toLowerCase());
                first = false;
            }
        }
        
        return positions.toString();
    }

    private String getPieceSymbol(com.github.bhlangonijr.chesslib.Piece piece, SupportedLanguage language) {
        if (piece.getPieceSide() == Side.WHITE) {
            switch (piece.getPieceType()) {
                case KING: return "K";
                case QUEEN: return language == SupportedLanguage.nl || language == SupportedLanguage.fr ? "D" : "Q";
                case ROOK: return language == SupportedLanguage.nl || language == SupportedLanguage.fr ? "T" : "R";
                case BISHOP: return language == SupportedLanguage.nl ? "L" : language == SupportedLanguage.fr ? "F" : "B";
                case KNIGHT: return language == SupportedLanguage.nl ? "P" : language == SupportedLanguage.fr ? "C" : "N";
                default: return "";
            }
        } else {
            switch (piece.getPieceType()) {
                case KING: return "K";
                case QUEEN: return language == SupportedLanguage.nl || language == SupportedLanguage.fr ? "D" : "Q";
                case ROOK: return language == SupportedLanguage.nl || language == SupportedLanguage.fr ? "T" : "R";
                case BISHOP: return language == SupportedLanguage.nl ? "L" : language == SupportedLanguage.fr ? "F" : "B";
                case KNIGHT: return language == SupportedLanguage.nl ? "P" : language == SupportedLanguage.fr ? "C" : "N";
                default: return "";
            }
        }
    }
} 