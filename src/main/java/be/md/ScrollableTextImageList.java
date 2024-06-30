package be.md;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

public class ScrollableTextImageList extends JPanel {
    private final List<TextImageItem> items;

    public ScrollableTextImageList() {
        this.items = new ArrayList<>();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    public void addItem(BufferedImage image, String text) {
        TextImageItem item = new TextImageItem(image, text);
        items.add(item);
        add(item);
        revalidate();
        repaint();
    }

    public void addItem(String text) {
        TextImageItem item = new TextImageItem(text);
        items.add(item);
        add(item);
        revalidate();
        repaint();
    }

    private static class TextImageItem extends JPanel {
        private final Image image;
        private final String text;
        private final int imageHeight;
        private final int imageWidth;

        public TextImageItem(BufferedImage image, String text) {
            this.text = text;

            int maxWidth = 100;
            int newWidth = Math.min(image.getWidth(), maxWidth);
            int newHeight = (image.getHeight() * newWidth) / image.getWidth();

            Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            this.image = scaledImage;
            this.imageWidth = newWidth;
            this.imageHeight = newHeight;
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);

            ImageIcon imageAndText = new ImageIcon(scaledImage, text);
            JLabel imageLabel = new JLabel(imageAndText);
            imageLabel.setToolTipText(text);
            add(imageLabel, BorderLayout.WEST);
        }

        public TextImageItem(String text) {
            this.text = text;
            this.image = null;
            this.imageWidth = 0;
            this.imageHeight = 0;
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            JLabel textLabel = new JLabel(text);
            add(textLabel, BorderLayout.WEST);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            var preferredSize = isNull(image) ? new Dimension(100, 30) : new Dimension(imageWidth, imageHeight + 10);
            setPreferredSize(preferredSize);
        }
    }

}
