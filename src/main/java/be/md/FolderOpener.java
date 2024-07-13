package be.md;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class FolderOpener {

    public static void openFolder(String folderPath) {
        // Validate the folder path
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("The provided path does not exist or is not a directory.");
            return;
        }

        // Open the folder
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.open(folder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Desktop operations are not supported on this system.");
        }
    }

    public static void main(String[] args) {
        // Example usage
        String folderPath = "C:\\path\\to\\your\\folder"; // Change this to your folder path
        openFolder(folderPath);
    }
}
