package dev.joeoc.quotifier;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.AttachmentOption;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class Bot extends ListenerAdapter {
    private final Font font;

    public Bot() throws URISyntaxException, IOException, FontFormatException {
        font = Font.createFont(Font.TRUETYPE_FONT, new File(new URI("https://fonts.google.com/download?family=Rogue%20Script")));
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
        name = name.replace("_", " ");

        BufferedImage image = getBufferedImage(100, 100);

        Graphics2D g = image.createGraphics();
        g.setFont(font);
        g.drawString(name, 50, 50);

        InputStream file;
        try {
            file = getStreamFromBuffer(image);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        channel.sendFile(file, "quote.jpg").queue();
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
