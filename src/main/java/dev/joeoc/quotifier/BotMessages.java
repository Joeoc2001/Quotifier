package dev.joeoc.quotifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BotMessages {

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
            "exciting",
            "animating",
            "enlivening",
            "exalting",
            "inspiriting",
            "motivating",
            "stimulating",
            "hopeful",
            "promising",
            "reassuring",
            "supportive",
            "promiseful",
            "appealing",
            "astonishing",
            "breathtaking",
            "dangerous",
            "dramatic",
            "flashy",
            "hectic",
            "impressive",
            "interesting",
            "intriguing",
            "lively",
            "moving",
            "provocative",
            "stimulating",
            "thrilling",
            "animating",
            "arousing",
            "arresting",
            "bracing",
            "commoving",
            "electrifying",
            "fine",
            "impelling",
            "intoxicating",
            "overpowering",
            "overwhelming",
            "rousing",
            "stirring",
            "titillating",
            "agitative",
            "exhilarant",
            "eye-popping",
            "far-out",
            "groovy",
            "hair-raising",
            "heady",
            "melodramatic",
            "mind-blowing",
            "neat",
            "racy",
            "rip-roaring",
            "sensational",
            "showy",
            "spine-tingling",
            "wild",
            "zestful",
            "heartening",
            "stirring",
            "inspirational",
            "uplifting",
            "moving",
            "exhilarating",
            "refreshing",
            "fresh",
            "original",
            "authentic",
            "wise"
    ));

    private static final MessageBag adjectiveInspiringExtension = new MessageBag(
            List.of(
                    "",
                    "It's just so {adjective_inspiring}.",
                    "It's very {adjective_inspiring}.",
                    "How {adjective_inspiring}.",
                    "You've created something that's quite {adjective_inspiring}.",
                    "Generations will study your {adjective_inspiring} words.",
                    "I wish I could be that {adjective_inspiring}.",
                    "Many people wish they were a tenth as {adjective_inspiring} as you.",
                    "I can see the headlines now: {adjective_inspiring} author writes {adjective_inspiring} quote.",
                    "One word: {adjective_inspiring}.",
                    "I can sum it up in two words: {adjective_inspiring} and {adjective_inspiring}.",
                    "I have three words for you: {adjective_inspiring}, {adjective_inspiring} and {adjective_inspiring}."
            ),
            Map.of(
                    "adjective_inspiring", adjectiveInspiring
            )
    );

    private static final MessageBag actionRead = new MessageBag(
            List.of(
                    "read",
                    "study",
                    "interpret differently",
                    "view",
                    "go over",
                    "go through",
                    "pore over",
                    "scratch the surface",
                    "try to comprehend",
                    "try to fathom",
                    "try to grasp",
                    "begin to understand",
                    "take in",
                    "absorb"
            )
    );

    private static final MessageBag periodEveryday = new MessageBag(
            List.of(
                    "every day",
                    "every now and then",
                    "now and then",
                    "sometimes",
                    "all the time",
                    "every week",
                    "weekly",
                    "every once in a while",
                    "every time I can",
                    "whenever I get the chance",
                    "whenever the missus will let me",
                    "whenever I have guests",
                    "at least once a month",
                    "annually"
            )
    );

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

    public static final MessageBag Failure = new MessageBag(
            List.of(
                "Sorry friend, something went wrong. {adjective_inspiring_extension}",
                "My friend, I appear to have made a mistake while preparing your {adjective_inspiring} quote:",
                "My good man, something has gone terribly wrong while I was attempting to immortalize your {adjective_inspiring} words:"
            ),
            Map.of(
                    "adjective_inspiring", adjectiveInspiring,
                    "adjective_inspiring_extension", adjectiveInspiringExtension
            )
    );

    public static final MessageBag Success = new MessageBag(
            List.of(
                    "I think I might {action_hang} that one on my {place_wall} for {group_myself} to {action_read}. {adjective_inspiring_extension}",
                    "I think I might {action_read} that one {period_everyday}. {adjective_inspiring_extension}",
                    "How {adjective_inspiring}. I feel I should {action_read} that one {period_everyday}. {adjective_inspiring_extension}",
                    "How very {adjective_inspiring}! {adjective_inspiring_extension}",
                    "How {adjective_inspiring}! "
            ),
            Map.of(
                    "action_hang", actionHang,
                    "place_wall", placeWall,
                    "adjective_inspiring", adjectiveInspiring,
                    "adjective_inspiring_extension", adjectiveInspiringExtension,
                    "action_read", actionRead,
                    "period_everyday", periodEveryday
            )
    );
}
