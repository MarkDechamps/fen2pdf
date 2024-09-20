package be.md;

import javax.swing.*;
import java.nio.file.Path;

public class PdfGenerationWorker extends SwingWorker<Void, Void> {
    private final Metadata metadata;
    private final Runnable done;
    private final Feedback feedback;
    private final String workingDirectory;

    public PdfGenerationWorker(Metadata metadata, Runnable done, String workingDirectory, Feedback feedback) {
        this.metadata = metadata;
        this.done = done;
        this.workingDirectory = workingDirectory;
        this.feedback = feedback;
    }

    @Override
    protected Void doInBackground() {
        Path location = Path.of(workingDirectory);
        ChessBoardPDFGenerator.process(feedback, PgnFileLister.listPgnFilesInCurrentDirectory(workingDirectory), metadata, location);
        return null;
    }

    @Override
    protected void done() {
        done.run();
    }
}