package be.md;

import javax.swing.*;
import java.awt.*;

public class Fen2PdfMain {
    private static final JButton startButton = new JButton("Start");
    //private static final JTextArea textArea = new JTextArea(10, 30);
    private static final ScrollableTextImageList scrollableTextImageList = new ScrollableTextImageList();
    private static boolean inProgress = false;

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();

            var pgns = PgnFileLister.listPgnFilesInCurrentDirectory();
            scrollableTextImageList.addItem("Pgn files to process:\n");
            pgns.forEach(pgn -> {
                //textArea.append(pgn.toString()+"\n");
                scrollableTextImageList.addItem(pgn.toString());
            });
        });


        startButton.addActionListener(e -> {
            if (!inProgress) {
                startButton.setEnabled(false);
                setBusyState(true);
                new PdfGenerationWorker().execute();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("FEN2PDF    Mark Dechamps      (B)eer licensed 2024");
        putIconOn(frame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 720);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Use BoxLayout with Y_AXIS orientation
        frame.add(panel);

        // Add padding to top and bottom of panel
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add start button with centered alignment
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(startButton);
        panel.add(buttonPanel);

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(4, 1, 8, 1);

        // Create the spinner with the SpinnerNumberModel
        JSpinner spinner = new JSpinner(spinnerModel);

        // Set preferred size to ensure the spinner fits nicely
        spinner.setPreferredSize(new Dimension(50, 30));

        panel.add(spinner);

        // Add padding between button and text area
        panel.add(Box.createVerticalStrut(10));

        //textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(scrollableTextImageList);
        panel.add(scrollPane);

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
        public PdfGenerationWorker() {
        }

        @Override
        protected Void doInBackground() {
            ChessBoardPDFGenerator.process(new Feedback(scrollableTextImageList), PgnFileLister.listPgnFilesInCurrentDirectory());
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
