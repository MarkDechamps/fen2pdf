package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.util.ObjectUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
            addImagesToPDF(document, images,3,5);
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

    public static void addImagesToPDF(PDDocument document, List<String> imagePaths, int imagesPerRow, int spacing) throws IOException {
        int imagesPerPage = imagesPerRow * imagesPerRow;
        int numRows = (int) Math.ceil((double) imagePaths.size() / imagesPerPage);

        for (int pageIdx = 0; pageIdx < numRows; pageIdx++) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Add title
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, page.getMediaBox().getHeight() - 50);
                contentStream.showText("Chessboard Images");
                contentStream.endText();

                // Add page number
                contentStream.beginText();
                contentStream.newLineAtOffset(page.getMediaBox().getWidth() - 100, page.getMediaBox().getHeight() - 50);
                contentStream.showText("Page " + (pageIdx + 1));
                contentStream.endText();

                int rowStartIdx = pageIdx * imagesPerPage;
                int rowEndIdx = Math.min(rowStartIdx + imagesPerPage, imagePaths.size());

                // Calculate maximum width and height for an image based on the number of images per row and the spacing
                float availableWidth = page.getMediaBox().getWidth() - (imagesPerRow + 1) * spacing;
                float availableHeight = page.getMediaBox().getHeight() - (imagesPerRow + 1) * spacing;
                float maxWidth = availableWidth / imagesPerRow;
                float maxHeight = availableHeight / imagesPerRow;

                for (int i = rowStartIdx; i < rowEndIdx; i++) {
                    String imagePath = imagePaths.get(i);
                    BufferedImage bufferedImage = ImageIO.read(new File(imagePath));
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage, "png", output);
                    PDImageXObject image = PDImageXObject.createFromByteArray(document, output.toByteArray(), "image");

                    // Calculate scaling factor to fit within the maximum dimensions while preserving aspect ratio
                    float scale = Math.min(maxWidth / image.getWidth(), maxHeight / image.getHeight());

                    // Calculate image dimensions after scaling
                    float width = image.getWidth() * scale;
                    float height = image.getHeight() * scale;

                    // Calculate image position on the page, considering the spacing
                    int row = i / imagesPerRow;
                    int col = i % imagesPerRow;
                    float x = spacing + col * (maxWidth + spacing);
                    float y = spacing + row * (maxHeight + spacing);

                    // Draw the scaled image on the page
                    contentStream.drawImage(image, x, y, width, height);
                }
            }
        }
    }
}
