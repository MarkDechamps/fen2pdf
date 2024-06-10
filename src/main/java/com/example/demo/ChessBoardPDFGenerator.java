package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
public class ChessBoardPDFGenerator {
    public static void main(String[] args) {
        List<String> fens ;
        if(args.length > 0) {
            log.info(Arrays.toString(args));
            fens = PGNFileReader.loadPgn(args[0]);
        }else{
            log.info("Please provide an argument to a PGN file with FEN diagrams in it.");
            log.info("None provided. Falling back to demo file.");
            fens = PGNFileReader.loadPgn("src/main/resources/static/example.pgn");
        }


        try (PDDocument document = new PDDocument()) {
            var images = fens.stream().map(ChessBoardPDFGenerator::generateChessBoardImage).toList();
            addImagesToPDF(document, images);
            document.save("chessboard_images.pdf");
            System.out.println("PDF saved successfully : chessboard_images.pdf");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String generateChessBoardImage(String fen) {
        // ChessVision API endpoint for FEN to Image conversion
        String apiUrl = "https://fen2image.chessvision.ai/" + fen.replaceAll(" ", "%20");
        String fileName = "./" + UUID.randomUUID() + ".png";
        try {
            return downloadImage(apiUrl, fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String downloadImage(String imageUrl, String fileName) throws IOException {
        Path tempDir = Files.createTempDirectory("chessboard_images");

        String randomFileName = UUID.randomUUID() + ".png";
        Path target = tempDir.resolve(randomFileName);

        // Open a URL Stream and save the image to the temporary directory
        try (InputStream in = new URL(imageUrl).openStream()) {
            Files.copy(in, target);
            log.info("Saved " + target);
        }
        return target.toFile().getAbsolutePath();
    }

    public static void addImagesToPDF(PDDocument document, List<String> imagePaths) throws IOException {
        int imagesPerPage = 9; // 3 rows * 3 columns
        int numRows = (int) Math.ceil((double) imagePaths.size() / imagesPerPage);

        for (int pageIdx = 0; pageIdx < numRows; pageIdx++) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                int rowStartIdx = pageIdx * imagesPerPage;
                int rowEndIdx = Math.min(rowStartIdx + imagesPerPage, imagePaths.size());

                for (int i = rowStartIdx; i < rowEndIdx; i++) {
                    String imagePath = imagePaths.get(i);
                    PDImageXObject image = PDImageXObject.createFromFile(imagePath, document);
                    int row = (i - rowStartIdx) / 3; // 3 columns per row
                    int col = (i - rowStartIdx) % 3;
                    float x = 50 + col * 200; // Adjust x-coordinate based on column index
                    float y = 600 - row * 200; // Adjust y-coordinate based on row index
                    contentStream.drawImage(image, x, y, image.getWidth(), image.getHeight());
                }
            }
        }
    }
}
