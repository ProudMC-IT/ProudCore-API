package it.proud.api.managers;

import org.bukkit.Location;

/**
 * Manager for the server's global spawn point.
 *
 * <p>{@code ISpawnManager} exposes read and write access to the single server
 * spawn location used for join teleports and the {@code /spawn} command.
 * The spawn is persisted automatically; callers do not need to invoke any
 * explicit save method.</p>
 *
 * <h2>Typical usage</h2>
 * <pre>{@code
 * ISpawnManager spawn = ProudCoreAPI.get().getSpawnManager();
 *
 * // Teleport a player to spawn
 * Location loc = spawn.getSpawn();
 * if (loc != null) player.teleport(loc);
 *
 * // Override the spawn from code
 * spawn.setSpawn(player.getLocation());
 * }</pre>
 *
 * @author  ProudCore Team
 * @version 1.0
 * @since   1.0
 */
public interface ISpawnManager {

    /**
     * Returns the current server spawn location, or {@code null} if it has
     * not been set yet.
     *
     * <p>The returned {@link Location} is a live reference; mutating it may
     * affect the stored value depending on the implementation. Prefer cloning
     * the location if you intend to modify it.</p>
     *
     * @return the spawn {@link Location}, or {@code null} if not configured
     */
    Location getSpawn();

    /**
     * Sets the server spawn to the given location and persists the change.
     *
     * @param loc the new spawn location; must not be {@code null}
     */
    void setSpawn(Location loc);

    /**
     * Returns {@code true} if a spawn point has been configured.
     *
     * <p>Equivalent to {@code getSpawn() != null}.</p>
     *
     * @return {@code true} if the spawn is set
     */
    default boolean isSpawnSet() {
        return getSpawn() != null;
    }
}