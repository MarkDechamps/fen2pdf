package com.example.demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

public class PGNFileReader {

    public static List<String> loadPgn(Path filePath) {
        try {
            Scanner scanner = new Scanner(filePath);

            StringBuilder pgnBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                pgnBuilder.append(scanner.nextLine()).append("\n");
            }
            scanner.close();
            var pgnContent = pgnBuilder.toString();

            return extractFENsFromPGN(pgnContent);

        } catch (IOException e) {
            System.err.println("PGN file not found!");
        }
        return List.of();
    }

    public static List<String> extractFENsFromPGN(String pgnContent) {
        var games = Arrays.asList(pgnContent.split("(?m)^\\s*$"));
        return games.stream()
                .map(String::trim)
                .map(game -> {
                    List<String> lines = Arrays.asList(game.split("\n"));
                    return lines.stream()
                            .filter(line -> line.startsWith("[FEN "))
                            .map(line -> line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\"")))
                            .findFirst()
                            .orElse(null);
                }).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
