package dev.joeoc.quotifier;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws LoginException, IOException {
        if (args.length < 1) {
            System.out.println("You have to provide a token as first argument!");
            System.exit(1);
        }

        FontSet fontSet = new FontSet(List.of(
                "Exmouth", "Champignon", "Beauty Angelique"//,
                //"Tangerine", "Rosabelia Script", "Frutilla Script",
                //"Argentina Script", "Nattalia", "Bandara Signature",
                //"Balithya", "Angellie Script", "Aureligena Script", "Bellisa"
        ));

        BackingSet backingSet = new BackingSet();

        JDABuilder.createLight(args[0], GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .addEventListeners(new Bot(fontSet, backingSet))
                .setActivity(Activity.playing("with Abstract Nouns"))
                .build();
    }
}
