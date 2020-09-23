package dev.joeoc.quotifier;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class Bot extends ListenerAdapter {
    private final FontSet fontSet;
    private final BackingSet backingSet;

    private static final String extension = "png";

    public Bot(FontSet fontSet, BackingSet backingSet){
        this.fontSet = fontSet;
        this.backingSet = backingSet;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message msg = event.getMessage();

        // If not a command
        if (msg.getAuthor().isBot() || !msg.isFromGuild()) {
            return;
        }

        String message = msg.getContentRaw();

        // If not a message for this bot
        if (!message.startsWith("~quotify")) {
            return;
        }

        String[] messageParts = message.split("\\s");
        if (messageParts.length == 0) {
            return;
        }

        if (!messageParts[0].equals("~quotify")) {
            return;
        }

        MessageChannel channel = event.getChannel();

        if (messageParts.length <= 2) {
            channel.sendMessage("[INFO]").queue();
            return;
        }

        String name = messageParts[1];
        String[] quote = Arrays.copyOfRange(messageParts, 1, messageParts.length - 1);
        name = name.replace("_", " ");

        InputStream file;
        try {
            BufferedImage image = drawQuote(name, quote);
            file = getStreamFromBuffer(image);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        channel.sendFile(file, "quote." + extension).queue();
    }

    private BufferedImage drawQuote(String name, String[] quote) throws IOException {
        Backing backing = backingSet.getRandomBacking();

        BufferedImage image = backing.getImage();

        Graphics2D graphics = getGraphics2D(image);

        graphics.setPaint(Color.BLACK);
        graphics.setFont(fontSet.getRandomFont().deriveFont(160f));
        graphics.drawString(name, 50, 50);

        graphics.dispose();
        return image;
    }

    public static Graphics2D getGraphics2D(BufferedImage image) {
        Graphics2D graphics = image.createGraphics();

        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);

        return graphics;
    }

    public static InputStream getStreamFromBuffer(BufferedImage bufferedImage) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, extension, outStream);
        return new ByteArrayInputStream(outStream.toByteArray());
    }

    public static CompletableFuture<Optional<Message>> getMostRecentMessageByUser(MessageChannel channel, User user, int depth) {
        return channel.getIterableHistory()
                .takeAsync(depth)
                .thenApply(list ->
                        list.stream()
                                .filter(m -> m.getAuthor().equals(user)) // Filter messages by author
                                .findFirst()
                );
    }
}
