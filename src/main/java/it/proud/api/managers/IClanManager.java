package it.proud.api.managers;

import it.proud.api.data.IClan;
import it.proud.api.data.IPlayerData;
import org.bukkit.Chunk;

import java.util.Map;
import java.util.UUID;

/**
 * Main manager for all clan and territorial claim operations.
 * <p>
 * This manager provides a complete interface for clan management, including
 * creation, disbanding, member management, and control of territorial claims.
 * It also handles integration with player data related to the clan system.
 * </p>
 * <p>
 * <b>Main features:</b>
 * </p>
 * <ul>
 *   <li>Clan creation and management</li>
 *   <li>Territorial claim system (chunk claims)</li>
 *   <li>Power and member management</li>
 *   <li>Player data integration</li>
 * </ul>
 * <p>
 * <b>Usage example:</b>
 * </p>
 * <pre>{@code
 * IClanManager clanManager = api.getClanManager();
 * 
 * // Check if a player is in a clan
 * if (clanManager.isInClan(playerUuid)) {
 *     IClan clan = clanManager.getPlayerClan(playerUuid);
 *     player.sendMessage("You are in " + clan.getDisplayName());
 * }
 * 
 * // Create a new clan
 * boolean success = clanManager.createClan("MyClan", leaderUuid);
 * 
 * // Check chunk owner
 * IClan owner = clanManager.getClaimOwner(chunk);
 * if (owner != null) {
 *     System.out.println("This chunk belongs to " + owner.getName());
 * }
 * }</pre>
 *
 * @author ProudCore Team
 * @version 1.0
 * @since 1.0
 */
public interface IClanManager {
    /**
     * Gets a clan by its name.
     *
     * @param name the unique clan name
     * @return the clan instance, or {@code null} if it doesn't exist
     */
    IClan getClan(String name);
    
    /**
     * Gets the clan of which a player is a member.
     * <p>
     * Returns the clan to which the specified player belongs,
     * either as leader or regular member.
     * </p>
     *
     * @param uuid the player's UUID
     * @return the player's clan, or {@code null} if not in any clan
     */
    IClan getPlayerClan(UUID uuid);
    
    /**
     * Checks if a player is a member of a clan.
     * <p>
     * Utility method to quickly check clan membership
     * without retrieving the complete clan object.
     * </p>
     *
     * @param uuid the UUID of the player to check
     * @return {@code true} if the player is in a clan, {@code false} otherwise
     */
    boolean isInClan(UUID uuid);
    
    /**
     * Creates a new clan with the specified leader.
     * <p>
     * Initializes a new clan with the provided name and leader. The leader
     * automatically becomes the first member. Fails if the name is already in use
     * or if the leader is already in another clan.
     * </p>
     *
     * @param name the unique name for the new clan
     * @param leader the UUID of the player who will be the leader
     * @return {@code true} if the clan was created successfully, {@code false} otherwise
     */
    boolean createClan(String name, UUID leader);
    
    /**
     * Disbands an existing clan.
     * <p>
     * Permanently removes the clan, freeing all its members and
     * releasing all territorial claims. This action is irreversible.
     * </p>
     *
     * @param name the name of the clan to disband
     * @return {@code true} if the clan was disbanded, {@code false} if it didn't exist
     */
    boolean disbandClan(String name);
    
    /**
     * Gets all existing clans on the server.
     * <p>
     * Returns an immutable map of all registered clans, indexed
     * by clan name. Useful for statistics and bulk operations.
     * </p>
     *
     * @return a name→clan map of all existing clans
     */
    Map<String, ? extends IClan> getAllClans();
    
    /**
     * Gets a player's data related to the clan system.
     * <p>
     * Provides access to player data such as power, chunk limits, and
     * statistics. This is a convenience method that internally delegates
     * to the {@code IPlayerManager}.
     * </p>
     *
     * @param uuid the player's UUID
     * @return the player data, never {@code null}
     */
    IPlayerData getPlayerData(UUID uuid);
    
    /**
     * Determines which clan has claimed a specific chunk.
     * <p>
     * Checks if the provided chunk has been claimed by a clan and returns
     * the owner. Useful for protection systems and access control.
     * </p>
     *
     * @param chunk the chunk to check
     * @return the clan owner of the chunk, or {@code null} if not claimed
     */
    IClan getClaimOwner(Chunk chunk);
    
    /**
     * Saves all clan and player data.
     * <p>
     * Forces immediate saving of all persistent data to disk/database.
     * Normally saving is automatic, but this method can be used
     * to ensure persistence before critical operations or shutdown.
     * </p>
     */
    void saveAll();
}