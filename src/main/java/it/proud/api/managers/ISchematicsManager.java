package it.proud.api.managers;

import org.bukkit.Location;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Manager for the server's schematic registry.
 *
 * <p>{@code ISchematicsManager} exposes a read-only view of the in-memory
 * registry of schematics that have been pasted into the world via the
 * {@code /schematic load} command. External plugins can query which schematics
 * are loaded, in which worlds, and which block locations they occupy.</p>
 *
 * <h2>Typical usage</h2>
 * <pre>{@code
 * ISchematicsManager schematics = ProudCoreAPI.get().getSchematicsManager();
 *
 * // Check if an arena schematic is already pasted
 * if (schematics.isLoaded("arena_1", "world")) {
 *     List<Location> blocks = schematics.getBlocks("arena_1", "world");
 *     // ... do something with the block list
 * }
 *
 * // List all loaded schematics
 * for (String name : schematics.getAllNames()) {
 *     System.out.println("Loaded: " + name);
 * }
 * }</pre>
 *
 * @author  ProudCore Team
 * @version 1.0
 * @since   1.0
 */
public interface ISchematicsManager {

    /**
     * Returns {@code true} if the schematic with the given name is currently
     * registered (i.e. has been pasted) in the specified world.
     *
     * @param name      schematic identifier (filename without {@code .schem})
     * @param worldName the target world name
     * @return {@code true} if loaded, {@code false} otherwise
     */
    boolean isLoaded(String name, String worldName);

    /**
     * Returns an unmodifiable list of all block {@link Location}s that were
     * placed when the schematic was pasted, or an empty list if it is not
     * registered.
     *
     * <p>This list can be used, for example, to iterate over arena boundaries
     * or to verify that a structure is still intact.</p>
     *
     * @param name      schematic identifier
     * @param worldName the target world name
     * @return unmodifiable list of block locations; never {@code null}
     */
    List<Location> getBlocks(String name, String worldName);

    /**
     * Returns the set of schematic names currently loaded in the given world.
     *
     * @param worldName the target world name
     * @return an unmodifiable set of schematic names; never {@code null}, may be empty
     */
    Set<String> getNamesInWorld(String worldName);

    /**
     * Returns all registered schematic names across all worlds.
     *
     * <p>If the same schematic name has been loaded in multiple worlds it will
     * appear only once in the returned set.</p>
     *
     * @return an unmodifiable set of schematic names; never {@code null}, may be empty
     */
    Set<String> getAllNames();

    /**
     * Returns a map of every registered schematic keyed by its internal
     * {@code "worldName:schematicName"} composite key, with a human-readable
     * display label as the value.
     *
     * <p>Useful for admin list commands or GUIs that need both the key for
     * look-up and a pretty label for display.</p>
     *
     * @return an unmodifiable map; never {@code null}, may be empty
     */
    Map<String, String> getAllWithWorld();

    /**
     * Returns the total number of schematics currently registered across all
     * worlds.
     *
     * @return count ≥ 0
     */
    int count();
}