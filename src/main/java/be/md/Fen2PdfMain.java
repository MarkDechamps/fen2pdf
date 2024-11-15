package be.md;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Path;
import java.util.Properties;
import java.util.stream.Collectors;

import static be.md.Messages.*;
import static java.awt.FlowLayout.*;
import static javax.swing.JFileChooser.*;

public class Fen2PdfMain {
    private static final String CONFIG_FILE = "config.properties"; // Properties file name
    private static final Properties properties = new Properties();
    private static final JButton startButton = new JButton(generate_pdf);
    private static final SpinnerNumberModel spinnerModel = new SpinnerNumberModel(4, 1, 8, 1);
    private static final ScrollableTextImageList scrollableTextImageList = new ScrollableTextImageList();
    private static final Color BACKGROUND_COLOR = Color.LIGHT_GRAY;
    private static final Logger log = LoggerFactory.getLogger(Fen2PdfMain.class);
    private static boolean inProgress = false;
    private static String workingDirectory;
    private static final JButton selectDirButton = new JButton(select_working_dir);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            loadProperties();
            createAndShowGUI();
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
        rootPanel.add(buttons());



        JPanel bottom = new JPanel();
        bottom.setBackground(BACKGROUND_COLOR);
        var image = new ImagePanel("icons/icon.png");
        image.setBackground(BACKGROUND_COLOR);
        bottom.add(image);

        footer(bottom);

        rootPanel.add(bottom);

        frame.add(rootPanel);
        frame.setVisible(true);
    }

    private static void footer(JPanel bottom) {
        JPanel bottomText = new JPanel();
        bottomText.setLayout(new BoxLayout(bottomText, BoxLayout.Y_AXIS));
        bottomText.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        bottomText.setBackground(BACKGROUND_COLOR);
        var fen2Pgn = new HyperLink("Made possible by fen2pgn", link_fen2pgn);
        fen2Pgn.setBackground(BACKGROUND_COLOR);
        bottomText.add(fen2Pgn);
        var pairFx = new HyperLink("Try Pairfx! Free tournament software for in the classroom or club", "https://sourceforge.net/projects/pairfx/");
        pairFx.setBackground(BACKGROUND_COLOR);
        bottomText.add(pairFx);
        bottom.add(bottomText);
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

    private static JPanel buttons() {
        JPanel startButtonPanel = new JPanel(new FlowLayout(CENTER));
        startButtonPanel.setBackground(BACKGROUND_COLOR);

        var startButton = new JButton("Generate PDF");
        startButton.setToolTipText("Creates PDF's for all pgns in the selected folder");

        var mirrorFenCheckbox = new JCheckBox("Flip positions");
        mirrorFenCheckbox.setToolTipText("Mirrors all positions so a1 becomes h1. (Be careful with positions where castling is possible!)");
        mirrorFenCheckbox.setBackground(BACKGROUND_COLOR);

        var pagenumberCheckbox = new JCheckBox("Page numbers");
        pagenumberCheckbox.setToolTipText("Add page numbers to pdf");
        pagenumberCheckbox.setBackground(BACKGROUND_COLOR);

        startButtonPanel.add(startButton);
        startButtonPanel.add(mirrorFenCheckbox);
        startButtonPanel.add(pagenumberCheckbox);

        startButton.addActionListener(e -> {
            if (!inProgress) {
                startButton.setEnabled(false);
                setBusyState(true);
                int diagramsPerRow = Integer.parseInt(spinnerModel.getValue().toString());
                boolean mirrorFen = mirrorFenCheckbox.isSelected();
                boolean pageNumbers = pagenumberCheckbox.isSelected();

                Runnable whenDone = () -> {
                    setBusyState(false);
                    startButton.setEnabled(true);
                    FolderOpener.openFolder(workingDirectory);
                };

                var metadata = Metadata.builder()
                        .diagramsPerRow(diagramsPerRow)
                        .addPageNumbers(pageNumbers)
                        .mirror(mirrorFen).build();

                var pdfWorker = new PdfGenerationWorker(metadata, whenDone, workingDirectory, new Feedback(scrollableTextImageList));
                pdfWorker.execute();
            }
        });

        return startButtonPanel;
    }


    private static JPanel selectWorkingDir(JFrame frame) {
        selectDirButton.addActionListener(e -> {
            var fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(workingDirectory));
            fileChooser.setDialogTitle(file_chooser_title);
            fileChooser.setFileSelectionMode(DIRECTORIES_ONLY);

            int inputFolder = fileChooser.showDialog(frame, file_chooser_select);
            if (inputFolder == APPROVE_OPTION) {
                setSelectedDir(fileChooser.getSelectedFile().getAbsolutePath());
                addPgnFilesToLog();
            }
        });
        var dirSelectionPanel = new JPanel(new FlowLayout(LEFT));
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
        JPanel spinnerPanel = new JPanel(new FlowLayout(LEFT));
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
