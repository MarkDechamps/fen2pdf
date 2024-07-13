package be.md;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

class ImagePanel extends JPanel {
    private Image image;

    public ImagePanel(String imagePath) {
        try {
            image = ImageIO.read(new File(imagePath));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            int panelWidth = this.getWidth();
            int panelHeight = this.getHeight();
            int imageWidth = image.getWidth(this);
            int imageHeight = image.getHeight(this);

            double aspectRatio = (double) imageWidth / imageHeight;
            int newWidth, newHeight;

            if (panelWidth < panelHeight * aspectRatio) {
                newWidth = panelWidth;
                newHeight = (int) (panelWidth / aspectRatio);
            } else {
                newHeight = panelHeight;
                newWidth = (int) (panelHeight * aspectRatio);
            }

            int x = (panelWidth - newWidth) / 2;
            int y = (panelHeight - newHeight) / 2;

            g.drawImage(image, x, y, newWidth, newHeight, this);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return ofNullable(image)
                .map(image-> new Dimension(image.getWidth(this), image.getHeight(this)))
                .orElseGet(super::getPreferredSize);
    }
}
