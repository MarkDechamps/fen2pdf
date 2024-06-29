package be.md;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Slf4j
public class ChessBoardPDFGenerator {
    private static Feedback genFeedback;

    public static void process(Feedback feedback, List<Path> pgns, int diagramsPerRow,Path location) {
        genFeedback = feedback;
        log("PGN files in the same folder are detected and processed");
        log("Generates a pdf file with chess diagrams from the FEN's in the pgn");

        if (!pgns.isEmpty()) {
            log("Arguments found:" + Arrays.toString(pgns.toArray()));
            pgns.forEach(pgn -> {
                log("Processing " + pgn);
                var parsedPgn = PGNFileReader.loadPgn(pgn);
                Path fileName = pgn.getFileName();
                String name = fileName.toFile().getName();
                if (name.endsWith(".pgn")) {
                    name = name.substring(0, name.length() - 4); // Remove the last 4 characters (.pgn)
                }
                createPdfFileWithDiagramsFrom(location,name, parsedPgn,diagramsPerRow);
            });
        } else {
            log("Please put a pgn file in the same folder as this program.");
            log.info("No pgn found in {}. Exiting.", new File(".").getAbsolutePath());
            System.exit(1);
        }
    }

    private static void log(String msg, BufferedImage image) {
        log.info(msg);
        genFeedback.setText(msg, image);
    }

    private static void log(String msg) {
        log.info(msg);
        genFeedback.setText(msg);
    }

    private static void createPdfFileWithDiagramsFrom(Path location, String title, List<String> fens, int diagramsPerRow) {
        log("Title used: " + title);
        log("FENS found:" + fens.size());
        try (PDDocument document = new PDDocument()) {
            var images = fens.stream().map(ChessBoardPDFGenerator::generateChessBoardImage)
                    .toList();
            addImagesToPDF(document, images, diagramsPerRow, 5, title);
            String path = (location.toAbsolutePath().toString()+"\\"+ (title + ".pdf"));
            document.save( path);
            log("PDF saved successfully : " + title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String generateChessBoardImage(String fen) {
        Optional<String> cachedFile = fetchFromCache(fen);

        cachedFile.ifPresent(s -> {
            try {
                log("Generated from cache:" + s, ImageIO.read(new File(s)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return cachedFile.orElseGet(() -> {
            try {
                var image = downloadFen2pngImage(fen);
                log.info("Generated:" + image, image);
                return image;
            } catch (IOException e) {
                log("Failed to download with Fen2png service. Trying chessvision.ai.");
                try {
                    return downloadChessvisionImage(fen);
                } catch (IOException e1) {
                    log("Failed to download with chessvision.ai as well.");
                    throw new RuntimeException(e);
                }
            }
        });

    }

    private static Optional<String> fetchFromCache(String fen) {
        Path tempFilePath = getTempFilePath(fen);
        boolean existsInCache = tempFilePath.toFile().exists();
        if (existsInCache) {
            log.info("Fetching " + tempFilePath + " from cache");
        }

        return existsInCache ? Optional.of(tempFilePath.toFile().getAbsolutePath()) : Optional.empty();
    }

    public static String downloadChessvisionImage(String fen) throws IOException {
        String imageUrl = "https://fen2image.chessvision.ai/" + escapeFen(fen);
        Path target = getTempFilePath(fen);
        try (InputStream in = new URL(imageUrl).openStream()) {
            Files.copy(in, target);
            log("Saved " + target);
            return target.toFile().getAbsolutePath();
        } catch (Exception e) {
            log.error("Can not create temp file {}", target, e);
            e.printStackTrace();
            throw e;
        }
    }

    private static String escapeFen(String fen) {
        return fen.replaceAll(" ", "%20");
    }

    private static Path getTempFilePath(String fen) {

        Path cacheDir = Paths.get("cache");
        if (Files.notExists(cacheDir)) {
            try {
                Files.createDirectory(cacheDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            log("Directory created: " + cacheDir.toAbsolutePath());
        }

        String fileName = escapeForCache(fen);
        return cacheDir.resolve(fileName);
    }

    private static String escapeForCache(String fen) {
        return escapeFen(fen).replaceAll("/", "@").replaceAll("-", "_") + ".png";
    }

    public static String downloadFen2pngImage(String fen) throws IOException {
        String fen2PgnUrl = "https://fen2png.com/api/?fen=" + escapeFen(fen);
        URL url = new URL(fen2PgnUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        Path target = getTempFilePath(fen);
        if (!target.toFile().exists()) {
            try (InputStream inputStream = connection.getInputStream()) {
                Document doc = Jsoup.parse(inputStream, "UTF-8", fen2PgnUrl);
                var imgElement = doc.select("img").first();
                if (imgElement != null) {
                    var src = imgElement.attr("src");
                    if (isBase64Image(src)) {
                        writeBase64ImageTo(src, target);
                    } else {
                        throw new IOException("Unexpected image format: " + src);
                    }
                } else {
                    throw new IOException("No image element found in the HTML response.");
                }
            } finally {
                connection.disconnect();
            }
        } else {
            log("Using " + target.toFile().getAbsolutePath() + " from cache ", ImageIO.read(target.toFile()));
        }

        return target.toFile().getAbsolutePath();
    }

    private static boolean isBase64Image(String src) {
        return src.startsWith("data:image/png;base64,");
    }

    private static void writeBase64ImageTo(String src, Path target) {
        String base64Data = src.substring("data:image/png;base64,".length());
        var imageBytes = Base64.getDecoder().decode(base64Data);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes)) {
            var image = ImageIO.read(bis);
            if (image != null) {
                ImageIO.write(image, "png", target.toFile());
                log("Image saved to: " + target, image);
            } else {
                throw new IOException("Failed to decode the image from the base64 data.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void addImagesToPDF(PDDocument document, List<String> imagePaths, int imagesPerRow, int spacing, String title) throws IOException {
        int imagesPerPage = imagesPerRow * imagesPerRow;
        int numPages = (int) Math.ceil((double) imagePaths.size() / imagesPerPage);

        log("Creating pdf '"+title+"' of " + numPages + " pages");
        for (int pageIdx = 0; pageIdx < numPages; pageIdx++) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            addTitleAndPageNumber(document, title, page, pageIdx);
            addImages(document, imagePaths, imagesPerRow, spacing, page, pageIdx, imagesPerPage);
        }
    }

    private static void addImages(PDDocument document, List<String> imagePaths, int imagesPerRow, int spacing, PDPage page, int pageIdx, int imagesPerPage) throws IOException {
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)) {
            int rowStartIdx = pageIdx * imagesPerPage;
            int rowEndIdx = Math.min(rowStartIdx + imagesPerPage, imagePaths.size());

            float availableWidth = page.getMediaBox().getWidth() - (imagesPerRow + 1) * spacing;
            float availableHeight = page.getMediaBox().getHeight() - 100 - (imagesPerRow + 1) * spacing; // Reduce height for title and page number
            float maxWidth = availableWidth / imagesPerRow;
            float maxHeight = availableHeight / imagesPerRow;

            for (int i = rowStartIdx; i < rowEndIdx; i++) {
                String imagePath = imagePaths.get(i);
                log.info("Reading:{}", imagePath);
                var bufferedImage = ImageIO.read(new File(imagePath));
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "png", output);
                PDImageXObject image = PDImageXObject.createFromByteArray(document, output.toByteArray(), "image");

                float scale = Math.min(maxWidth / image.getWidth(), maxHeight / image.getHeight());

                float width = image.getWidth() * scale;
                float height = image.getHeight() * scale;

                int row = (i - rowStartIdx) / imagesPerRow;
                int col = (i - rowStartIdx) % imagesPerRow;
                float x = spacing + col * (maxWidth + spacing);
                float y = page.getMediaBox().getHeight() - (spacing + row * (maxHeight + spacing)) - height;

                contentStream.drawImage(image, x, y - 50, width, height);
            }
        }
    }

    private static void addTitleAndPageNumber(PDDocument document, String title, PDPage page, int pageIdx) throws IOException {
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            // Add title
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, page.getMediaBox().getHeight() - 50);
            contentStream.showText(title);
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(page.getMediaBox().getWidth() - 100, page.getMediaBox().getHeight() - 50);
            contentStream.showText("Page " + (pageIdx + 1));
            contentStream.endText();
        }
    }
}

