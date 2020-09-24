package dev.joeoc.quotifier;

import java.util.*;

public class MessageBag {
    private final List<String> messages;
    private final Map<String, MessageBag> replacements;
    private final Random rng = new Random();

    public MessageBag(Collection<String> messages) {
        this(messages, new HashMap<>());
    }

    public MessageBag(Collection<String> messages, Map<String, MessageBag> replacements) {
        this.messages = List.copyOf(messages);
        this.replacements = replacements;
    }

    public String getRandom() {
        String baseMessage = messages.get(rng.nextInt(messages.size()));
        for (String key : replacements.keySet()) {
            String fullKey = "{" + key + "}";
            int index;
            while ((index = baseMessage.indexOf(fullKey)) != -1) {
                String replacement = replacements.get(key).getRandom();
                baseMessage = baseMessage.substring(0, index) + replacement + baseMessage.substring(index + fullKey.length());
            }
        }
        return baseMessage;
    }
}
