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
 * <h2>Scoreboard types (core templates)</h2>
 * <ul>
 *   <li>{@code core:main}        — permanent lobby/main-world sidebar.</li>
 *   <li>{@code core:pre_lobby}   — pre-event waiting room sidebar.</li>
 *   <li>{@code core:wave_<name>} — per-wave event sidebar.</li>
 *   <li>{@code HIDDEN}           — removes the sidebar entirely.</li>
 * </ul>
 *
 * <h2>External templates</h2>
 * <p>External modules can contribute their own templates through
 * {@link IScoreboardRegistry}. Once registered, those templates can be
 * applied to players via {@link #setExternalScoreboard(Player, String)}
 * using the fully-qualified key {@code "<providerId>:<templateName>"}.</p>
 *
 * <h2>Typical usage</h2>
 * <pre>{@code
 * IScoreboardManager sb = ProudCoreAPI.get().getScoreboardManager();
 *
 * sb.setMainScoreboard(player);
 * sb.setWaveScoreboard(player, "starter");
 * sb.setExternalScoreboard(player, "mymod:arena");
 * sb.removeScoreboard(player);
 * }</pre>
 *
 * @author ProudCore Team
 * @version 1.0
 * @since 1.0
 */
public interface IScoreboardManager {

    /**
     * Sets the player's sidebar to the {@code core:main} template.
     *
     * @param player the target player; must not be {@code null}
     */
    void setMainScoreboard(Player player);

    /**
     * Sets the player's sidebar to the {@code core:pre_lobby} template.
     *
     * @param player the target player; must not be {@code null}
     */
    void setPreLobbyScoreboard(Player player);

    /**
     * Sets the player's sidebar to the {@code core:wave_<waveName>} template.
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
     * Sets the player's sidebar to any template registered in the
     * {@link IScoreboardRegistry}, including both core and external templates.
     *
     * <p>The {@code fullKey} must be in the form {@code "<namespace>:<name>"},
     * e.g. {@code "core:main"}, {@code "mymod:arena"}. If the template does
     * not exist in the registry, the sidebar is left unchanged and a warning
     * is logged.</p>
     *
     * <pre>{@code
     * sb.setExternalScoreboard(player, "mymod:arena");
     * }</pre>
     *
     * @param player  the target player; must not be {@code null}
     * @param fullKey the fully-qualified template key; must not be {@code null}
     */
    void setExternalScoreboard(Player player, String fullKey);

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
     * Returns the fully-qualified template key currently applied to the given
     * player, or {@code null} if the player has no managed scoreboard or the
     * sidebar is hidden.
     *
     * <p>Example return values: {@code "core:main"}, {@code "mymod:arena"},
     * {@code null}.</p>
     *
     * @param player the target player; must not be {@code null}
     * @return the current template key, or {@code null}
     */
    String getCurrentTemplate(Player player);

    /**
     * Reloads the scoreboard configuration from disk, refreshes the
     * {@code core:*} templates in the registry, and marks all active
     * scoreboards dirty so they update on the next cycle.
     *
     * <p>External provider templates are not affected by this call.</p>
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