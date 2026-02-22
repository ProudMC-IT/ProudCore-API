package it.proud.api.managers;

import it.proud.api.data.IPlayerStats;

import java.util.Map;
import java.util.UUID;

/**
 * Manager for per-player base Minecraft statistics.
 *
 * <p>{@code IPlayerStatsManager} provides a unified, cache-backed view of the
 * standard Bukkit statistics (kills, deaths, mob kills, playtime, …) for any
 * player who has ever joined the server. Results are served from a fast
 * in-memory cache and refreshed either on demand or automatically when
 * relevant events fire (death, login, etc.).</p>
 *
 * <h2>Typical usage</h2>
 * <pre>{@code
 * IPlayerStatsManager stats = ProudCoreAPI.get().getPlayerStatsManager();
 *
 * IPlayerStats s = stats.getStats(playerUuid);
 * player.sendMessage("Kills: " + s.getKills() + " | Deaths: " + s.getDeaths()
 *         + " | K/D: " + String.format("%.2f", s.getKdr()));
 * }</pre>
 *
 * @author  ProudCore Team
 * @version 1.0
 * @since   1.0
 * @see     IPlayerStats
 */
public interface IPlayerStatsManager {

    /**
     * Returns the stats snapshot for the given player.
     *
     * <p>If the player's data is not yet cached, it is loaded synchronously
     * from Bukkit's statistics backend. Online players are guaranteed to have
     * their data cached already.</p>
     *
     * <p>This method never returns {@code null}: if the player has never joined
     * the server a zeroed-out snapshot is returned.</p>
     *
     * @param uuid the player UUID; must not be {@code null}
     * @return the non-{@code null} stats snapshot
     */
    IPlayerStats getStats(UUID uuid);

    /**
     * Refreshes the cached stats for the given player by re-reading from
     * Bukkit's statistics backend, then returns the updated snapshot.
     *
     * @param uuid the player UUID; must not be {@code null}
     * @return the refreshed, non-{@code null} stats snapshot
     */
    IPlayerStats refreshStats(UUID uuid);

    /**
     * Refreshes cached stats for every player currently in the cache.
     *
     * <p>This is an O(n) operation. Prefer calling {@link #refreshStats(UUID)}
     * for individual players when only a single player's data is stale.</p>
     */
    void refreshAll();

    /**
     * Returns {@code true} if the given player's stats are currently cached.
     *
     * @param uuid the player UUID; must not be {@code null}
     * @return {@code true} if cached
     */
    boolean isCached(UUID uuid);

    /**
     * Evicts the given player's stats from the cache.
     *
     * <p>Subsequent calls to {@link #getStats(UUID)} will re-load the data.</p>
     *
     * @param uuid the player UUID; must not be {@code null}
     */
    void evict(UUID uuid);

    /**
     * Returns an unmodifiable snapshot of all currently cached stats,
     * keyed by player UUID.
     *
     * @return a non-{@code null}, unmodifiable map
     */
    Map<UUID, ? extends IPlayerStats> getAllCached();

    /**
     * Returns the kill count for the given player.
     *
     * <p>Shortcut for {@code getStats(uuid).getKills()}.</p>
     *
     * @param uuid the player UUID; must not be {@code null}
     * @return PvP kill count; always {@code >= 0}
     */
    default long getKills(UUID uuid) {
        return getStats(uuid).getKills();
    }

    /**
     * Returns the death count for the given player.
     *
     * <p>Shortcut for {@code getStats(uuid).getDeaths()}.</p>
     *
     * @param uuid the player UUID; must not be {@code null}
     * @return death count; always {@code >= 0}
     */
    default long getDeaths(UUID uuid) {
        return getStats(uuid).getDeaths();
    }

    /**
     * Returns the K/D ratio for the given player.
     *
     * <p>Shortcut for {@code getStats(uuid).getKdr()}.</p>
     *
     * @param uuid the player UUID; must not be {@code null}
     * @return K/D ratio; always {@code >= 0}
     */
    default double getKdr(UUID uuid) {
        return getStats(uuid).getKdr();
    }
}