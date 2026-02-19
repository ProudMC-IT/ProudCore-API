package it.proud.api.data;

import org.bukkit.Location;
import java.util.Set;
import java.util.UUID;

/**
 * Read-only view of a ProudCore clan.
 *
 * <p>A clan is a persistent group of players that share a collective identity,
 * territory, and a pool of power. One player holds the role of <em>leader</em>
 * and has exclusive control over administrative operations such as inviting or
 * kicking members and managing territorial claims.</p>
 *
 * <h2>Power system</h2>
 * <p>Every clan has a <em>current total power</em> and a <em>maximum total
 * power</em>. Power is the sum of all members' individual power values
 * (see {@link IPlayerData}) and directly limits the number of chunks the clan
 * may claim: if total power drops below the number of existing claims, the
 * server may automatically release excess claims according to its configured
 * policy. Power decreases when members die and recovers passively over time.</p>
 *
 * <h2>Territorial claims</h2>
 * <p>Claimed chunks are Minecraft world chunks "owned" by the clan. The maximum
 * number of claimable chunks is bounded by the clan's total power. The current
 * count of active claims is exposed via {@link #getClaimsCount()}.</p>
 *
 * <h2>Usage example</h2>
 * <pre>{@code
 * IClan clan = clanManager.getClan("spartans");
 * if (clan != null) {
 *     System.out.println("Leader : " + clan.getLeader());
 *     System.out.println("Members: " + clan.getMembers().size());
 *     System.out.printf("Power  : %.1f / %.1f%n",
 *             clan.getTotalPower(), clan.getTotalMaxPower());
 * }
 * }</pre>
 *
 * @author  ProudCore Team
 * @version 1.0
 * @since   1.0
 * @see     it.proud.api.managers.IClanManager
 * @see     IPlayerData
 */
public interface IClan {

    /**
     * Returns the clan's unique internal name.
     *
     * <p>This identifier is set at creation time, is case-sensitive, and cannot
     * be changed afterwards. It is used as the primary key in all manager lookups
     * (e.g. {@link it.proud.api.managers.IClanManager#getClan(String)}). It does
     * not carry color codes or formatting — for the decorated variant, use
     * {@link #getDisplayName()}.</p>
     *
     * @return the clan's non-{@code null}, non-blank internal name
     */
    String getName();

    /**
     * Returns the clan's display name, which may contain color codes and formatting.
     *
     * <p>The display name is the text shown to players in chat, tablist, or GUIs.
     * It can contain Minecraft legacy color codes ({@code &a}, {@code §a}, etc.)
     * or MiniMessage tags, depending on the server's chat library. It may differ
     * from the value returned by {@link #getName()}.</p>
     *
     * @return the clan's non-{@code null} display name, potentially formatted
     */
    String getDisplayName();

    /**
     * Returns the clan's description (motto or short lore text).
     *
     * <p>This field is set by the clan leader and is purely cosmetic. It may be
     * empty but is never {@code null}.</p>
     *
     * @return the clan's description; never {@code null}, may be empty
     */
    String getDescription();

    /**
     * Returns the UUID of the clan's current leader.
     *
     * <p>The leader always appears in the set returned by {@link #getMembers()}.
     * There is exactly one leader per clan at any given time.</p>
     *
     * @return the non-{@code null} UUID of the clan leader
     */
    UUID getLeader();

    /**
     * Returns an unmodifiable view of all clan members, including the leader.
     *
     * <p>The returned {@link Set} is immutable: any attempt to add or remove
     * elements will throw {@link UnsupportedOperationException}. Membership
     * changes must go through the manager API.</p>
     *
     * @return a non-{@code null}, unmodifiable set of every member's UUID
     */
    Set<UUID> getMembers();

    /**
     * Returns {@code true} if the given player is a member of this clan.
     *
     * <p>This check includes the leader. Equivalent to
     * {@code getMembers().contains(uuid)}, but may be faster depending on
     * the underlying implementation.</p>
     *
     * @param uuid the UUID to test; must not be {@code null}
     * @return {@code true} if the player is a member or the leader,
     *         {@code false} otherwise
     */
    boolean isMember(UUID uuid);

    /**
     * Returns {@code true} if the given player is the leader of this clan.
     *
     * <p>Equivalent to {@code getLeader().equals(uuid)}, provided as a
     * convenience for permission checks.</p>
     *
     * @param uuid the UUID to test; must not be {@code null}
     * @return {@code true} if the player is the clan leader, {@code false} otherwise
     */
    boolean isLeader(UUID uuid);

    /**
     * Returns the clan's current aggregate power.
     *
     * <p>This value is the real-time sum of all members' current power. It is
     * used to determine the maximum number of territorial chunks the clan may hold.
     * The value is always in the range {@code [0, getTotalMaxPower()]}.</p>
     *
     * @return the clan's current total power; always {@code >= 0}
     * @see #getTotalMaxPower()
     * @see IPlayerData#getPower()
     */
    double getTotalPower();

    /**
     * Returns the clan's theoretical maximum aggregate power.
     *
     * <p>This cap is the sum of all members' individual maximum power values. It
     * grows as new members join (each contributing their own power cap) and shrinks
     * when members leave. The clan's actual power can never exceed this value.</p>
     *
     * @return the clan's total maximum power; always {@code >= 0}
     * @see #getTotalPower()
     * @see IPlayerData#getMaxPower()
     */
    double getTotalMaxPower();

    /**
     * Returns the number of Minecraft chunks currently claimed by this clan.
     *
     * <p>Each claimed chunk contributes one unit toward the clan's power
     * consumption. If the current total power drops below this count, the
     * server may automatically release the weakest claims.</p>
     *
     * @return the count of claimed chunks; always {@code >= 0}
     * @see #getTotalPower()
     */
    int getClaimsCount();

    /**
     * Returns the clan's designated home location, or {@code null} if none is set.
     *
     * <p>The home is typically set by the clan leader inside claimed territory and
     * serves as a teleportation anchor for all members. The returned {@link Location}
     * object is a copy; mutating it has no effect on the stored value.</p>
     *
     * @return the clan home {@link Location}, or {@code null} if no home has been set
     */
    Location getHome();
}