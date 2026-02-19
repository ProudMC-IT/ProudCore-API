package it.proud.api.managers;

import it.proud.api.data.IClan;
import it.proud.api.data.IPlayerData;
import org.bukkit.Chunk;

import java.util.Map;
import java.util.UUID;

/**
 * Authoritative manager for clans and territorial chunk claims.
 *
 * <p>{@code IClanManager} is the primary gateway for every clan-related operation
 * exposed by ProudCore. It covers the full lifecycle of a clan — from creation to
 * disbanding — as well as membership queries, territorial claim lookups, and
 * integrated access to per-player data.</p>
 *
 * <h2>Clan system overview</h2>
 * <ul>
 *   <li><b>Clans</b> are identified by a unique lowercase name and consist of one
 *       leader and zero or more regular members.</li>
 *   <li><b>Power</b> is the resource that governs how many chunks a clan may claim.
 *       It is the sum of all members' individual power values and decreases when
 *       members die.</li>
 *   <li><b>Claims</b> are Minecraft chunks "owned" by a clan. The number of
 *       claimable chunks is bounded by the clan's total power.</li>
 * </ul>
 *
 * <h2>Typical usage</h2>
 * <pre>{@code
 * IClanManager clans = ProudCoreAPI.get().getClanManager();
 *
 * // Check membership and greet the player
 * if (clans.isInClan(playerUuid)) {
 *     IClan clan = clans.getPlayerClan(playerUuid);
 *     player.sendMessage("Welcome back, member of " + clan.getDisplayName() + "!");
 * }
 *
 * // Protect a chunk — deny building if the player does not own the claim
 * IClan owner = clans.getClaimOwner(event.getBlock().getChunk());
 * if (owner != null && !owner.isMember(playerUuid)) {
 *     event.setCancelled(true);
 * }
 * }</pre>
 *
 * @author  ProudCore Team
 * @version 1.0
 * @since   1.0
 * @see     IClan
 * @see     IPlayerData
 */
public interface IClanManager {

    /**
     * Returns the clan with the given name, or {@code null} if none exists.
     *
     * <p>The lookup is case-sensitive and matches the internal (non-display) name
     * exactly as it was provided during {@link #createClan(String, UUID)}.</p>
     *
     * @param name the unique identifier of the clan; must not be {@code null}
     * @return the matching {@link IClan}, or {@code null} if no clan has that name
     */
    IClan getClan(String name);

    /**
     * Returns the clan that a given player belongs to, or {@code null} if the
     * player is clanless.
     *
     * <p>A player can belong to at most one clan at any time. This method returns
     * the same object regardless of whether the player is the leader or a regular
     * member.</p>
     *
     * @param uuid the UUID of the player to look up; must not be {@code null}
     * @return the player's {@link IClan}, or {@code null} if the player has no clan
     * @see #isInClan(UUID)
     */
    IClan getPlayerClan(UUID uuid);

    /**
     * Returns {@code true} if the given player is a member of any clan.
     *
     * <p>This is a lightweight convenience method. When you only need a boolean
     * answer and do not need the clan object itself, prefer this method over
     * {@code getPlayerClan(uuid) != null} for clarity.</p>
     *
     * @param uuid the UUID of the player to check; must not be {@code null}
     * @return {@code true} if the player belongs to a clan, {@code false} otherwise
     */
    boolean isInClan(UUID uuid);

    /**
     * Returns an immutable snapshot of all clans currently registered on the server,
     * keyed by their internal name.
     *
     * <p>The returned map is a read-only view. Attempting to mutate it will throw
     * {@link UnsupportedOperationException}. The map may be empty but is never
     * {@code null}.</p>
     *
     * <p>This method is suitable for server-wide statistics, admin tooling, and
     * leaderboard calculations. For high-frequency per-player checks, prefer the
     * targeted lookup methods to avoid unnecessary iteration.</p>
     *
     * @return an unmodifiable {@code Map<name, IClan>} of all existing clans
     */
    Map<String, ? extends IClan> getAllClans();

    /**
     * Creates a new clan and assigns the specified player as its first leader.
     *
     * <p>The operation will fail — returning {@code false} — in any of the
     * following situations:</p>
     * <ul>
     *   <li>A clan with the same name already exists.</li>
     *   <li>The designated leader is already a member of another clan.</li>
     *   <li>The provided name does not meet server-configured validation rules
     *       (e.g. length or character restrictions).</li>
     * </ul>
     *
     * <p>On success, the leader is automatically enrolled as the sole initial
     * member and the clan becomes immediately visible via {@link #getClan(String)}.</p>
     *
     * @param name   the unique, human-readable identifier for the new clan;
     *               must not be {@code null} or blank
     * @param leader the UUID of the player who will own and lead the clan;
     *               must not be {@code null}
     * @return {@code true} if the clan was created successfully,
     *         {@code false} if any precondition was not met
     */
    boolean createClan(String name, UUID leader);

    /**
     * Permanently disbands the clan with the given name.
     *
     * <p>Disbanding a clan:</p>
     * <ul>
     *   <li>Removes all members (including the leader) from the clan.</li>
     *   <li>Releases all territorial claims held by the clan.</li>
     *   <li>Deletes all persistent clan data from storage.</li>
     * </ul>
     *
     * <p><b>This action is irreversible.</b> There is no undo mechanism; a
     * disbanded clan's data cannot be recovered.</p>
     *
     * @param name the name of the clan to disband; must not be {@code null}
     * @return {@code true} if the clan existed and was disbanded,
     *         {@code false} if no clan with that name was found
     */
    boolean disbandClan(String name);

    /**
     * Returns the clan that owns the given chunk, or {@code null} if the chunk
     * is unclaimed.
     *
     * <p>This method is optimized for high-frequency calls (e.g. block-interaction
     * events) and typically resolves claims from an in-memory index without hitting
     * the database.</p>
     *
     * <pre>{@code
     * IClan owner = clans.getClaimOwner(event.getBlock().getChunk());
     * if (owner != null && !owner.isMember(attacker.getUniqueId())) {
     *     event.setCancelled(true);
     *     attacker.sendMessage("This territory belongs to " + owner.getDisplayName());
     * }
     * }</pre>
     *
     * @param chunk the Bukkit {@link Chunk} to check; must not be {@code null}
     * @return the {@link IClan} that has claimed this chunk, or {@code null} if
     *         the chunk belongs to no clan
     */
    IClan getClaimOwner(Chunk chunk);

    /**
     * Returns the clan-system player data for the specified player.
     *
     * <p>This is a convenience shortcut that internally delegates to
     * {@link IPlayerManager#getPlayer(UUID)}. It exposes power levels, chunk-claim
     * limits, and other per-player statistics that feed into clan calculations.</p>
     *
     * <p>This method never returns {@code null}: if no data record exists yet for
     * the player, a default one is created and cached automatically.</p>
     *
     * @param uuid the UUID of the player whose data is requested; must not be {@code null}
     * @return the non-{@code null} {@link IPlayerData} for that player
     * @see IPlayerManager#getPlayer(UUID)
     */
    IPlayerData getPlayerData(UUID uuid);

    /**
     * Forces an immediate flush of all in-memory clan and player data to persistent
     * storage (file or database).
     *
     * <p>Under normal operation, ProudCore saves data automatically at configurable
     * intervals. Call this method explicitly only when data consistency is critical
     * and cannot wait for the next scheduled save — for example, just before a
     * controlled server restart, after a bulk administrative operation, or inside
     * a critical-section plugin workflow.</p>
     *
     * <p><b>Note:</b> This operation may block the calling thread briefly while
     * I/O completes. Avoid calling it on the main server thread in
     * performance-sensitive code paths.</p>
     */
    void saveAll();
}