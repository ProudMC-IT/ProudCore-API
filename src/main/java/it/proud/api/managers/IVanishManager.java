package it.proud.api.managers;

import org.bukkit.entity.Player;

/**
 * Manager for per-player vanish state.
 *
 * <p>{@code IVanishManager} controls whether a player is hidden from other
 * online players. Visibility changes are applied immediately to all current
 * online players. Vanish state is <em>not</em> persisted across restarts; it
 * is cleared automatically when a player disconnects.</p>
 *
 * <h2>Typical usage</h2>
 * <pre>{@code
 * IVanishManager vanish = ProudCoreAPI.get().getVanishManager();
 *
 * // Toggle vanish for a player
 * boolean nowVanished = vanish.toggle(player);
 *
 * // Check from another plugin (e.g. to skip vanished players in a broadcast)
 * if (!vanish.isVanished(player)) {
 *     player.sendMessage(broadcast);
 * }
 *
 * // Force-reveal a player
 * vanish.setVanished(player, false);
 * }</pre>
 *
 * @author  ProudCore Team
 * @version 1.0
 * @since   1.0
 */
public interface IVanishManager {

    /**
     * Returns {@code true} if the given player is currently vanished.
     *
     * @param player the player to check; must not be {@code null}
     * @return {@code true} if the player is hidden from others
     */
    boolean isVanished(Player player);

    /**
     * Explicitly sets the vanish state of a player.
     *
     * <p>Visibility changes are applied immediately to all online players.
     * If the state is already equal to {@code vanished}, the call is a no-op
     * for the toggle logic but visibility is still refreshed.</p>
     *
     * @param player   the target player; must not be {@code null}
     * @param vanished {@code true} to hide the player, {@code false} to reveal
     * @return the new state (same as {@code vanished})
     */
    boolean setVanished(Player player, boolean vanished);

    /**
     * Toggles the vanish state of a player.
     *
     * <p>If the player is currently visible they are hidden, and vice versa.
     * Visibility changes are applied immediately.</p>
     *
     * @param player the target player; must not be {@code null}
     * @return {@code true} if the player is now vanished,
     *         {@code false} if they are now visible
     */
    boolean toggle(Player player);

    /**
     * Reveals the player to all online players and clears their vanish state.
     *
     * <p>Typically called on player quit to ensure a clean state.</p>
     *
     * @param player the target player; must not be {@code null}
     */
    void clear(Player player);

    /**
     * Re-applies vanish visibility for a newly joining viewer.
     *
     * <p>Should be called in a {@code PlayerJoinEvent} handler so that the
     * joining player cannot see anyone who is currently vanished.</p>
     *
     * @param viewer the player who just joined; must not be {@code null}
     */
    void handleJoin(Player viewer);
}