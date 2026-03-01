package it.proud.api.managers;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * Unified notification service for ProudCore.
 *
 * <p>Modules send notifications without knowing how they are displayed.
 * The server owner configures delivery channels in {@code notifications/config.yml}.
 * Modules never reference titles, actionbars, or bossbars directly.</p>
 *
 * <h2>Typical usage</h2>
 * <pre>{@code
 * INotificationService notify = ProudCoreAPI.get().getNotificationService();
 *
 * notify.send(player, Notification.builder()
 *     .type("economy.deposit")
 *     .title("&a+{amount} {currency}")
 *     .body("Il tuo saldo è ora &e{balance}")
 *     .placeholder("amount",   "500")
 *     .placeholder("currency", "Monete")
 *     .placeholder("balance",  "1500")
 *     .build());
 * }</pre>
 *
 * @author ProudCore Team
 * @version 1.0
 * @since 1.0
 */
public interface INotificationService {

    /**
     * Sends a notification to a single online player.
     * If the player is offline the notification is silently discarded.
     *
     * @param player       the target; must not be {@code null}
     * @param notification the notification to deliver; must not be {@code null}
     */
    void send(Player player, Notification notification);

    /**
     * Sends a notification to a player identified by UUID.
     * No-op if the player is not online.
     *
     * @param uuid         the target UUID; must not be {@code null}
     * @param notification the notification to deliver; must not be {@code null}
     */
    void send(UUID uuid, Notification notification);

    /**
     * Broadcasts a notification to every online player.
     *
     * @param notification the notification to broadcast; must not be {@code null}
     */
    void broadcast(Notification notification);

    /**
     * Sends a notification to every member of the given clan that is currently online.
     *
     * @param clanName     the internal clan name; must not be {@code null}
     * @param notification the notification to deliver; must not be {@code null}
     */
    void sendToClan(String clanName, Notification notification);

    /**
     * Sends a notification to a specific list of players.
     *
     * @param players      the targets; must not be {@code null}
     * @param notification the notification to deliver; must not be {@code null}
     */
    void sendToAll(List<Player> players, Notification notification);

    /**
     * Registers a custom delivery channel.
     * Channels are identified by their {@link NotificationChannel#getId()} and
     * are automatically mapped in the config.
     *
     * @param channel the channel to register; must not be {@code null}
     */
    void registerChannel(NotificationChannel channel);

    /**
     * Returns {@code true} if a channel with the given id is registered.
     *
     * @param channelId the channel id; must not be {@code null}
     * @return {@code true} if registered
     */
    boolean isChannelRegistered(String channelId);

    /**
     * Immutable notification payload.
     * Build one with {@link #builder()}.
     */
    interface Notification {

        /** Returns the notification type key (e.g. {@code "economy.deposit"}). */
        String getType();

        /** Returns the title string, may contain color codes and {placeholders}. */
        String getTitle();

        /** Returns the body string, may contain color codes and {placeholders}. */
        String getBody();

        /** Returns the resolved value for the given placeholder key, or {@code null}. */
        String getPlaceholder(String key);

        /**
         * Returns the title with all placeholders replaced.
         *
         * @return resolved title
         */
        String resolveTitle();

        /**
         * Returns the body with all placeholders replaced.
         *
         * @return resolved body
         */
        String resolveBody();

        /** Starts a new builder. */
        static NotificationBuilder builder() {
            return new NotificationBuilder();
        }

        /** Shortcut for a simple one-liner chat message. */
        static Notification simple(String type, String message) {
            return builder().type(type).body(message).build();
        }
    }

    /**
     * Fluent builder for {@link Notification}.
     */
    final class NotificationBuilder {

        private String type  = "generic";
        private String title = "";
        private String body  = "";
        private final java.util.Map<String, String> placeholders = new java.util.LinkedHashMap<>();

        public NotificationBuilder type(String type) {
            this.type = type;
            return this;
        }

        public NotificationBuilder title(String title) {
            this.title = title;
            return this;
        }

        public NotificationBuilder body(String body) {
            this.body = body;
            return this;
        }

        /** Adds a placeholder replaced in both title and body as {@code {key}}. */
        public NotificationBuilder placeholder(String key, String value) {
            placeholders.put(key, value);
            return this;
        }

        public Notification build() {
            final String t = type, ti = title, b = body;
            final java.util.Map<String, String> ph =
                    java.util.Collections.unmodifiableMap(new java.util.LinkedHashMap<>(placeholders));

            return new Notification() {
                @Override public String getType()  { return t;  }
                @Override public String getTitle() { return ti; }
                @Override public String getBody()  { return b;  }
                @Override public String getPlaceholder(String key) { return ph.get(key); }

                private String resolve(String s) {
                    for (var e : ph.entrySet()) s = s.replace("{" + e.getKey() + "}", e.getValue());
                    return s;
                }

                @Override public String resolveTitle() { return resolve(ti); }
                @Override public String resolveBody()  { return resolve(b);  }
            };
        }
    }

    /**
     * A delivery channel that knows how to show a notification to a player.
     */
    interface NotificationChannel {

        /** Unique channel id (e.g. {@code "actionbar"}, {@code "title"}, {@code "chat"}). */
        String getId();

        /**
         * Delivers the notification to the given online player.
         *
         * @param player       the online target
         * @param notification the fully resolved notification
         */
        void deliver(Player player, Notification notification);
    }
}