package be.md;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Fen2PdfUI {
    private static final JButton startButton = new JButton("Start");
    private static final JTextArea textArea = new JTextArea(10, 30);
    private static boolean inProgress = false;

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });

        startButton.addActionListener(e -> {
            if (!inProgress) {
                setBusyState(true);
                new PdfGenerationWorker().execute();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("FEN2PDF    Mark Dechamps 2024");
        putIconOn(frame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 720);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Use BoxLayout with Y_AXIS orientation
        frame.add(panel);

        // Add components with Box.createVerticalStrut for even spacing
        panel.add(Box.createVerticalStrut(10));
        panel.add(startButton);
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
        @Override
        protected Void doInBackground() {
            ChessBoardPDFGenerator.main(new Feedback(textArea));
            return null;
        }

        @Override
        protected void done() {
            setBusyState(false);
            JOptionPane.showMessageDialog(null, "PDF generation completed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
