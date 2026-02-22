package it.proud.api.data;

import java.util.UUID;

/**
 * Snapshot of a player's base Minecraft statistics.
 *
 * <p>Values are sourced from {@link org.bukkit.Statistic} and are therefore
 * cumulative since the player first joined the server. All counts are
 * non-negative integers.</p>
 *
 * @author  ProudCore Team
 * @version 1.0
 * @since   1.0
 * @see     it.proud.api.managers.IPlayerStatsManager
 */
public interface IPlayerStats {

    /**
     * Returns the UUID of the player this snapshot belongs to.
     *
     * @return the non-{@code null} player UUID
     */
    UUID getUuid();

    /**
     * Returns the last-known username associated with this record.
     *
     * @return the non-{@code null} username
     */
    String getName();

    /**
     * Returns the total number of player-kills (PvP kills).
     * Maps to {@link org.bukkit.Statistic#PLAYER_KILLS}.
     *
     * @return total PvP kills; always {@code >= 0}
     */
    long getKills();

    /**
     * Returns the total number of deaths.
     * Maps to {@link org.bukkit.Statistic#DEATHS}.
     *
     * @return total deaths; always {@code >= 0}
     */
    long getDeaths();

    /**
     * Returns the total number of mob kills.
     * Maps to {@link org.bukkit.Statistic#MOB_KILLS}.
     *
     * @return total mob kills; always {@code >= 0}
     */
    long getMobKills();

    /**
     * Returns the Kill/Death ratio as a double, or {@code 0.0} if the player
     * has no deaths yet (to avoid division by zero).
     *
     * @return K/D ratio; always {@code >= 0}
     */
    double getKdr();

    /**
     * Returns the total distance walked in centimetres (Minecraft stat unit).
     * Maps to {@link org.bukkit.Statistic#WALK_ONE_CM}.
     *
     * @return cm walked; always {@code >= 0}
     */
    long getWalkOneCm();

    /**
     * Returns the total playtime in ticks (20 ticks = 1 second).
     * Maps to {@link org.bukkit.Statistic#PLAY_ONE_MINUTE}.
     *
     * @return ticks played; always {@code >= 0}
     */
    long getPlayTimeTicks();

    /**
     * Convenience method: returns playtime in whole seconds.
     *
     * @return seconds played; always {@code >= 0}
     */
    default long getPlayTimeSeconds() {
        return getPlayTimeTicks() / 20L;
    }

    /**
     * Convenience method: returns playtime in whole minutes.
     *
     * @return minutes played; always {@code >= 0}
     */
    default long getPlayTimeMinutes() {
        return getPlayTimeSeconds() / 60L;
    }

    /**
     * Returns the total number of items crafted.
     * Maps to {@link org.bukkit.Statistic#CRAFT_ITEM} (sum across all items).
     *
     * <p>Note: Bukkit's CRAFT_ITEM is per-material; this value is pre-aggregated
     * by the implementation.</p>
     *
     * @return total items crafted; always {@code >= 0}
     */
    long getItemsCrafted();

    /**
     * Returns the total jumps performed.
     * Maps to {@link org.bukkit.Statistic#JUMP}.
     *
     * @return total jumps; always {@code >= 0}
     */
    long getJumps();

    /**
     * Returns the unix-millis timestamp of when this snapshot was last refreshed.
     *
     * @return snapshot timestamp in milliseconds
     */
    long getLastUpdated();
}