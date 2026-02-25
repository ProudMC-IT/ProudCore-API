package it.proud.api.managers;

import org.bukkit.entity.Player;

/**
 * Manager for per-player god mode (invulnerability).
 *
 * <p>{@code IGodManager} tracks which players have god mode active and
 * synchronises the Bukkit {@code invulnerable} flag accordingly. God mode is
 * <em>not</em> persisted across restarts; it is cleared automatically when a
 * player disconnects.</p>
 *
 * <h2>Typical usage</h2>
 * <pre>{@code
 * IGodManager god = ProudCoreAPI.get().getGodManager();
 *
 * // Toggle god mode
 * boolean nowGod = god.toggle(player);
 *
 * // Check from another plugin (e.g. to skip damage logic)
 * if (god.isGod(player)) return;
 *
 * // Force-disable god mode
 * god.clear(player);
 * }</pre>
 *
 * @author  ProudCore Team
 * @version 1.0
 * @since   1.0
 */
public interface IGodManager {

    /**
     * Returns {@code true} if the given player currently has god mode active.
     *
     * @param player the player to check; must not be {@code null}
     * @return {@code true} if the player is invulnerable via god mode
     */
    boolean isGod(Player player);

    /**
     * Toggles god mode for the given player, updating the Bukkit
     * {@code invulnerable} flag immediately.
     *
     * @param player the target player; must not be {@code null}
     * @return {@code true} if god mode is now <em>active</em>,
     *         {@code false} if it is now <em>inactive</em>
     */
    boolean toggle(Player player);

    /**
     * Disables god mode for the given player and sets them as vulnerable.
     *
     * <p>Typically called on player quit to ensure a clean state.</p>
     *
     * @param player the target player; must not be {@code null}
     */
    void clear(Player player);
}