package it.proud.api.data;

/**
 * Represents a chunk location in the Minecraft world.
 * <p>
 * This interface provides the essential coordinates to uniquely identify
 * a chunk within a specific world. Coordinates are expressed in chunk
 * units (not blocks).
 * </p>
 * <p>
 * <b>Usage example:</b>
 * </p>
 * <pre>{@code
 * IChunkLocation location = ...;
 * String worldName = location.world();
 * int chunkX = location.x();
 * int chunkZ = location.z();
 * }</pre>
 *
 * @author ProudCore Team
 * @version 1.0
 * @since 1.0
 */
public interface IChunkLocation {
    /**
     * Gets the name of the world where the chunk is located.
     *
     * @return the world name
     */
    String world();
    
    /**
     * Gets the X coordinate of the chunk.
     * <p>
     * This is the chunk coordinate, not the block coordinate. To convert to
     * block coordinates, multiply by 16.
     * </p>
     *
     * @return the chunk X coordinate
     */
    int x();
    
    /**
     * Gets the Z coordinate of the chunk.
     * <p>
     * This is the chunk coordinate, not the block coordinate. To convert to
     * block coordinates, multiply by 16.
     * </p>
     *
     * @return the chunk Z coordinate
     */
    int z();
}