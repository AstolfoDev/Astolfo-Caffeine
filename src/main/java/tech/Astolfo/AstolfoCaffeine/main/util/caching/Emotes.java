package tech.Astolfo.AstolfoCaffeine.main.util.caching;

import java.util.Objects;

public class Emotes {
    // Emoji codes for the Currency Icons
    public static class currency {
        public String credit = Objects.requireNonNull(Cache.jda.getEmoteById("738537190652510299")).getAsMention();
        public String trapcoin = Objects.requireNonNull(Cache.jda.getEmoteById("738537189884821606").getAsMention());
        public String token = Objects.requireNonNull(Cache.jda.getEmoteById("738537180003172354").getAsMention());
    }
}