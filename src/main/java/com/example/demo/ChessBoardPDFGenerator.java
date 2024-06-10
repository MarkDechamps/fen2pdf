package com.example.demo;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

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

//    public static void main(String[] args) {
//        var fens = PGNFileReader.loadPgn("src/main/resources/static/example.pgn");
//
//        for (String fen : fens) {
//            try {
//                generateChessBoardImage(fen);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
    public static void main(String[] args) {
        var fens = PGNFileReader.loadPgn("src/main/resources/static/example.pgn");

        try (PDDocument document = new PDDocument()) {
            for (String fen : fens) {
                String imageUrl = generateChessBoardImage(fen);
                addImageToPDF(document, imageUrl);
            }
            document.save("chessboard_images.pdf");
            System.out.println("PDF saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String generateChessBoardImage(String fen) throws IOException {
        // ChessVision API endpoint for FEN to Image conversion
        String apiUrl = "https://fen2image.chessvision.ai/" + fen.replaceAll(" ", "%20");
        String fileName = "./" + UUID.randomUUID() + ".png";
        downloadImage(apiUrl, fileName);
        return fileName;
    }

    public static void downloadImage(String imageUrl, String fileName) throws IOException {
        // Open a URL Stream
        try (InputStream in = new URL(imageUrl).openStream()) {
            Path target = Paths.get(fileName);
            Files.copy(in, target);
            System.out.println("Saved " + target);
        }
    }
    public static void addImageToPDF(PDDocument document, String imagePath) throws IOException {
        PDPage page = new PDPage();
        document.addPage(page);
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            PDImageXObject image = PDImageXObject.createFromFile(imagePath, document);
            contentStream.drawImage(image, 50, 600, image.getWidth(), image.getHeight());
        }
    }
}
