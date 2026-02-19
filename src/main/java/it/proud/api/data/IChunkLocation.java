package it.proud.api.data;

/**
 * Immutable identifier for a Minecraft chunk within a specific world.
 *
 * <p>{@code IChunkLocation} uniquely addresses a chunk using three components:
 * the name of the world it resides in, its X coordinate, and its Z coordinate
 * — all expressed in <em>chunk units</em>, not block units. Two
 * {@code IChunkLocation} instances with identical world, X, and Z values refer
 * to the same chunk.</p>
 *
 * <h2>Coordinate system</h2>
 * <p>Minecraft divides each world into 16×16-block columns called chunks.
 * The chunk at chunk-coordinates {@code (cx, cz)} covers blocks
 * {@code (cx * 16)} through {@code (cx * 16 + 15)} on the X axis, and
 * {@code (cz * 16)} through {@code (cz * 16 + 15)} on the Z axis.
 * To convert a block coordinate to a chunk coordinate, perform an
 * arithmetic right-shift by 4 bits (i.e. {@code blockX >> 4}).</p>
 *
 * <h2>Usage example</h2>
 * <pre>{@code
 * IChunkLocation loc = ...;
 *
 * // Convert to block coordinates for the north-west corner of the chunk
 * int blockX = loc.x() * 16;
 * int blockZ = loc.z() * 16;
 *
 * System.out.printf("Chunk (%d, %d) in world '%s' starts at block (%d, %d)%n",
 *         loc.x(), loc.z(), loc.world(), blockX, blockZ);
 * }</pre>
 *
 * @author  ProudCore Team
 * @version 1.0
 * @since   1.0
 * @see     it.proud.api.managers.IClanManager#getClaimOwner(org.bukkit.Chunk)
 */
public interface IChunkLocation {

    /**
     * Returns the name of the world that contains this chunk.
     *
     * <p>The world name matches the folder name of the world on disk and
     * corresponds to the value returned by {@code org.bukkit.World#getName()}.
     * It is case-sensitive.</p>
     *
     * @return the non-{@code null}, non-blank world name
     */
    String world();

    /**
     * Returns the X coordinate of this chunk in chunk units.
     *
     * <p>To obtain the X coordinate of the north-west block corner, multiply
     * by 16: {@code int blockX = x() * 16;}.</p>
     *
     * @return the chunk X coordinate
     */
    int x();

    /**
     * Returns the Z coordinate of this chunk in chunk units.
     *
     * <p>To obtain the Z coordinate of the north-west block corner, multiply
     * by 16: {@code int blockZ = z() * 16;}.</p>
     *
     * @return the chunk Z coordinate
     */
    int z();
}