package it.proud.api.managers;

import org.bukkit.entity.Player;

/**
 * Manager for the server's sidebar scoreboard system.
 *
 * <p>{@code IScoreboardManager} controls per-player sidebars with configurable
 * templates (defined in {@code scoreboard/config.yml}) and optional
 * PlaceholderAPI support. Scoreboards are updated asynchronously and applied
 * on the main thread at a configurable tick rate.</p>
 *
 * <h2>Scoreboard types</h2>
 * <ul>
 *   <li>{@code MAIN}      — permanent lobby/main-world sidebar.</li>
 *   <li>{@code PRE_LOBBY} — pre-event waiting room sidebar.</li>
 *   <li>{@code WAVE}      — per-wave event sidebar; requires a wave name.</li>
 *   <li>{@code HIDDEN}    — removes the sidebar entirely.</li>
 * </ul>
 *
 * <h2>Typical usage</h2>
 * <pre>{@code
 * IScoreboardManager sb = ProudCoreAPI.get().getScoreboardManager();
 *
 * // Show main scoreboard to a player
 * sb.setMainScoreboard(player);
 *
 * // Show wave scoreboard
 * sb.setWaveScoreboard(player, "starter");
 *
 * // Remove scoreboard
 * sb.removeScoreboard(player);
 * }</pre>
 *
 * @author  ProudCore Team
 * @version 1.0
 * @since   1.0
 */
public interface IScoreboardManager {

    /**
     * Sets the player's sidebar to the {@code MAIN} template.
     *
     * @param player the target player; must not be {@code null}
     */
    void setMainScoreboard(Player player);

    /**
     * Sets the player's sidebar to the {@code PRE_LOBBY} template.
     *
     * @param player the target player; must not be {@code null}
     */
    void setPreLobbyScoreboard(Player player);

    /**
     * Sets the player's sidebar to the {@code WAVE} template for the given wave.
     *
     * <p>If no scoreboard data is found for {@code waveName}, the sidebar is
     * left unchanged and a warning is logged.</p>
     *
     * @param player   the target player; must not be {@code null}
     * @param waveName the wave identifier matching a key in
     *                 {@code scoreboard/config.yml}; must not be {@code null}
     */
    void setWaveScoreboard(Player player, String waveName);

    /**
     * Removes the sidebar scoreboard from the player, restoring the server's
     * default scoreboard.
     *
     * @param player the target player; must not be {@code null}
     */
    void removeScoreboard(Player player);

    /**
     * Returns {@code true} if the player currently has an active sidebar
     * managed by ProudCore.
     *
     * @param player the target player; must not be {@code null}
     * @return {@code true} if a managed scoreboard is active for this player
     */
    boolean hasScoreboard(Player player);

    /**
     * Reloads the scoreboard configuration from disk and refreshes all active
     * scoreboards on the next update cycle.
     *
     * <p>Call this after any external modification to
     * {@code scoreboard/config.yml} to pick up the changes without a
     * server restart.</p>
     */
    void reload();

    /**
     * Shuts down the scoreboard manager: cancels all scheduled tasks and
     * removes all active scoreboards.
     *
     * <p><b>Internal use only.</b> Called by ProudCore during
     * {@code onDisable()}.</p>
     */
    void shutdown();
}