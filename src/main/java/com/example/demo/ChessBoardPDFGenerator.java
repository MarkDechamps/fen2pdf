package com.example.demo;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ChessBoardPDFGenerator {

    public static void main(String[] args) {
        var fens =PGNFileReader.loadPgn("src/main/resources/static/example.pgn");

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
        String apiUrl = "https://fen2image.chessvision.ai/" + fen;

        // Create connection
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Read response
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            // Response contains the image URL
            String imageUrl = response.toString();
            System.out.println("Image URL: " + imageUrl);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void downloadImage(String imageUrl, String fileName) throws IOException {
        // Open a URL Stream
        try (InputStream in = new URL(imageUrl).openStream()) {
            Files.copy(in, Paths.get(fileName));
        }
    }
}
