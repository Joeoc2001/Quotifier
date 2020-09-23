package dev.joeoc.quotifier;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.AttachmentOption;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class Bot extends ListenerAdapter {
    private final FontSet _fontSet;

    public Bot(FontSet fontSet){
        _fontSet = fontSet;
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

        BufferedImage image = drawQuote(name, quote, 1000, 1000);

        InputStream file;
        try {
            file = getStreamFromBuffer(image);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        channel.sendFile(file, "quote.jpg").queue();
    }

    private BufferedImage drawQuote(String name, String[] quote, int width, int height) {
        BufferedImage image = getBufferedImage(width, height);

        Graphics2D graphics = image.createGraphics();

        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);

        graphics.setPaint(Color.WHITE);
        graphics.fillRect(0, 0, width, height);

        graphics.setPaint(Color.BLACK);
        graphics.setFont(_fontSet.getRandomFont().deriveFont(160f));
        graphics.drawString(name, 50, 950);

        return image;
    }

    public static BufferedImage getBufferedImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public static InputStream getStreamFromBuffer(BufferedImage bufferedImage) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", outStream);
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
