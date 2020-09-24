package dev.joeoc.quotifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BotMessages {
    public static final MessageBag HowToUse = new MessageBag(List.of(
            "If you wish for me to produce a quote, you must provide a [name] and then the [quote] with which the name is attributed.",
            "I am more than happy to produce a quote for you, but you must provide a [name] and then a [quote] for me to immortalise.",
            "Please friend, provide a [name] and then a [quote] for me to write down.",
            "Hello good sir! Please provide me with a [name] and a [quote].",
            "Did somebody call? Please provide a [name] and a [quote] for me."
    ));

    public static final MessageBag TooLong = new MessageBag(List.of(
            "Sorry friend, that is far too verbose to ever catch on as a quote.",
            "My friend, you must think smaller! That quote is far too long",
            "My good man, I cannot use a whole novel! Please do not mock me so.",
            "'Your quotes must be shorter than that' - Me, right now. Try again.",
            "I feel you misunderstand the purpose of a quote, my good friend.",
            "I think you might need to go back to the drawing board with that one friend; it is a tad wordy.",
            "If one is to reach the literary prowess that one desires, one must express their thoughts in far fewer words."
    ));

    private static final MessageBag actionHang = new MessageBag(List.of(
            "hang",
            "rest",
            "put",
            "attach",
            "drape",
            "dangle",
            "pin",
            "nail",
            "fasten"
    ));

    private static final MessageBag placeWall = new MessageBag(List.of(
            "wall",
            "door",
            "fireplace",
            "kitchen table",
            "fridge door",
            "office door",
            "desk",
            "monitor",
            "wall behind my monitor",
            "front door",
            "back door"
    ));

    private static final MessageBag adjectiveInspiring = new MessageBag(List.of(
            "inspiring",
            "encouraging",
            "heartening",
            "stirring",
            "inspirational",
            "uplifting",
            "moving",
            "exhilarating",
            "refreshing",
            "fresh",
            "original",
            "authentic"
    ));

    private static final MessageBag adjectiveInspiringExtension = new MessageBag(
            List.of(
                    "",
                    "It's just so {adjective_inspiring}.",
                    "It's very {adjective_inspiring}.",
                    "You've created something that's quite {adjective_inspiring}."
            ),
            Map.of(
                    "adjective_inspiring", adjectiveInspiring
            )
    );

    public static final MessageBag Success = new MessageBag(
            List.of(
                    "I think I might {action_hang} that one on my {place_wall}. {adjective_inspiring_extension}",
                    "How {adjective_inspiring}.",
                    "How very {adjective_inspiring}!"
            ),
            Map.of(
                    "action_hang", actionHang,
                    "place_wall", placeWall,
                    "adjective_inspiring", adjectiveInspiring,
                    "adjective_inspiring_extension", adjectiveInspiringExtension
            )
    );
}
