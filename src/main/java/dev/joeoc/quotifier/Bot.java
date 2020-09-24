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
                LocalDate.now().format(DateTimeFormatter.ISO_DATE)
        };

        Backing backing = backingSet.getRandomBacking();
        BufferedImage image = backing.getImage();
        Graphics2D graphics = getGraphics2D(image);

        //Font baseFont = fontSet.getRandomFont();
        Font font = graphics.getFont();//baseFont.deriveFont(160f);
        graphics.setPaint(Color.BLACK);

        float fontSize = 180f;
        graphics.setFont(font.deriveFont(fontSize));
        while(!tryDrawQuote(graphics, backing, paragraphs)) {
            fontSize *= 0.75f;

            if (fontSize <= 5f) {
                throw new RuntimeException("Font size hit 5 without text fitting");
            }

            graphics.setFont(graphics.getFont().deriveFont(fontSize));
        }

        graphics.dispose();
        return image;
    }

    private boolean tryDrawQuote(Graphics2D graphics, Backing backing, String[] paragraphs) {
        FontMetrics metrics = graphics.getFontMetrics();

        int usableWidth = backing.getUsableWidth();
        java.util.List<String> lines = new java.util.ArrayList<>();
        for (String paragraph : paragraphs) {
            lines.addAll(StringUtils.wrap(paragraph, metrics, usableWidth));
        }

        int usableHeight = backing.getUsableHeight();
        float lineHeight = metrics.getHeight();
        float totalHeight = lines.size() * lineHeight;

        if (totalHeight > usableHeight) {
            return false;
        }

        drawParagraph(graphics, lines, backing.getUsableTopLeft());

        return true;
    }

    public static void drawParagraph(Graphics2D graphics, java.util.List<String> paragraph, Point topLeft) {
        float x = topLeft.x;
        float y = topLeft.y;

        FontMetrics metrics = graphics.getFontMetrics();
        float lineHeight = metrics.getHeight();
        float ascent = metrics.getAscent();

        y += ascent;
        for (String line : paragraph) {
            graphics.drawString(line, x, y);
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
