package be.md;

import javax.swing.*;

public class Feedback {
    private final JTextArea message;

    public Feedback(JTextArea label) {
        this.message = label;
    }

    public void setText(String text) {
        SwingUtilities.invokeLater(() -> {
                    message.append(text + "\n");
                    message.setCaretPosition(message.getDocument().getLength());
                }
        );
    }
}
