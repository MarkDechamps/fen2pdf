package be.md;


import javax.swing.*;
import java.awt.*;

// Thanks class
public class Thanks extends JPanel {
    public Thanks() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel messageLabel = new JLabel(Messages.using);
        HyperlinkLabel linkLabel = new HyperlinkLabel(Messages.link_fen2pgn, Messages.link_fen2pgn);

        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        linkLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createVerticalGlue());
        add(messageLabel);
        add(Box.createVerticalStrut(10));
        add(linkLabel);
        add(Box.createVerticalGlue());
    }
}