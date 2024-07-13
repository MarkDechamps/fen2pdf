package be.md;

import javax.swing.*;
import java.awt.*;

public class HyperLink extends JPanel {
    public HyperLink(String label, String link) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.CENTER_ALIGNMENT);
        var linkLabel = new HyperlinkLabel(label,link);
        linkLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createVerticalStrut(10));
        add(Box.createVerticalGlue());
        add(linkLabel);
        add(Box.createVerticalStrut(10));
    }
}