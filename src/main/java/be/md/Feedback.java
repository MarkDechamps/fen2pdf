package be.md;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class Feedback {
    private ScrollableTextImageList lines;

    public Feedback(ScrollableTextImageList list) {
        this.lines=list;
    }

    public void setText(String text) {
        SwingUtilities.invokeLater(() -> {
            lines.addItem(text);
                }
        );
    }
    public void setText(String text, BufferedImage image) {
        SwingUtilities.invokeLater(() -> {
                    lines.addItem(image,text);
                }
        );
    }
}
