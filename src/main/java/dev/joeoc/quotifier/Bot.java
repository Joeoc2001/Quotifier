package dev.joeoc.quotifier;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Bot extends ListenerAdapter {
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

        Guild guild = msg.getGuild();

        String name = messageParts[1];

        List<Member> members = msg.getMentionedMembers();

        channel.sendMessage("Mentioned: " + members.stream().map(Member::getNickname).collect(Collectors.joining(", "))).queue();

        channel.sendMessage("Quote from " + name).queue();

        Member attributation = getMemberByName(guild, name);
        channel.sendMessage("User " + attributation).queue();

        if (attributation != null) {
            channel.sendMessage("Most recent message: " + getMostRecentMessageByUser(channel, attributation.getUser(), 100)).queue();
        }
    }

    public static Member getMemberByName(Guild guild, String name) {
        Member possibleMember = guild.getMemberById(name);
        if (possibleMember != null) {
            return possibleMember;
        }

        possibleMember = guild.getMemberByTag(name);
        if (possibleMember != null) {
            return possibleMember;
        }

        List<Member> possibleMembers = guild.getMembersByEffectiveName(name, true);
        if (possibleMembers.size() == 1) {
            return possibleMembers.get(0);
        }

        possibleMembers = guild.getMembersByName(name, true);
        if (possibleMembers.size() == 1) {
            return possibleMembers.get(0);
        }

        possibleMembers = guild.getMembersByNickname(name, true);
        if (possibleMembers.size() == 1) {
            return possibleMembers.get(0);
        }

        return null;
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
