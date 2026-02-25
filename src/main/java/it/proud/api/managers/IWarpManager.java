package it.proud.api.managers;

import org.bukkit.Location;

import java.util.Map;

/**
 * Manager for global warp points.
 *
 * <p>{@code IWarpManager} manages the server-wide registry of named warp
 * destinations. Warps are shared across all players and persisted automatically.</p>
 *
 * <h2>Typical usage</h2>
 * <pre>{@code
 * IWarpManager warps = ProudCoreAPI.get().getWarpManager();
 *
 * // Teleport a player to a warp
 * Location loc = warps.getWarp("spawn");
 * if (loc != null) player.teleport(loc);
 *
 * // Create a new warp at the player's current position
 * warps.setWarp("market", player.getLocation());
 *
 * // Remove a warp
 * warps.deleteWarp("oldbase");
 * }</pre>
 *
 * @author  ProudCore Team
 * @version 1.0
 * @since   1.0
 */
public interface IWarpManager {

    /**
     * Returns an unmodifiable view of all registered warps, keyed by name (lowercase).
     *
     * <p>The map may be empty but is never {@code null}.</p>
     *
     * @return an unmodifiable {@code Map<name, Location>} of all warps
     */
    Map<String, Location> getWarps();

    /**
     * Returns the {@link Location} of the named warp, or {@code null} if it
     * does not exist.
     *
     * @param name the warp name (case-insensitive); must not be {@code null}
     * @return the warp {@link Location}, or {@code null}
     */
    Location getWarp(String name);

    /**
     * Creates or overwrites a warp with the given name at the specified location.
     *
     * <p>The change is persisted automatically.</p>
     *
     * @param name the warp name (case-insensitive); must not be {@code null} or blank
     * @param loc  the location to store; must not be {@code null}
     * @return {@code true} always (future implementations may return {@code false} on error)
     */
    boolean setWarp(String name, Location loc);

    /**
     * Deletes the named warp.
     *
     * <p>The change is persisted automatically.</p>
     *
     * @param name the warp name (case-insensitive); must not be {@code null}
     * @return {@code true} if the warp existed and was deleted,
     *         {@code false} if no warp with that name was found
     */
    boolean deleteWarp(String name);

    /**
     * Returns {@code true} if a warp with the given name exists.
     *
     * @param name the warp name (case-insensitive); must not be {@code null}
     * @return {@code true} if the warp exists
     */
    default boolean hasWarp(String name) {
        return getWarp(name) != null;
    }

    /**
     * Returns the total number of registered warps.
     *
     * @return warp count; always {@code >= 0}
     */
    default int getWarpCount() {
        return getWarps().size();
    }
}