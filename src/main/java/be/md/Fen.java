package be.md;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public record Fen(String position) {

    public String escapeFen() {
        return position.replaceAll(" ", "%20");
    }

    public Fen mirrorAndFlip2() {
        ChessPosition chessPosition = new ChessPosition(position);
        return new Fen(chessPosition.mirrorAndFlip().getFen());
    }

    public Fen mirrorAndFlip() {
        var lines = position.split(" ");
        var pieces = lines[0];
        var rest = toggleMetadata(position.substring(pieces.length()));

        String[] flippedBoard = this.rotate(pieces.split("/"));
        var newPosition = String.join("/", flippedBoard);

        return new Fen(toggleString(newPosition) + rest);
    }

    private String[] rotate(String[] split) {
        List<String> list = Arrays.asList(split);
        Collections.reverse(list);
        return list.toArray(new String[0]);
    }

    private String toggleMetadata(String metaInfo) {
        return metaInfo
                .replaceAll("w", "t_")
                .replaceAll("W", "t_")
                .replaceAll("b", "w")
                .replaceAll("B", "w")
                .replaceAll("t_", "b");

    }

    private String toggleString(String toConvert) {
        return toConvert.chars()
                .mapToObj(c -> (char) toggleCase((char) c))
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    public char toggleCase(char c) {
        if (Character.isLowerCase(c)) {
            return Character.toUpperCase(c);
        } else if (Character.isUpperCase(c)) {
            return Character.toLowerCase(c);
        }
        return c;
    }
}

