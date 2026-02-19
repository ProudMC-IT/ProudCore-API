package it.proud.api.data;

import java.util.UUID;

/**
 * Persistent data record for a single player in the ProudCore system.
 *
 * <p>{@code IPlayerData} provides both read and write access to the player-specific
 * values that feed into the clan and territory systems. All mutations are automatically
 * validated and scheduled for persistence; callers do not need to invoke any explicit
 * save method after modifying values through this interface.</p>
 *
 * <h2>Power system</h2>
 * <p>Power is the core resource that controls territorial influence:</p>
 * <ul>
 *   <li>Each player has a <em>current power</em> value capped by their
 *       <em>maximum power</em>.</li>
 *   <li>A clan's total power is the sum of all its members' current power values,
 *       and governs how many chunks the clan may claim.</li>
 *   <li>Power is reduced by {@link #takePower(double)} on death and restored over
 *       time or by administrative action.</li>
 *   <li>Both the current and maximum values can be overridden programmatically
 *       (e.g. by rank plugins or admin commands) via the setter methods.</li>
 * </ul>
 *
 * <h2>Chunk limit</h2>
 * <p>In addition to power-based claim limits, each player may have a personal
 * chunk limit ({@link #getChunkLimit()}) that acts as a hard cap on the number of
 * chunks they can individually hold, independent of power.</p>
 *
 * <h2>Usage example</h2>
 * <pre>{@code
 * IPlayerData data = playerManager.getPlayer(playerUuid);
 *
 * System.out.printf("Power: %.1f / %.1f%n", data.getPower(), data.getMaxPower());
 *
 * // Reward a player on a kill streak
 * data.addPower(5.0);
 *
 * // Penalise a player on death
 * data.takePower(3.0);
 * }</pre>
 *
 * @author  ProudCore Team
 * @version 1.0
 * @since   1.0
 * @see     it.proud.api.managers.IPlayerManager
 * @see     IClan
 */
public interface IPlayerData {

    /**
     * Returns the player's unique identifier.
     *
     * <p>This UUID is immutable and corresponds directly to the Bukkit/Mojang
     * player UUID. It is used as the primary key for all storage and cache lookups.</p>
     *
     * @return the non-{@code null} UUID that uniquely identifies this player
     */
    UUID getUuid();

    /**
     * Returns the player's most recently known username.
     *
     * <p>This value is refreshed automatically every time the player connects to
     * the server. It may therefore lag behind by one session for players who have
     * changed their Minecraft username since their last login.</p>
     *
     * @return the non-{@code null} last-known username of the player
     */
    String getName();

    /**
     * Returns the player's current power.
     *
     * <p>The value is always in the range {@code [0, getMaxPower()]}. It represents
     * the player's individual contribution to their clan's total power pool, and
     * therefore directly affects how many territorial chunks the clan can hold.</p>
     *
     * @return the current power value; always {@code >= 0} and {@code <= getMaxPower()}
     * @see #getMaxPower()
     */
    double getPower();

    /**
     * Returns the player's maximum power cap.
     *
     * <p>Current power can never exceed this value. The cap can be raised or
     * lowered by permission plugins, rank systems, or direct administrative
     * calls to {@link #setMaxPower(double)}.</p>
     *
     * @return the maximum power value; always {@code > 0}
     * @see #getPower()
     */
    double getMaxPower();

    /**
     * Returns the maximum number of chunks this player is personally allowed
     * to claim.
     *
     * <p>This is an independent cap that operates alongside (but separately from)
     * the power-based claim limit. It is typically managed by permission tiers or
     * donor rank plugins. A value of {@code 0} means the player cannot personally
     * claim any chunks.</p>
     *
     * @return the personal chunk-claim limit; always {@code >= 0}
     */
    int getChunkLimit();

    /**
     * Sets the player's current power to the specified value.
     *
     * <p>The provided value is automatically clamped to the range
     * {@code [0, getMaxPower()]}: values below zero are treated as zero, and
     * values above the maximum are silently reduced to the maximum. The change is
     * scheduled for persistence automatically.</p>
     *
     * @param power the desired power value; clamped to {@code [0, getMaxPower()]}
     */
    void setPower(double power);

    /**
     * Sets the player's maximum power cap.
     *
     * <p>If the player's current power exceeds the new maximum, it is automatically
     * reduced to match. Setting this value below the current power effectively
     * applies an immediate {@link #setPower(double)} as a side effect. Changes are
     * persisted automatically.</p>
     *
     * @param maxPower the new maximum power; must be {@code > 0}
     */
    void setMaxPower(double maxPower);

    /**
     * Sets the player's personal chunk-claim limit.
     *
     * <p>This directly overrides the current limit. Typically used by rank or
     * permission plugins to grant additional claiming capacity. The change is
     * persisted automatically.</p>
     *
     * @param chunkLimit the new chunk-claim limit; must be {@code >= 0}
     */
    void setChunkLimit(int chunkLimit);

    /**
     * Increases the player's current power by the given amount.
     *
     * <p>The resulting power is capped at {@link #getMaxPower()}; any surplus is
     * silently discarded. Negative or zero values are ignored and produce no change.
     * The update is persisted automatically.</p>
     *
     * <pre>{@code
     * // Grant 10 power on capturing a flag
     * data.addPower(10.0);
     * }</pre>
     *
     * @param amount the amount to add; values {@code <= 0} are ignored
     * @see #takePower(double)
     */
    void addPower(double amount);

    /**
     * Decreases the player's current power by the given amount.
     *
     * <p>The resulting power cannot fall below {@code 0}; any excess reduction is
     * silently clamped. Negative or zero values are ignored and produce no change.
     * The update is persisted automatically. This method is typically called in
     * response to player death events.</p>
     *
     * <pre>{@code
     * // Penalise 3 power on death
     * data.takePower(3.0);
     * }</pre>
     *
     * @param amount the amount to remove; values {@code <= 0} are ignored
     * @see #addPower(double)
     */
    void takePower(double amount);
}