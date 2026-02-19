package it.proud.api.managers;

import it.proud.api.data.IPlayerData;
import java.util.UUID;

/**
 * Manager for persistent player data in the ProudCore system.
 *
 * <p>{@code IPlayerManager} is responsible for the full lifecycle of
 * {@link IPlayerData} records: loading them from the database, keeping
 * them in a fast in-memory cache while a player is online, and flushing
 * them back to storage on logout or server shutdown.</p>
 *
 * <h2>Caching behaviour</h2>
 * <p>Online players always have their data pre-loaded in memory — reads are
 * therefore instantaneous and produce no I/O. For <em>offline</em> players,
 * the first call to {@link #getPlayer(UUID)} may trigger a synchronous database
 * read if the record is not cached; use {@link #isLoaded(UUID)} to check
 * beforehand if latency is a concern.</p>
 *
 * <h2>Guaranteed non-null returns</h2>
 * <p>{@link #getPlayer(UUID)} never returns {@code null}. If no record exists
 * for the given UUID, a default one is created and cached on the spot. This
 * simplifies calling code because null-checks are unnecessary.</p>
 *
 * <h2>Usage example</h2>
 * <pre>{@code
 * IPlayerManager players = ProudCoreAPI.get().getPlayerManager();
 *
 * // Read player power — always safe, never null
 * IPlayerData data = players.getPlayer(playerUuid);
 * System.out.printf("Power: %.1f / %.1f%n", data.getPower(), data.getMaxPower());
 *
 * // Efficient bulk operation — only iterate already-loaded data
 * for (Player online : Bukkit.getOnlinePlayers()) {
 *     UUID uuid = online.getUniqueId();
 *     if (players.isLoaded(uuid)) {
 *         players.getPlayer(uuid).addPower(1.0); // passive regen tick
 *     }
 * }
 * }</pre>
 *
 * @author  ProudCore Team
 * @version 1.0
 * @since   1.0
 * @see     IPlayerData
 * @see     IClanManager#getPlayerData(UUID)
 */
public interface IPlayerManager {

    /**
     * Returns the {@link IPlayerData} record for the given player, loading it
     * from the database if necessary.
     *
     * <p>This method guarantees a non-{@code null} return value: if no existing
     * record is found for the UUID, a fresh default record is created, cached,
     * and returned. There is therefore no need to null-check the result.</p>
     *
     * <p><b>Performance note:</b> For online players, data is served directly
     * from the in-memory cache with no I/O overhead. For offline players whose
     * data is not cached, a synchronous database read occurs on the first call.
     * If you need to avoid that latency, guard the call with
     * {@link #isLoaded(UUID)}.</p>
     *
     * @param uuid the UUID of the player whose data is requested;
     *             must not be {@code null}
     * @return the non-{@code null} {@link IPlayerData} for that player
     */
    IPlayerData getPlayer(UUID uuid);

    /**
     * Returns {@code true} if the given player's data is currently held in the
     * in-memory cache.
     *
     * <p>All online players are guaranteed to have their data cached. Offline
     * players may or may not be cached depending on whether their data was
     * loaded for a recent operation and has not yet been evicted.</p>
     *
     * <p>This method is particularly useful before performing bulk operations
     * over a large player set: iterating only over cached players avoids
     * triggering unintended database reads for every offline UUID.</p>
     *
     * <pre>{@code
     * if (!playerManager.isLoaded(uuid)) {
     *     // Skip — loading this offline player's data just for a stat update
     *     // is not worth the I/O cost.
     *     return;
     * }
     * playerManager.getPlayer(uuid).addPower(bonus);
     * }</pre>
     *
     * @param uuid the UUID of the player to check; must not be {@code null}
     * @return {@code true} if the player's data is in the cache,
     *         {@code false} if it would require a database load
     */
    boolean isLoaded(UUID uuid);

    /**
     * Forces an immediate flush of all cached player data to persistent storage.
     *
     * <p>Under normal operation, ProudCore persists data automatically at
     * configurable intervals and whenever a player disconnects. Call this
     * method explicitly only in situations where data integrity is critical
     * and cannot wait for the next scheduled save — for example:</p>
     * <ul>
     *   <li>Just before a controlled server shutdown initiated by a plugin.</li>
     *   <li>After a bulk administrative operation that modified many records.</li>
     *   <li>As a safety net before executing a risky or irreversible game action.</li>
     * </ul>
     *
     * <p><b>Threading note:</b> Depending on the implementation, this call may
     * block until all I/O completes. Avoid invoking it on the main server thread
     * in latency-sensitive code paths.</p>
     */
    void saveAll();
}