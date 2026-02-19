package it.proud.api.data;

import java.util.UUID;

/**
 * Represents persistent player data in the ProudCore system.
 * <p>
 * This interface provides access and modification of player-specific data,
 * including power, claim limits, and basic information. Data is automatically
 * persisted and synchronized with the database.
 * </p>
 * <p>
 * <b>Power System:</b><br>
 * Power is a resource that determines how many territorial claims a
 * player (and their clan) can maintain. Power increases over time and can
 * decrease with deaths.
 * </p>
 * <p>
 * <b>Usage example:</b>
 * </p>
 * <pre>{@code
 * IPlayerData data = playerManager.getPlayer(uuid);
 * double currentPower = data.getPower();
 * data.addPower(10.0);
 * System.out.println("Power: " + data.getPower() + "/" + data.getMaxPower());
 * }</pre>
 *
 * @author ProudCore Team
 * @version 1.0
 * @since 1.0
 */
public interface IPlayerData {
    /**
     * Gets the player's unique UUID.
     *
     * @return the player's UUID
     */
    UUID getUuid();
    
    /**
     * Gets the player's current name.
     * <p>
     * This name is automatically updated when the player
     * connects to the server.
     * </p>
     *
     * @return the player's name
     */
    String getName();
    
    /**
     * Gets the player's current power.
     * <p>
     * Power contributes to the clan's total power and determines the ability
     * to claim territory. Cannot exceed {@link #getMaxPower()}.
     * </p>
     *
     * @return the current power value
     */
    double getPower();
    
    /**
     * Gets the player's maximum power.
     * <p>
     * This is the upper limit of power the player can accumulate.
     * Can be modified by permissions or admin commands.
     * </p>
     *
     * @return the maximum power value
     */
    double getMaxPower();
    
    /**
     * Gets the player's claimable chunk limit.
     * <p>
     * Represents the maximum number of chunks this player can
     * personally claim, independent of power. Used for
     * personal claim systems.
     * </p>
     *
     * @return the claimable chunk limit
     */
    int getChunkLimit();
    
    /**
     * Sets the player's current power.
     * <p>
     * The value will be automatically clamped between 0 and {@link #getMaxPower()}.
     * Changes are automatically persisted.
     * </p>
     *
     * @param power the new power value
     */
    void setPower(double power);
    
    /**
     * Sets the player's maximum power.
     * <p>
     * Modifies the upper limit of accumulable power. If the current
     * power exceeds the new maximum, it is automatically reduced.
     * </p>
     *
     * @param maxPower the new maximum power value
     */
    void setMaxPower(double maxPower);
    
    /**
     * Sets the claimable chunk limit.
     * <p>
     * Modifies the maximum number of chunks the player can claim.
     * Typically used by admin commands or permission systems.
     * </p>
     *
     * @param chunkLimit the new chunk limit
     */
    void setChunkLimit(int chunkLimit);
    
    /**
     * Adds power to the player.
     * <p>
     * Increases the current power by the specified amount, up to
     * the limit of {@link #getMaxPower()}. Negative values are ignored.
     * </p>
     *
     * @param amount the amount of power to add (must be positive)
     */
    void addPower(double amount);
    
    /**
     * Removes power from the player.
     * <p>
     * Decreases the current power by the specified amount, with a minimum
     * of 0. Typically called when a player dies. Negative values
     * are ignored.
     * </p>
     *
     * @param amount the amount of power to remove (must be positive)
     */
    void takePower(double amount);
}