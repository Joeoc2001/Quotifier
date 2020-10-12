package dev.joeoc.quotifier;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Backing {
    private final String image;
    private final Point usableTopLeft;
    private final Point usableBottomRight;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Backing(@JsonProperty("image") String image, @JsonProperty("usableTopLeft") Point usableTopLeft, @JsonProperty("usableBottomRight") Point usableBottomRight) {
        this.image = image;
        this.usableTopLeft = usableTopLeft;
        this.usableBottomRight = usableBottomRight;
    }

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
        return new Point(usableTopLeft);
    }

    public Point getUsableBottomRight() {
        return new Point(usableBottomRight);
    }

    public int getUsableWidth() {
        return usableBottomRight.x - usableTopLeft.x;
    }

    public int getUsableHeight() {
        return usableBottomRight.y - usableTopLeft.y;
    }
}
