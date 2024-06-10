package com.example.demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class PGNFileReader {

    public static List<String> loadPgn(String filePath) {
        try {
            // Path to the PGN file
            //String filePath = "src/main/resources/static/example.pgn";
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);

            StringBuilder pgnBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                pgnBuilder.append(scanner.nextLine()).append("\n");
            }
            scanner.close();

            // Extract FEN notations from the PGN
            String pgnContent = pgnBuilder.toString();
            String[] fens = extractFENsFromPGN(pgnContent);

            return List.of(fens);

        } catch (FileNotFoundException e) {
            System.err.println("PGN file not found!");
        }
        return List.of();
    }

    private static String[] extractFENsFromPGN(String pgnContent) {
        // Split PGN content by game delimiter
        String[] games = pgnContent.split("(?m)^\\s*$");

        // Extract FEN notation from each game
        String[] fens = new String[games.length];
        for (int i = 0; i < games.length; i++) {
            String game = games[i].trim();
            String[] lines = game.split("\n");
            for (String line : lines) {
                if (line.startsWith("[FEN ")) {
                    fens[i] = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
                    break;
                }
            }
        }
        return fens;
    }
}
