package it.proud.api.data;

import org.bukkit.Location;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a clan in the ProudCore system.
 * <p>
 * A clan is a group of players with a leader, members, collective power, and
 * the ability to claim territory. This interface provides read-only access
 * to all essential clan information.
 * </p>
 * <p>
 * <b>Usage example:</b>
 * </p>
 * <pre>{@code
 * IClan clan = clanManager.getClan("MyClan");
 * if (clan != null) {
 *     System.out.println("Leader: " + clan.getLeader());
 *     System.out.println("Power: " + clan.getTotalPower());
 *     System.out.println("Members: " + clan.getMembers().size());
 * }
 * }</pre>
 *
 * @author ProudCore Team
 * @version 1.0
 * @since 1.0
 */
public interface IClan {
    /**
     * Gets the unique clan name.
     * <p>
     * This is the clan's primary identifier, typically lowercase
     * and without spaces.
     * </p>
     *
     * @return the clan name
     */
    String getName();
    
    /**
     * Gets the display name of the clan.
     * <p>
     * This is the formatted name shown to players, which may contain
     * colors, formatting, and special characters.
     * </p>
     *
     * @return the clan display name
     */
    String getDisplayName();
    
    /**
     * Gets the clan description.
     * <p>
     * A brief description or motto for the clan set by the leader.
     * </p>
     *
     * @return the clan description
     */
    String getDescription();
    
    /**
     * Gets the UUID of the clan leader.
     * <p>
     * The leader has special privileges such as inviting/kicking members,
     * managing claims, and modifying clan settings.
     * </p>
     *
     * @return the leader's UUID
     */
    UUID getLeader();
    
    /**
     * Gets the set of all clan members.
     * <p>
     * Includes both the leader and all regular members. The returned set
     * is immutable to prevent unauthorized modifications.
     * </p>
     *
     * @return an unmodifiable set containing the UUIDs of all members
     */
    Set<UUID> getMembers();
    
    /**
     * Checks if a player is a member of the clan.
     *
     * @param uuid the UUID of the player to check
     * @return {@code true} if the player is a member, {@code false} otherwise
     */
    boolean isMember(UUID uuid);
    
    /**
     * Checks if a player is the clan leader.
     *
     * @param uuid the UUID of the player to check
     * @return {@code true} if the player is the leader, {@code false} otherwise
     */
    boolean isLeader(UUID uuid);
    
    /**
     * Gets the clan's current total power.
     * <p>
     * The total power is the sum of all members' power and is used
     * to determine how many chunks the clan can claim. Power can
     * decrease based on member deaths.
     * </p>
     *
     * @return the clan's current total power
     */
    double getTotalPower();
    
    /**
     * Gets the clan's total maximum power.
     * <p>
     * This is the upper limit of power the clan can have, based
     * on the number of members and their individual limits.
     * </p>
     *
     * @return the clan's total maximum power
     */
    double getTotalMaxPower();
    
    /**
     * Gets the number of chunks currently claimed by the clan.
     * <p>
     * The number of possible claims is typically limited by
     * the clan's total power.
     * </p>
     *
     * @return the count of claimed chunks
     */
    int getClaimsCount();
    
    /**
     * Gets the location of the clan's home.
     * <p>
     * The home is a safe teleport point for clan members,
     * typically set within claimed territory.
     * </p>
     *
     * @return the clan home location, or {@code null} if not set
     */
    Location getHome();
}