package be.md;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;

public class Fen2PdfMain {
    private static final JButton startButton = new JButton("Generate PDF");
    private static final SpinnerNumberModel spinnerModel = new SpinnerNumberModel(4, 1, 8, 1);
    private static final ScrollableTextImageList scrollableTextImageList = new ScrollableTextImageList();
    private static final Color BACKGROUND_COLOR = Color.LIGHT_GRAY;
    private static boolean inProgress = false;
    private static String workingDirectory;

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();

            var pgns = PgnFileLister.listPgnFilesInCurrentDirectory(".");
            scrollableTextImageList.addItem("Pgn files to process:\n");
            pgns.forEach(pgn -> {
                scrollableTextImageList.addItem(pgn.toString());
            });
        });


        startButton.addActionListener(e -> {
            if (!inProgress) {
                startButton.setEnabled(false);
                setBusyState(true);
                new PdfGenerationWorker(Integer.parseInt(spinnerModel.getValue().toString())).execute();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("FEN2PDF    Mark Dechamps      (B)eer licensed 2024");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 720);
        frame.getContentPane().setBackground(BACKGROUND_COLOR);
        putIconOn(frame);

        JPanel rootPanel = new JPanel();
        rootPanel.setBackground(BACKGROUND_COLOR);
        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.Y_AXIS));
        rootPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Button panel with centered alignment
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(startButton);
        buttonPanel.add(Box.createHorizontalStrut(20)); // Add horizontal spacing between buttons
        JButton selectDirButton = new JButton("Select working dir");
        selectDirButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select working directory (for input pgn and output pdf files)");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int inputFolder = fileChooser.showDialog(frame, "Select");
            if (inputFolder == JFileChooser.APPROVE_OPTION) {
                String selectedDir = fileChooser.getSelectedFile().getAbsolutePath();
                workingDirectory = selectedDir;
                JOptionPane.showMessageDialog(frame, "Selected Directory: " + selectedDir);
            }
        });
        buttonPanel.add(selectDirButton);
        rootPanel.add(buttonPanel);

        // Add padding between button panel and spinner panel
        rootPanel.add(Box.createVerticalStrut(10));

        // Spinner panel
        JPanel spinnerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        spinnerPanel.setBackground(BACKGROUND_COLOR);
        JSpinner spinner = new JSpinner(spinnerModel);
        spinner.setPreferredSize(new Dimension(50, 30));
        spinnerPanel.add(new JLabel("How many diagrams per row? (1-8): "));
        spinnerPanel.add(spinner);
        rootPanel.add(spinnerPanel);

        // Add padding between spinner panel and scroll pane
        rootPanel.add(Box.createVerticalStrut(10));

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(scrollableTextImageList);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollableTextImageList.setBackground(BACKGROUND_COLOR);
        scrollPane.setPreferredSize(new Dimension(600, 500));
        rootPanel.add(scrollPane);

        frame.add(rootPanel);
        frame.setVisible(true);
    }

    private static void putIconOn(JFrame frame) {
        ImageIcon img = new ImageIcon("icons/icon.png");
        frame.setIconImage(img.getImage());
    }


    private static void setBusyState(boolean busy) {
        inProgress = busy;
    }

    private static class PdfGenerationWorker extends SwingWorker<Void, Void> {
        private final int diagramsPerRow;

        public PdfGenerationWorker(int diagramsPerRow) {
            this.diagramsPerRow = diagramsPerRow;
        }

        @Override
        protected Void doInBackground() {
            Path location= Path.of(workingDirectory);
            ChessBoardPDFGenerator.process(new Feedback(scrollableTextImageList), PgnFileLister.listPgnFilesInCurrentDirectory(workingDirectory),diagramsPerRow,location);
            return null;
        }

        @Override
        protected void done() {
            setBusyState(false);
            startButton.setEnabled(true);
            JOptionPane.showMessageDialog(null, "PDF generation completed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
