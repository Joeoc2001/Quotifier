package dev.joeoc.quotifier;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class MessageBag {
    private final List<String> messages;
    private final Random rng = new Random();

    public MessageBag(Collection<String> messages) {
        this.messages = List.copyOf(messages);
    }

    public String getRandom() {
        return messages.get(rng.nextInt(messages.size()));
    }
}
