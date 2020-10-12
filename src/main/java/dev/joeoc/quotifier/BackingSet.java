package dev.joeoc.quotifier;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BackingSet {
    private final ObjectMapper mapper = new ObjectMapper();

    private final List<Backing> backings;

    private final Random rng = new Random();

    public BackingSet() throws IOException {
        try (InputStream in = getClass().getResourceAsStream("/backings/backings.json");
             InputStreamReader reader = new InputStreamReader(in)) {
            backings = mapper.readValue(reader, new TypeReference<>(){});
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
