package be.md;

import static be.md.MirrorFenVertically.mirrorFenVertically;

public record Fen(String position) {
    public Fen mirrorVertically() {
        return new Fen(mirrorFenVertically(position));
    }

    public String escapeFen() {
            return position.replaceAll(" ", "%20");
    }
}
