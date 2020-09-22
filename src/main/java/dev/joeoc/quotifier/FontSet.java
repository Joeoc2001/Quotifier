package dev.joeoc.quotifier;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

public class FontSet {
    private final Map<String, Font> fonts;
    private final Random rng = new Random();

    public FontSet(List<String> names)
    {
        fonts = getFonts(names);

        if (fonts.size() == 0)
        {
            throw new RuntimeException("Need at least one valid font");
        }
    }

    private static HashMap<String, Font> getFonts(List<String> names)
    {
        HashMap<String, Font> fonts = new HashMap<>();

        for (String name: names) {
            String urlStr = "https://fonts.google.com/download?family=" + encodeValue(name);

            URL url;
            try {
                url = new URL(urlStr);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                continue;
            }

            BufferedInputStream in;
            try {
                in = new BufferedInputStream(url.openStream());
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            Font font;
            try {
                font = Font.createFont(Font.TRUETYPE_FONT, in);
            } catch (FontFormatException | IOException e) {
                e.printStackTrace();
                continue;
            }

            fonts.put(name.toLowerCase(), font);
        }

        return fonts;
    }

    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    public Font getFont(String name) {
        return fonts.get(name);
    }

    public Font getRandomFont() {
        List<Font> valuesList = new ArrayList<>(fonts.values());
        int randomIndex = rng.nextInt(valuesList.size());
        return valuesList.get(randomIndex);
    }

    public Set<String> getFontNames() {
        return fonts.keySet();
    }
}
