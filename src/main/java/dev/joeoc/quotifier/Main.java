package dev.joeoc.quotifier;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws LoginException
    {
        if (args.length < 1) {
            System.out.println("You have to provide a token as first argument!");
            System.exit(1);
        }

        FontSet fontSet = new FontSet(List.of(
                "Exmouth", "Champignon", "Beauty Angelique",
                "Tangerine", "Rosabelia Script", "Frutilla Script",
                "Argentina Script", "Nattalia", "Bandara Signature", "Symphonie CAT",
                "Balithya", "Angellie Script", "Aureligena Script", "Bellisa"
        ));

        JDABuilder.createLight(args[0], GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .addEventListeners(new Bot(fontSet))
                .setActivity(Activity.playing("with Abstract Nouns"))
                .build();
    }
}
