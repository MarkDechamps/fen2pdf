package be.md;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Fen2PdfMain {
    private static final JButton startButton = new JButton("Start");
    private static final SpinnerNumberModel spinnerModel = new SpinnerNumberModel(4, 1, 8, 1);
    private static final ScrollableTextImageList scrollableTextImageList = new ScrollableTextImageList();
    private static final Color BACKGROUND_COLOR = Color.LIGHT_GRAY;
    private static boolean inProgress = false;
    private static String inputDirectory;

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();

            var pgns = PgnFileLister.listPgnFilesInCurrentDirectory();
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
        var frame = new JFrame("FEN2PDF    Mark Dechamps      (B)eer licensed 2024");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 720);
        frame.getContentPane().setBackground(Color.BLUE);
        putIconOn(frame);

        JPanel rootPanel = new JPanel();
        rootPanel.setBackground(BACKGROUND_COLOR);
        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.Y_AXIS));
        rootPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(startButton);
        rootPanel.add(buttonPanel);

        // Add padding between button and spinner
        rootPanel.add(Box.createVerticalStrut(10));

        JSpinner spinner = new JSpinner(spinnerModel);
        spinner.setPreferredSize(new Dimension(50, 30)); // Set preferred size for spinner
        JPanel spinnerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        spinnerPanel.setBackground(BACKGROUND_COLOR);
        spinnerPanel.add(new JLabel("How many diagrams per row ? (1-8): "));
        spinnerPanel.add(spinner);
        rootPanel.add(spinnerPanel);
        rootPanel.add(Box.createVerticalStrut(10));

        JButton selectDirButton = new JButton("Input directory");
        selectDirButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select input folder with pgn files");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int inputFolder = fileChooser.showDialog(frame, "Select");
            if (inputFolder == JFileChooser.APPROVE_OPTION) {
                String selectedDir = fileChooser.getSelectedFile().getAbsolutePath();
                inputDirectory = selectedDir;
                JOptionPane.showMessageDialog(frame, "Selected Directory: " + selectedDir);
            }
        });
        rootPanel.add(selectDirButton);

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
            ChessBoardPDFGenerator.process(new Feedback(scrollableTextImageList), PgnFileLister.listPgnFilesInCurrentDirectory(),diagramsPerRow);
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
