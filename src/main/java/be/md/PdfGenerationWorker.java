package be.md;

import javax.swing.*;
import java.nio.file.Path;

public class PdfGenerationWorker extends SwingWorker<Void, Void> {
    private final int diagramsPerRow;
    private final Runnable done;
    private final Feedback feedback;
    private final String workingDirectory;

    public PdfGenerationWorker(int diagramsPerRow, Runnable done, String workingDirectory, Feedback feedback) {
        this.diagramsPerRow = diagramsPerRow;
        this.done = done;
        this.workingDirectory = workingDirectory;
        this.feedback = feedback;
    }

    @Override
    protected Void doInBackground() {
        Path location = Path.of(workingDirectory);
        ChessBoardPDFGenerator.process(feedback, PgnFileLister.listPgnFilesInCurrentDirectory(workingDirectory), diagramsPerRow, location);
        return null;
    }

    @Override
    protected void done() {
        done.run();
    }
}