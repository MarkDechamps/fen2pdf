package be.md;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

class HyperlinkLabel extends JLabel {
    private static final Color LINK_COLOR = new Color(0, 0, 255);

    public HyperlinkLabel(String text, String url) {
        super("<html><a href=''>" + text + "</a></html>");
        setForeground(LINK_COLOR);
        setHorizontalAlignment(SwingConstants.CENTER);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
}