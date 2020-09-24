package dev.joeoc.quotifier;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;

public class Backing {
    public String image;
    public Point usableTopLeft;
    public Point usableBottomRight;

    public BufferedImage getImage() throws IOException {
        InputStream stream = getClass().getResourceAsStream("/backings/" + image);
        return copyImage(ImageIO.read(stream));
    }

    public static BufferedImage copyImage(BufferedImage source) {
        int type = source.getType();
        if (type == BufferedImage.TYPE_CUSTOM) {
            throw new IllegalArgumentException("Cannot use custom image types");
        }

        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), type);
        Graphics g = b.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, b.getWidth(), b.getHeight());
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

    public Point getUsableTopLeft() {
        return usableTopLeft;
    }

    public Point getUsableBottomRight() {
        return usableBottomRight;
    }

    public int getUsableWidth() {
        return usableBottomRight.x - usableTopLeft.x;
    }

    public int getUsableHeight() {
        return usableBottomRight.y - usableTopLeft.y;
    }
}
