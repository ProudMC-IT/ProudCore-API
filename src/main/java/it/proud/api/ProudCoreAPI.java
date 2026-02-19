package it.proud.api;

import it.proud.api.managers.ICharManager;
import it.proud.api.managers.IClanManager;
import it.proud.api.managers.IPlayerManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main ProudCore API for centralized access to system managers.
 * <p>
 * This class provides a thread-safe singleton access point for all services
 * offered by ProudCore, including clan management, characters, and player data.
 * </p>
 * <p>
 * <b>Usage:</b><br>
 * To access the API from your external plugins, use {@link #get()} after ProudCore
 * has been loaded:
 * </p>
 * <pre>{@code
 * ProudCoreAPI api = ProudCoreAPI.get();
 * IClanManager clanManager = api.getClanManager();
 * }</pre>
 *
 * @author ProudCore Team
 * @version 1.0
 * @since 1.0
 */
public final class ProudCoreAPI {

    private static final Logger log = LogManager.getLogger("ProudCore/API");

    private static final String RESET  = "\u001B[0m";
    private static final String BOLD   = "\u001B[1m";
    private static final String GREEN  = "\u001B[32m";
    private static final String RED    = "\u001B[31m";
    private static final String CYAN   = "\u001B[36m";
    private static final String GRAY   = "\u001B[90m";

    private static final String PREFIX = BOLD + CYAN + "[API]" + RESET + " ";

    private static volatile ProudCoreAPI instance;

    private final IClanManager   clanManager;
    private final ICharManager   charManager;
    private final IPlayerManager playerManager;

    /**
     * Constructs a new ProudCoreAPI instance with the specified managers.
     * <p>
     * This constructor is internal and used by ProudCore's core to initialize
     * the API. External plugins should not instantiate this class directly,
     * but use {@link #get()} to access the singleton instance.
     * </p>
     *
     * @param clanManager   the clan and territorial claims manager
     * @param charManager   the custom characters manager
     * @param playerManager the player data manager
     */
    public ProudCoreAPI(IClanManager clanManager,
                        ICharManager charManager,
                        IPlayerManager playerManager) {
        this.clanManager   = clanManager;
        this.charManager   = charManager;
        this.playerManager = playerManager;
    }

    /**
     * Registers the API instance making it globally available.
     * <p>
     * This method is called internally by ProudCore on startup and sets up
     * the singleton instance accessible via {@link #get()}. It also logs
     * all managers for diagnostic purposes.
     * </p>
     * <p>
     * <b>Note:</b> This method is thread-safe and should only be called
     * by ProudCore's core during initialization.
     * </p>
     *
     * @param api the API instance to register as singleton
     */
    public static void register(ProudCoreAPI api) {
        instance = api;
        log.info("{}{}{}{}", PREFIX, GREEN, "✔ API registered successfully.", RESET);
        log.info("{}ClanManager   → {}", PREFIX, api.clanManager.getClass().getSimpleName());
        log.info("{}CharManager   → {}", PREFIX, api.charManager.getClass().getSimpleName());
        log.info("{}PlayerManager → {}", PREFIX, api.playerManager.getClass().getSimpleName());
        log.info("{}{}{}{}", PREFIX, GREEN, "Ready — external plugins can now call ProudCoreAPI.get()", RESET);
    }

    /**
     * Unregisters the API and releases the singleton instance.
     * <p>
     * Called during ProudCore shutdown to clean up resources.
     * After this call, {@link #get()} will throw an exception until
     * a new instance is registered.
     * </p>
     */
    public static void unregister() {
        if (instance != null) {
            log.info("{}{}API unregistered — ProudCore is shutting down.{}", PREFIX, GRAY, RESET);
            instance = null;
        }
    }

    /**
     * Gets the singleton API instance.
     * <p>
     * This is the main method for accessing the API from external plugins.
     * Throws an exception if ProudCore has not been loaded yet or has been
     * disabled.
     * </p>
     *
     * @return the current ProudCoreAPI instance
     * @throws IllegalStateException if the API has not been registered yet
     * @see #getOrNull() for an alternative that returns null instead of throwing exceptions
     */
    public static ProudCoreAPI get() {
        if (instance == null) {
            log.error("{}{}{}{}", PREFIX, RED, "get() called but API is not registered! Is ProudCore loaded?", RESET);
            throw new IllegalStateException(
                    "ProudCoreAPI non disponibile — ProudCore è caricato?");
        }
        return instance;
    }

    /**
     * Gets the singleton API instance without throwing exceptions.
     * <p>
     * Safe variant of {@link #get()} that returns {@code null} if the API
     * is not available instead of throwing an exception. Useful for conditional
     * checks on API existence.
     * </p>
     *
     * @return the current ProudCoreAPI instance, or {@code null} if not registered
     */
    public static ProudCoreAPI getOrNull() { return instance; }

    /**
     * Gets the clan manager.
     * <p>
     * Provides full access to clan management, including operations for
     * creation, disbanding, member management, and territorial claims.
     * </p>
     *
     * @return the clan manager instance
     */
    public IClanManager   getClanManager()   { return clanManager;   }
    
    /**
     * Gets the custom characters manager.
     * <p>
     * Provides access to the custom characters system, allowing
     * management and retrieval of special characters usable on the server.
     * </p>
     *
     * @return the character manager instance
     */
    public ICharManager   getCharManager()   { return charManager;   }
    
    /**
     * Gets the player data manager.
     * <p>
     * Provides access to player-specific data, including statistics,
     * power levels, and other persistent information.
     * </p>
     *
     * @return the player manager instance
     */
    public IPlayerManager getPlayerManager() { return playerManager; }
}