package be.md;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Path;
import java.util.Properties;
import java.util.stream.Collectors;

public class Fen2PdfMain {
    private static final String CONFIG_FILE = "config.properties"; // Properties file name
    private static Properties properties = new Properties();
    private static final JButton startButton = new JButton("Generate PDF");
    private static final SpinnerNumberModel spinnerModel = new SpinnerNumberModel(4, 1, 8, 1);
    private static final ScrollableTextImageList scrollableTextImageList = new ScrollableTextImageList();
    private static final Color BACKGROUND_COLOR = Color.LIGHT_GRAY;
    private static boolean inProgress = false;
    private static String workingDirectory;
    private static final JButton selectDirButton= new JButton("Select working dir");

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            loadProperties();
            createAndShowGUI();
            var pgns = PgnFileLister.listPgnFilesInCurrentDirectory(".");


            String commaSeparatedPaths = pgns.stream()
                    .map(Path::toString)
                    .collect(Collectors.joining(", "));

            scrollableTextImageList.addItem("Pgn files to process:\n"+commaSeparatedPaths);
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


        JPanel spinnerPanel = selectNrDiagramsPerRow();
        rootPanel.add(spinnerPanel);


        rootPanel.add(Box.createVerticalStrut(10));

        JPanel dirSelectionPanel = selectWorkingDir(frame);
        rootPanel.add(dirSelectionPanel);

        rootPanel.add(Box.createVerticalStrut(10));

        JScrollPane scrollPane = new JScrollPane(scrollableTextImageList);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollableTextImageList.setBackground(BACKGROUND_COLOR);
        scrollPane.setPreferredSize(new Dimension(600, 500));
        rootPanel.add(scrollPane);


        JPanel startButtonPanel = generatePDFButton();
        rootPanel.add(startButtonPanel);

        frame.add(rootPanel);
        frame.setVisible(true);
    }

    private static JPanel generatePDFButton() {
        JPanel startButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        startButtonPanel.setBackground(BACKGROUND_COLOR);
        startButtonPanel.add(startButton);
        return startButtonPanel;
    }

    private static JPanel selectWorkingDir(JFrame frame) {
        selectDirButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(workingDirectory));
            fileChooser.setDialogTitle("Select working directory (for input pgn and output pdf files)");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int inputFolder = fileChooser.showDialog(frame, "Select");
            if (inputFolder == JFileChooser.APPROVE_OPTION) {
                String selectedDir = fileChooser.getSelectedFile().getAbsolutePath();
                workingDirectory = selectedDir;
                selectDirButton.setText(workingDirectory); // Update label text
                JOptionPane.showMessageDialog(frame, "Selected Directory: " + selectedDir);
                saveProperties();
            }
        });
        JPanel dirSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dirSelectionPanel.add(new JLabel("Select directory:"));
        dirSelectionPanel.add(selectDirButton);
        dirSelectionPanel.setBackground(BACKGROUND_COLOR);
        return dirSelectionPanel;
    }

    private static JPanel selectNrDiagramsPerRow() {
        // Spinner panel
        JPanel spinnerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        spinnerPanel.setBackground(BACKGROUND_COLOR);
        JSpinner spinner = new JSpinner(spinnerModel);
        spinner.setPreferredSize(new Dimension(50, 30));
        spinnerPanel.add(new JLabel("How many diagrams per row? (1-8): "));
        spinnerPanel.add(spinner);
        return spinnerPanel;
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
            Path location = Path.of(workingDirectory);
            ChessBoardPDFGenerator.process(new Feedback(scrollableTextImageList), PgnFileLister.listPgnFilesInCurrentDirectory(workingDirectory), diagramsPerRow, location);
            return null;
        }

        @Override
        protected void done() {
            setBusyState(false);
            startButton.setEnabled(true);
            JOptionPane.showMessageDialog(null, "PDF generation completed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static void saveProperties() {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.setProperty("workingDirectory", workingDirectory);
            properties.store(output, "FEN2PDF Application Properties");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void setDefaultProperties() {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.setProperty("workingDirectory", new File("").getAbsolutePath());
            properties.store(output, "FEN2PDF Application Properties (Default)");
            workingDirectory = properties.getProperty("workingDirectory");
            selectDirButton.setText(workingDirectory);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void loadProperties() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
            workingDirectory = properties.getProperty("workingDirectory", new File("").getAbsolutePath());
            if (workingDirectory.isEmpty()) {
                workingDirectory = new File("").getAbsolutePath();
            }
            selectDirButton.setText(workingDirectory);
        } catch (IOException ex) {
            setDefaultProperties();
        }
    }
}
