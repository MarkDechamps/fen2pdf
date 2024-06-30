package be.md;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Path;
import java.util.Properties;
import java.util.stream.Collectors;

import static be.md.Messages.*;

public class Fen2PdfMain {
    private static final String CONFIG_FILE = "config.properties"; // Properties file name
    private static final Properties properties = new Properties();
    private static final JButton startButton = new JButton(generate_pdf);
    private static final SpinnerNumberModel spinnerModel = new SpinnerNumberModel(4, 1, 8, 1);
    private static final ScrollableTextImageList scrollableTextImageList = new ScrollableTextImageList();
    private static final Color BACKGROUND_COLOR = Color.LIGHT_GRAY;
    private static boolean inProgress = false;
    private static String workingDirectory;
    private static final JButton selectDirButton = new JButton(select_working_dir);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            loadProperties();
            createAndShowGUI();
            //addPgnFilesToLog();
        });


        startButton.addActionListener(e -> {
            if (!inProgress) {
                startButton.setEnabled(false);
                setBusyState(true);
                int diagramsPerRow = Integer.parseInt(spinnerModel.getValue().toString());
                Runnable whenDone = () -> {
                    setBusyState(false);
                    startButton.setEnabled(true);
                };
                var pdfWorker = new PdfGenerationWorker(diagramsPerRow, whenDone, workingDirectory, new Feedback(scrollableTextImageList));
                pdfWorker.execute();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 720);
        frame.getContentPane().setBackground(BACKGROUND_COLOR);
        putIconOn(frame);

        var rootPanel = getRootPanel();
        rootPanel.add(selectNrDiagramsPerRow());
        rootPanel.add(Box.createVerticalStrut(10));
        rootPanel.add(selectWorkingDir(frame));
        rootPanel.add(Box.createVerticalStrut(10));
        rootPanel.add(scollingTextRegion());
        rootPanel.add(generatePDFButton());

        frame.add(rootPanel);
        frame.setVisible(true);
    }

    private static JScrollPane scollingTextRegion() {
        var scrollPane = new JScrollPane(scrollableTextImageList);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollableTextImageList.setBackground(BACKGROUND_COLOR);
        scrollPane.setPreferredSize(new Dimension(600, 500));
        return scrollPane;
    }

    private static JPanel getRootPanel() {
        JPanel rootPanel = new JPanel();
        rootPanel.setBackground(BACKGROUND_COLOR);
        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.Y_AXIS));
        rootPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return rootPanel;
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
            fileChooser.setDialogTitle(file_chooser_title);
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int inputFolder = fileChooser.showDialog(frame, file_chooser_select);
            if (inputFolder == JFileChooser.APPROVE_OPTION) {
                setSelectedDir(fileChooser.getSelectedFile().getAbsolutePath());
                addPgnFilesToLog();
            }
        });
        JPanel dirSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dirSelectionPanel.add(new JLabel(file_chooser_select_dir));
        dirSelectionPanel.add(selectDirButton);
        dirSelectionPanel.setBackground(BACKGROUND_COLOR);
        return dirSelectionPanel;
    }

    private static void setSelectedDir(String selectedDir) {
        workingDirectory = selectedDir;
        selectDirButton.setText(workingDirectory);
        saveProperties();
    }

    private static void addPgnFilesToLog() {
        var pgns = PgnFileLister.listPgnFilesInCurrentDirectory(workingDirectory);
        var commaSeparatedPaths = pgns.stream()
                .map(Path::toString)
                .collect(Collectors.joining(", "));

        scrollableTextImageList.addItem(pgn_files_to_process + commaSeparatedPaths);
    }

    private static JPanel selectNrDiagramsPerRow() {
        // Spinner panel
        JPanel spinnerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        spinnerPanel.setBackground(BACKGROUND_COLOR);
        JSpinner spinner = new JSpinner(spinnerModel);
        spinner.setPreferredSize(new Dimension(50, 30));
        spinnerPanel.add(new JLabel(how_many_diagrams));
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


    private static void saveProperties() {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.setProperty("workingDirectory", workingDirectory);
            properties.store(output, "FEN2PDF Application Properties");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void loadProperties() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
            workingDirectory = properties.getProperty("workingDirectory", new File("").getAbsolutePath());
            selectDirButton.setText(workingDirectory);
        } catch (IOException ex) {
            setSelectedDir(new File("").getAbsolutePath());
        }
        addPgnFilesToLog();
    }
}
