package com.example.demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class PGNFileReader {

    public static List<String> loadPgn(String filePath) {
        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);

            StringBuilder pgnBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                pgnBuilder.append(scanner.nextLine()).append("\n");
            }
            scanner.close();

            // Extract FEN notations from the PGN
            var pgnContent = pgnBuilder.toString();
            var fens = extractFENsFromPGN(pgnContent);

            return fens;

        } catch (FileNotFoundException e) {
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
                })
                .collect(Collectors.toList());
    }
}
