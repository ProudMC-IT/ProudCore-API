package it.proud.api.managers;

import it.proud.api.data.IPlayerData;
import java.util.UUID;

/**
 * Manager for persistent player data in the ProudCore system.
 * <p>
 * This manager handles loading, caching, and saving of player-specific
 * data, including statistics, power, and configurations.
 * Data is automatically loaded when a player connects
 * and saved periodically or on exit.
 * </p>
 * <p>
 * <b>Caching System:</b><br>
 * Online player data is kept in cache for optimal performance.
 * Use {@link #isLoaded(UUID)} to check if data is in memory before
 * bulk operations.
 * </p>
 * <p>
 * <b>Usage example:</b>
 * </p>
 * <pre>{@code
 * IPlayerManager playerManager = api.getPlayerManager();
 * 
 * // Get player data
 * IPlayerData data = playerManager.getPlayer(playerUuid);
 * double power = data.getPower();
 * 
 * // Check if data is loaded
 * if (playerManager.isLoaded(playerUuid)) {
 *     // Safe cached operations
 *     data.addPower(5.0);
 * }
 * 
 * // Save all data (typically on shutdown)
 * playerManager.saveAll();
 * }</pre>
 *
 * @author ProudCore Team
 * @version 1.0
 * @since 1.0
 */
public interface IPlayerManager {
    /**
     * Gets a player's data.
     * <p>
     * Retrieves or loads the specified player's data. If data is not
     * in cache, it is loaded from the database. This method never returns
     * {@code null} - it creates new data if the player doesn't exist.
     * </p>
     * <p>
     * <b>Note:</b> For offline players, there may be a slight delay
     * when loading from the database on the first call.
     * </p>
     *
     * @param uuid the player's UUID
     * @return the player data, never {@code null}
     */
    IPlayerData getPlayer(UUID uuid);
    
    /**
     * Checks if a player's data is currently loaded in memory.
     * <p>
     * Useful for optimizations and to avoid database loading during
     * batch operations. Online players always have their data loaded.
     * </p>
     *
     * @param uuid the UUID of the player to check
     * @return {@code true} if data is cached, {@code false} otherwise
     */
    boolean isLoaded(UUID uuid);
    
    /**
     * Saves all currently loaded player data.
     * <p>
     * Forces immediate persistence of all cached data to disk/database.
     * This method is typically called during server shutdown or before
     * critical operations that require data consistency.
     * </p>
     * <p>
     * <b>Note:</b> Saving is normally automatic and periodic. This
     * method is primarily for exceptional situations or controlled shutdown.
     * </p>
     */
    void saveAll();
}