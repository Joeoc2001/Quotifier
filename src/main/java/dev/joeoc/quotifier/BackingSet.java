package dev.joeoc.quotifier;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BackingSet {
    private final List<Backing> backings;

    private final Random rng = new Random();

    public BackingSet() throws IOException {
        Gson g = new Gson();
        Type collectionType = new TypeToken<List<Backing>>(){}.getType();

        try (InputStream in = getClass().getResourceAsStream("/backings/backings.json");
             InputStreamReader reader = new InputStreamReader(in)) {
            backings = g.fromJson(reader, collectionType);
        }

        if (backings.size() == 0) {
            throw new RuntimeException("Need at least one valid backing");
        }
    }

    public Backing getRandomBacking() {
        int randomIndex = rng.nextInt(backings.size());
        return backings.get(randomIndex);
    }

}
