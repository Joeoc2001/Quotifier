package dev.joeoc.quotifier;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

        if (messageParts.length == 1) {
            channel.sendMessage("[INFO]").queue();
            return;
        }

        if (messageParts.length >= 100) {
            channel.sendMessage(BotMessages.TooLong.getRandom()).queue();
            return;
        }

        String name = messageParts[1];
        String[] quote = Arrays.copyOfRange(messageParts, 2, messageParts.length);
        name = name.replace("_", " ");

        InputStream file;
        try {
            BufferedImage image = drawQuote(name, quote);
            file = getStreamFromBuffer(image);
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            channel.sendMessage("My apologies, something went wrong while building your quote: " + e.getMessage()).queue();
            return;
        }

        channel.sendFile(file, "quote." + extension).queue();
    }

    private BufferedImage drawQuote(String name, String[] quote) throws IOException {
        String[] paragraphs = new String[] {
                String.join(" ", quote),
                "- " + name,
                //LocalDate.now().format(DateTimeFormatter.ISO_DATE),
        };

        Backing backing = backingSet.getRandomBacking();
        BufferedImage image = backing.getImage();
        Graphics2D graphics = getGraphics2D(image);
        graphics.setPaint(Color.BLACK);

        Font font = fontSet.getRandomFont();

        for (String paragraph: paragraphs) {
            int displayUpTo = font.canDisplayUpTo(paragraph);
            if (displayUpTo != -1) {
                throw new RuntimeException("Unsupported character: " + paragraph.charAt(displayUpTo));
            }
        }

        float fontSize = 300f;
        graphics.setFont(font.deriveFont(fontSize));
        while(!tryDrawQuote(graphics, backing, paragraphs, 20)) {
            fontSize *= 0.9f;

            if (fontSize <= 5f) {
                throw new RuntimeException("Font size hit 5 without text fitting");
            }

            graphics.setFont(graphics.getFont().deriveFont(fontSize));
        }

        graphics.dispose();
        return image;
    }

    private boolean tryDrawQuote(Graphics2D graphics, Backing backing, String[] paragraphs, int padding) {
        FontMetrics metrics = graphics.getFontMetrics();

        int usableWidth = backing.getUsableWidth() - 2 * padding;
        int usableHeight = backing.getUsableHeight() - 2 * padding;

        float lineHeight = metrics.getHeight();

        java.util.List<String> lines = new java.util.ArrayList<>();
        float totalHeight = 0;
        for (String paragraph : paragraphs) {
            java.util.List<String> newLines = StringUtils.wrap(paragraph, metrics, usableWidth);

            totalHeight += newLines.size() * lineHeight;
            if (totalHeight > usableHeight) {
                return false;
            }

            lines.addAll(newLines);
        }

        drawParagraph(graphics, lines, backing.getUsableTopLeft(), padding);

        return true;
    }

    public static void drawParagraph(Graphics2D graphics, java.util.List<String> paragraph, Point topLeft, float padding) {
        float x = topLeft.x;
        float y = topLeft.y;

        FontMetrics metrics = graphics.getFontMetrics();
        float lineHeight = metrics.getHeight();
        float ascent = metrics.getAscent();

        y += ascent;
        for (String line : paragraph) {
            graphics.drawString(line, x + padding, y + padding);
            y += lineHeight;
        }
    }

    public static Graphics2D getGraphics2D(BufferedImage image) {
        Graphics2D graphics = image.createGraphics();

        graphics.setRenderingHint(RenderingHints.KEY_RESOLUTION_VARIANT, RenderingHints.VALUE_RESOLUTION_VARIANT_BASE);
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
