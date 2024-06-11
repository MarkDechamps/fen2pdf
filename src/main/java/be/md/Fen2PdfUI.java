package be.md;

import javax.swing.*;
import java.awt.*;

public class Fen2PdfUI {
    private static final JButton startButton = new JButton("Start");
    private static final JTextArea textArea = new JTextArea(10, 30);
    private static boolean inProgress = false;

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();

            var pgns = PgnFileLister.listPgnFilesInCurrentDirectory();
            textArea.append("Pgn files to process:\n");
            pgns.forEach(pgn -> {
                textArea.append(pgn.toString()+"\n");
            });
        });


        startButton.addActionListener(e -> {
            if (!inProgress) {
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

        // Add padding between button and text area
        panel.add(Box.createVerticalStrut(10));

        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
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
            ChessBoardPDFGenerator.process(new Feedback(textArea), PgnFileLister.listPgnFilesInCurrentDirectory());
            return null;
        }

        @Override
        protected void done() {
            setBusyState(false);
            JOptionPane.showMessageDialog(null, "PDF generation completed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
