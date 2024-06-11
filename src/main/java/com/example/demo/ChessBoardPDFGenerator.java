package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

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
        log.info("Thanks for using FEN2PDF!");
        log.info("Created by Mark Dechamps");
        log.info("(B)eer licensed 2024");
        log.info("Usage: provide 'pgnfile' 'title' as 2 arguments. We'll look for line sstarting with '[FEN' anduse chessvision to generate images. ");

        var pgns = PgnFileLister.listPgnFilesInCurrentDirectory();

        if (!pgns.isEmpty()) {
            log.info("Arguments found:" + Arrays.toString(pgns.toArray()));
            pgns.forEach(pgn -> {
                log.info("Processing " + pgn);
                var parsedPgn = PGNFileReader.loadPgn(pgn);
                createPdfFileWithDiagramsFrom(pgn.toFile().getName(), parsedPgn);
            });
        } else {
            log.info("Please put a pgn file in the same folder as this program.");
            log.info("No pgn found in {}. Exiting.", new File(".").getAbsolutePath());
            System.exit(1);
        }
    }

    private static void createPdfFileWithDiagramsFrom(String title, List<String> fens) {
        log.info("Title used: " + title);
        log.info("FENS found:" + fens.size());
        try (PDDocument document = new PDDocument()) {
            var images = fens.stream().map(ChessBoardPDFGenerator::generateChessBoardImage)
                    .toList();
            addImagesToPDF(document, images, 3, 5, title);
            document.save("chessboard_images.pdf");
            System.out.println("PDF saved successfully : chessboard_images.pdf");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String generateChessBoardImage(String fen) {
        // ChessVision API endpoint for FEN to Image conversion
        String apiUrl = "https://fen2image.chessvision.ai/" + fen.replaceAll(" ", "%20");
        try {
            return downloadImage(apiUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String downloadImage(String imageUrl) throws IOException {
        Path tempDir = Files.createTempDirectory("chessboard_images");

        String randomFileName = UUID.randomUUID() + ".png";
        Path target = tempDir.resolve(randomFileName);

        // Open a URL Stream and save the image to the temporary directory
        try (InputStream in = new URL(imageUrl).openStream()) {
            Files.copy(in, target);
            log.info("Saved " + target);
            return target.toFile().getAbsolutePath();
        } catch (Exception e) {
            log.error("Can not create temp file {}", target, e);
            e.printStackTrace();
            throw e;
        }
    }

    public static void addImagesToPDF(PDDocument document, List<String> imagePaths, int imagesPerRow, int spacing, String title) throws IOException {
        int imagesPerPage = imagesPerRow * imagesPerRow;
        int numRows = (int) Math.ceil((double) imagePaths.size() / imagesPerPage);

        log.info("Creating pdf of " + numRows + " images");
        for (int pageIdx = 0; pageIdx < numRows; pageIdx++) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Add title
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, page.getMediaBox().getHeight() - 50);
                contentStream.showText(title);
                contentStream.endText();

                // Add page number
                contentStream.beginText();
                contentStream.newLineAtOffset(page.getMediaBox().getWidth() - 100, page.getMediaBox().getHeight() - 50);
                contentStream.showText("Page " + (pageIdx + 1));
                contentStream.endText();
            }

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)) {
                int rowStartIdx = pageIdx * imagesPerPage;
                int rowEndIdx = Math.min(rowStartIdx + imagesPerPage, imagePaths.size());

                // Calculate maximum width and height for an image based on the number of images per row and the spacing
                float availableWidth = page.getMediaBox().getWidth() - (imagesPerRow + 1) * spacing;
                float availableHeight = page.getMediaBox().getHeight() - 100 - (imagesPerRow + 1) * spacing; // Reduce height for title and page number
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
                    int row = (i - rowStartIdx) / imagesPerRow;
                    int col = (i - rowStartIdx) % imagesPerRow;
                    float x = spacing + col * (maxWidth + spacing);
                    float y = page.getMediaBox().getHeight() - (spacing + row * (maxHeight + spacing)) - height;

                    // Draw the scaled image on the page
                    contentStream.drawImage(image, x, y - 50, width, height);
                }
            }
        }
    }
}

