package dev.joeoc.quotifier;

import java.awt.*;

public class ParagraphDrawing {
    public static void drawParagraph(Graphics2D graphics, java.util.List<String> paragraph, Point topLeft, Point bottomRight) {
        FontMetrics metrics = graphics.getFontMetrics();
        float totalWidth = bottomRight.x - topLeft.x;
        float totalHeight = bottomRight.y - topLeft.y;

        float indentPerLine = Float.POSITIVE_INFINITY;
        for (int i = 1; i < paragraph.size(); i++) {
            float textWidth = metrics.stringWidth(paragraph.get(i));
            float extra = totalWidth - textWidth;
            float extraPerLine = extra / i;
            indentPerLine = Math.min(indentPerLine, extraPerLine);
        }

        float x = topLeft.x;
        float y = topLeft.y;

        float lineHeight = (totalHeight + metrics.getLeading()) / paragraph.size();
        float ascent = metrics.getAscent();

        y += ascent;
        for (String line : paragraph) {
            graphics.drawString(line, x, y);
            y += lineHeight;
            x += indentPerLine;
        }
    }
}
