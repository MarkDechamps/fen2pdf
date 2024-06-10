package com.example.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class ChessBoardPDFGenerator {

    public static void main(String[] args) {
        var fens = PGNFileReader.loadPgn("src/main/resources/static/example.pgn");

        for (String fen : fens) {
            try {
                generateChessBoardImage(fen);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void generateChessBoardImage(String fen) throws IOException {
        // ChessVision API endpoint for FEN to Image conversion
        String apiUrl = "https://fen2image.chessvision.ai/" + fen.replaceAll(" ", "%20");
        downloadImage(apiUrl, "./" + UUID.randomUUID()+".png");
    }

    public static void downloadImage(String imageUrl, String fileName) throws IOException {
        // Open a URL Stream
        try (InputStream in = new URL(imageUrl).openStream()) {
            Path target = Paths.get(fileName);
            Files.copy(in, target);
            System.out.println("Saved " + target);
        }
    }
}
