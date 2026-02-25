package it.proud.api.managers;

import org.bukkit.Location;

import java.util.Map;
import java.util.UUID;

/**
 * Manager for per-player home locations.
 *
 * <p>{@code IHomeManager} exposes the full lifecycle of named home points:
 * creation, lookup, listing, and deletion. Homes are persisted automatically;
 * callers do not need to invoke any explicit save method.</p>
 *
 * <h2>Typical usage</h2>
 * <pre>{@code
 * IHomeManager homes = ProudCoreAPI.get().getHomeManager();
 *
 * // Teleport a player to their home
 * Location loc = homes.getHome(player.getUniqueId(), "home");
 * if (loc != null) player.teleport(loc);
 *
 * // Set a new home
 * homes.setHome(player.getUniqueId(), "base", player.getLocation());
 *
 * // Delete a home
 * homes.deleteHome(player.getUniqueId(), "base");
 * }</pre>
 *
 * @author  ProudCore Team
 * @version 1.0
 * @since   1.0
 */
public interface IHomeManager {

    /**
     * Returns an unmodifiable view of all homes belonging to the given player,
     * keyed by home name (lowercase).
     *
     * <p>The map may be empty but is never {@code null}.</p>
     *
     * @param uuid the player's UUID; must not be {@code null}
     * @return an unmodifiable {@code Map<name, Location>} of the player's homes
     */
    Map<String, Location> getHomes(UUID uuid);

    /**
     * Returns the {@link Location} of the named home, or {@code null} if it
     * does not exist.
     *
     * @param uuid the player's UUID; must not be {@code null}
     * @param name the home name (case-insensitive); must not be {@code null}
     * @return the home {@link Location}, or {@code null}
     */
    Location getHome(UUID uuid, String name);

    /**
     * Creates or overwrites the named home for the given player at the specified
     * location.
     *
     * <p>The change is persisted automatically.</p>
     *
     * @param uuid the player's UUID; must not be {@code null}
     * @param name the home name (case-insensitive); must not be {@code null} or blank
     * @param loc  the location to store; must not be {@code null}
     * @return {@code true} always (future implementations may return {@code false} on error)
     */
    boolean setHome(UUID uuid, String name, Location loc);

    /**
     * Deletes the named home for the given player.
     *
     * <p>The change is persisted automatically.</p>
     *
     * @param uuid the player's UUID; must not be {@code null}
     * @param name the home name (case-insensitive); must not be {@code null}
     * @return {@code true} if the home existed and was deleted,
     *         {@code false} if no home with that name was found
     */
    boolean deleteHome(UUID uuid, String name);

    /**
     * Returns {@code true} if the player has a home registered under the given name.
     *
     * @param uuid the player's UUID; must not be {@code null}
     * @param name the home name (case-insensitive); must not be {@code null}
     * @return {@code true} if the home exists
     */
    default boolean hasHome(UUID uuid, String name) {
        return getHome(uuid, name) != null;
    }

    /**
     * Returns the total number of homes the given player has registered.
     *
     * @param uuid the player's UUID; must not be {@code null}
     * @return home count; always {@code >= 0}
     */
    default int getHomeCount(UUID uuid) {
        return getHomes(uuid).size();
    }
}