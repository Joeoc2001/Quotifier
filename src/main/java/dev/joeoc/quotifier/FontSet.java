package dev.joeoc.quotifier;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
            System.out.println("Processing font " + name);

            Path directory = Paths.get("tmp");
            File cache = new File(directory.toString(), name + ".ttf");

            if (!cache.exists()) {
                System.out.println("Not cached");
                try {
                    byte[] fontData = getFontFromOnline(name);

                    Files.createDirectories(directory);
                    cache.createNewFile();

                    OutputStream outStream = new FileOutputStream(cache);
                    outStream.write(fontData);
                } catch (IOException e) {
                    System.out.println("Error on getting font from online: ");
                    System.out.println(e.getMessage());
                    continue;
                }
            }

            Font font;
            try {
                font = Font.createFont(Font.TRUETYPE_FONT, cache);
            } catch (FontFormatException | IOException e) {
                System.err.println(e.getMessage());
                continue;
            }

            fonts.put(name.toLowerCase(), font);
        }

        return fonts;
    }

    private static byte[] getFontFromOnline(String name) throws IOException {
        String urlStr = "https://dl.dafont.com/dl/?f=" + encodeValue(name);

        URL url = new URL(urlStr);
        try (BufferedInputStream zipData = new BufferedInputStream(url.openStream())) {
            return getFontFromZip(zipData);
        }
    }

    private static byte[] getFontFromZip(InputStream zipData) throws IOException {
        byte[] buffer = new byte[2048];

        try (BufferedInputStream bis = new BufferedInputStream(zipData);
             ZipInputStream stream = new ZipInputStream(bis)) {

            ZipEntry entry;
            while ((entry = stream.getNextEntry()) != null) {

                if (entry.isDirectory() || !entry.getName().endsWith(".ttf")) {
                    continue;
                }

                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    int len;
                    while ((len = stream.read(buffer)) > 0) {
                        baos.write(buffer, 0, len);
                    }

                    return baos.toByteArray();
                }
            }
        }

        throw new IOException("Zip file had no top level tff file");
    }

    private static String encodeValue(String inputString) {
        return inputString.toLowerCase().replace(" ", "_");
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
