package it.proud.api.managers;

import it.proud.api.module.IProudModule;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Manager for complete player state snapshots.
 *
 * <p>{@code ISnapshotManager} captures and restores the full state of an online
 * player: inventory, armor, off-hand, ender chest, location, health, food,
 * experience, game mode, potion effects, flight state, and economy balances.
 * Snapshots are owned by the module that created them and persisted in the
 * database automatically.</p>
 *
 * <h2>Typical usage — minigame module</h2>
 * <pre>{@code
 * ISnapshotManager snapshots = ProudCoreAPI.get().getSnapshotManager();
 *
 * // Before entering the arena — save everything
 * PlayerSnapshot snap = snapshots.save(player, "pre_arena", myModule);
 *
 * // Clear and set up arena loadout
 * player.getInventory().clear();
 * player.setGameMode(GameMode.SURVIVAL);
 *
 * // ... arena plays ...
 *
 * // Restore everything in one call
 * snapshots.restore(player, snap);
 * }</pre>
 *
 * <h2>Partial restore</h2>
 * <pre>{@code
 * // Restore only inventory and location, keep current health
 * snapshots.restore(player, snap, SnapshotPart.INVENTORY, SnapshotPart.LOCATION);
 * }</pre>
 *
 * @author ProudCore Team
 * @version 1.0
 * @since 1.0
 */
public interface ISnapshotManager {

    /**
     * Parts of a snapshot that can be selectively restored.
     */
    enum SnapshotPart {
        /** Main inventory (36 slots) */
        INVENTORY,
        /** Armor slots (4) */
        ARMOR,
        /** Off-hand slot */
        OFFHAND,
        /** Ender chest (27 slots) */
        ENDER_CHEST,
        /** World, coordinates, yaw, pitch */
        LOCATION,
        /** Max health and current health */
        HEALTH,
        /** Food level and saturation */
        FOOD,
        /** XP level, progress, and total */
        EXPERIENCE,
        /** GameMode */
        GAME_MODE,
        /** Active potion effects */
        POTION_EFFECTS,
        /** Allow-flight and is-flying flags */
        FLIGHT,
        /** All economy balances from IEconomyManager */
        ECONOMY,
        /** Everything above */
        ALL
    }

    /**
     * Captures the complete state of an online player and persists it.
     *
     * @param player the online player to snapshot; must not be {@code null}
     * @param label  a human-readable label (e.g. {@code "pre_arena"}); must not be {@code null}
     * @param owner  the module creating this snapshot; must not be {@code null}
     * @return the created {@link PlayerSnapshot}
     */
    PlayerSnapshot save(Player player, String label, IProudModule owner);

    /**
     * Captures only the selected parts of the player's state.
     *
     * @param player the online player; must not be {@code null}
     * @param label  snapshot label; must not be {@code null}
     * @param owner  owning module; must not be {@code null}
     * @param parts  parts to capture; if empty defaults to {@link SnapshotPart#ALL}
     * @return the created snapshot
     */
    PlayerSnapshot save(Player player, String label, IProudModule owner, SnapshotPart... parts);

    /**
     * Restores all parts of a snapshot to the player.
     *
     * @param player   the target player (may differ from original — useful for cloning); must not be {@code null}
     * @param snapshot the snapshot to restore; must not be {@code null}
     */
    void restore(Player player, PlayerSnapshot snapshot);

    /**
     * Restores only the selected parts of a snapshot.
     *
     * @param player   the target player; must not be {@code null}
     * @param snapshot the snapshot to restore; must not be {@code null}
     * @param parts    the parts to restore
     */
    void restore(Player player, PlayerSnapshot snapshot, SnapshotPart... parts);

    /**
     * Returns the most recent snapshot for the given player created by the given module.
     *
     * @param uuid  player UUID; must not be {@code null}
     * @param owner the owning module; must not be {@code null}
     * @return an {@link Optional} containing the latest snapshot, or empty
     */
    Optional<PlayerSnapshot> getLatest(UUID uuid, IProudModule owner);

    /**
     * Returns all snapshots for the given player created by the given module,
     * most-recent first.
     *
     * @param uuid  player UUID; must not be {@code null}
     * @param owner the owning module; must not be {@code null}
     * @return ordered list; never {@code null}
     */
    List<PlayerSnapshot> getAll(UUID uuid, IProudModule owner);

    /**
     * Returns the snapshot with the given id, regardless of owner.
     *
     * @param snapshotId the snapshot id
     * @return an {@link Optional} containing the snapshot, or empty
     */
    Optional<PlayerSnapshot> getById(String snapshotId);

    /**
     * Deletes the snapshot with the given id from persistent storage.
     *
     * @param snapshotId the id of the snapshot to delete
     * @return {@code true} if deleted, {@code false} if not found
     */
    boolean delete(String snapshotId);

    /**
     * Deletes all snapshots owned by the given module for the given player.
     *
     * @param uuid  player UUID; must not be {@code null}
     * @param owner the owning module; must not be {@code null}
     * @return the number of deleted snapshots
     */
    int deleteAll(UUID uuid, IProudModule owner);

    /**
     * Immutable record of a complete (or partial) player state.
     */
    interface PlayerSnapshot {

        /** Unique snapshot id (UUID string). */
        String getId();

        /** UUID of the player this snapshot belongs to. */
        UUID getPlayerUuid();

        /** Last-known name of the player at capture time. */
        String getPlayerName();

        /** Human-readable label assigned by the owning module. */
        String getLabel();

        /** Id of the module that created this snapshot. */
        String getOwnerId();

        /** Timestamp when the snapshot was created. */
        Instant getCreatedAt();

        /** Returns the parts that were captured in this snapshot. */
        List<SnapshotPart> getCapturedParts();

        /** Returns {@code true} if the given part was captured. */
        boolean hasPart(SnapshotPart part);

        /**
         * Returns serialized item data for the given part (INVENTORY, ARMOR, OFFHAND, ENDER_CHEST).
         * Returns an empty map if the part was not captured.
         *
         * @param part the inventory part
         * @return slot → Base64-encoded ItemStack
         */
        Map<Integer, String> getItemData(SnapshotPart part);

        /** Returns the location data or {@code null} if LOCATION was not captured. */
        LocationData getLocation();

        /** Returns the health/food/xp/gamemode/flight data. May be {@code null} if not captured. */
        PlayerStateData getState();

        /** Returns economy balances at capture time. Empty map if ECONOMY was not captured. */
        Map<String, Double> getEconomyBalances();

        record LocationData(String world, double x, double y, double z, float yaw, float pitch) {}

        record PlayerStateData(
                double maxHealth,
                double health,
                int    foodLevel,
                float  saturation,
                int    xpLevel,
                float  xpProgress,
                int    totalExperience,
                String gameMode,
                boolean allowFlight,
                boolean flying,
                List<String> potionEffects
        ) {}
    }
}