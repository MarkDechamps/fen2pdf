package be.md;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PgnFileLister {

    public static List<Path> listPgnFilesInCurrentDirectory(String input) {
        List<Path> pgnFiles = new ArrayList<>();
        var currentDir = Paths.get(input);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(currentDir, "*.pgn")) {
            stream.iterator().forEachRemaining(pgnFiles::add);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return pgnFiles;
    }
}
