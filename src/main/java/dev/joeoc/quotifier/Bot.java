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
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Bot extends ListenerAdapter {
    private static final Random random = new Random();

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

        String[] messageParts = message.split("\\s");
        if (messageParts.length == 0) {
            return;
        }

        if (messageParts[0].matches("^[~!-]quotify$")) {
            MessageChannel channel = event.getChannel();
            respondToQuotify(channel, messageParts);
            return;
        }
    }

    private void respondToQuotify(MessageChannel channel, String[] messageParts) {
        if (messageParts.length == 1) {
            channel.sendMessage(BotMessages.HowToUse.getRandom()).queue();
            return;
        }

        String name = messageParts[1].replace("_", " ");
        String quote = String.join(" ", Arrays.copyOfRange(messageParts, 2, messageParts.length));

        if (messageParts.length >= 50 || quote.length() >= 250) {
            channel.sendMessage(BotMessages.TooLong.getRandom()).queue();
            return;
        }

        final String[] paragraphs = new String[] {
                quote,
                "~ " + name,
        };

        makeAndSend(channel, paragraphs);
    }

    private void makeAndSend(MessageChannel channel, String[] paragraphs) {
        InputStream file;
        try {
            file = makeQuoteFile(paragraphs);
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            channel.sendMessage(BotMessages.Failure.getRandom() + "\n" + e.getMessage()).queue();
            throw new RuntimeException(e);
        }

        channel.sendMessage(BotMessages.Success.getRandom())
                .addFile(file, "quote." + extension)
                .timeout(random.nextInt(5000), TimeUnit.MILLISECONDS)
                .queue();
    }

    private InputStream makeQuoteFile(String[] paragraphs) throws IOException {
        BufferedImage image = drawQuote(paragraphs);
        return getStreamFromBuffer(image);
    }

    private BufferedImage drawQuote(String[] paragraphs) throws IOException {
        Backing backing = backingSet.getRandomBacking();
        BufferedImage image = backing.getImage();
        Graphics2D graphics = getGraphics2D(image);
        graphics.setPaint(Color.BLACK);

        Font font = fontSet.getRandomFont();

        for (String paragraph: paragraphs) {
            int displayUpTo = font.canDisplayUpTo(paragraph);
            if (displayUpTo != -1) {
                throw new RuntimeException("Unsupported character: '" + paragraph.charAt(displayUpTo) + "' in font " + font.getName());
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

        Point topLeft = backing.getUsableTopLeft();
        topLeft.translate(padding, padding);
        Point bottomRight = backing.getUsableBottomRight();
        bottomRight.translate(-padding, -padding);
        ParagraphDrawing.drawParagraph(graphics, lines, topLeft, bottomRight);

        return true;
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
