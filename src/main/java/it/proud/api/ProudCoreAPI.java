package it.proud.api;

import it.proud.api.managers.ICharManager;
import it.proud.api.managers.IClanManager;
import it.proud.api.managers.IPlayerManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Central access point for the ProudCore plugin API.
 *
 * <p>This class exposes a thread-safe singleton that grants external plugins
 * unified access to all ProudCore subsystems: clan management, custom characters,
 * and player data. The instance is created and registered by ProudCore itself
 * during startup; external code must never instantiate this class directly.</p>
 *
 * <h2>Lifecycle</h2>
 * <ol>
 *   <li>ProudCore constructs an instance via
 *       {@link #ProudCoreAPI(IClanManager, ICharManager, IPlayerManager)}.</li>
 *   <li>ProudCore publishes it globally via {@link #register(ProudCoreAPI)}.</li>
 *   <li>External plugins retrieve it at any time with {@link #get()}.</li>
 *   <li>On server shutdown, ProudCore calls {@link #unregister()} to release
 *       the instance and free resources.</li>
 * </ol>
 *
 * <h2>Typical usage</h2>
 * <pre>{@code
 * // Retrieve the API (throws if ProudCore is not loaded)
 * ProudCoreAPI api = ProudCoreAPI.get();
 *
 * IClanManager  clans   = api.getClanManager();
 * ICharManager  chars   = api.getCharManager();
 * IPlayerManager players = api.getPlayerManager();
 * }</pre>
 *
 * <h2>Safe usage (optional dependency)</h2>
 * <pre>{@code
 * ProudCoreAPI api = ProudCoreAPI.getOrNull();
 * if (api != null) {
 *     // ProudCore is available — proceed safely
 * }
 * }</pre>
 *
 * @author  ProudCore Team
 * @version 1.0
 * @since   1.0
 * @see     IClanManager
 * @see     ICharManager
 * @see     IPlayerManager
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

    /** Volatile singleton; written once on {@link #register} and nulled on {@link #unregister}. */
    private static volatile ProudCoreAPI instance;

    private final IClanManager   clanManager;
    private final ICharManager   charManager;
    private final IPlayerManager playerManager;

    /**
     * Constructs a new {@code ProudCoreAPI} with the provided manager implementations.
     *
     * <p><b>Internal use only.</b> This constructor is called exclusively by ProudCore's
     * bootstrap logic. External plugins must use {@link #get()} to obtain the shared
     * instance rather than creating their own.</p>
     *
     * @param clanManager   the manager responsible for clans and territorial claims;
     *                      must not be {@code null}
     * @param charManager   the manager responsible for custom Unicode characters;
     *                      must not be {@code null}
     * @param playerManager the manager responsible for persistent player data;
     *                      must not be {@code null}
     * @throws NullPointerException if any argument is {@code null}
     */
    public ProudCoreAPI(IClanManager clanManager,
                        ICharManager charManager,
                        IPlayerManager playerManager) {
        this.clanManager   = clanManager;
        this.charManager   = charManager;
        this.playerManager = playerManager;
    }

    /**
     * Publishes the given {@code ProudCoreAPI} instance as the global singleton.
     *
     * <p>After this call returns, any code on the server can retrieve the instance
     * via {@link #get()} or {@link #getOrNull()}. This method also emits diagnostic
     * log lines that confirm the concrete implementation class of every manager,
     * which is useful when troubleshooting custom manager overrides.</p>
     *
     * <p><b>Thread safety:</b> The internal field is {@code volatile}; the write
     * performed here is immediately visible to all threads.</p>
     *
     * <p><b>Internal use only.</b> Must be called exactly once per ProudCore
     * startup cycle, before any external plugin attempts to access the API.</p>
     *
     * @param api the fully-initialized {@code ProudCoreAPI} instance to publish;
     *            must not be {@code null}
     * @throws NullPointerException if {@code api} is {@code null}
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
     * Retracts the global singleton and signals that ProudCore is shutting down.
     *
     * <p>After this call, {@link #get()} will throw {@link IllegalStateException}
     * and {@link #getOrNull()} will return {@code null}. Any plugin that caches
     * the API reference should discard it upon receiving a ProudCore disable event.</p>
     *
     * <p>If the API has already been unregistered (or was never registered), this
     * method is a no-op.</p>
     *
     * <p><b>Internal use only.</b> Called exclusively by ProudCore's shutdown hook.</p>
     */
    public static void unregister() {
        if (instance != null) {
            log.info("{}{}API unregistered — ProudCore is shutting down.{}", PREFIX, GRAY, RESET);
            instance = null;
        }
    }

    /**
     * Returns the global {@code ProudCoreAPI} instance.
     *
     * <p>This is the primary entry point for all external plugins that depend on
     * ProudCore. It is safe to call this method from any thread after ProudCore
     * has fired its ready event.</p>
     *
     * <p><b>Precondition:</b> {@link #register(ProudCoreAPI)} must have been called
     * before this method is invoked. If ProudCore is not loaded, or has been
     * disabled, this method will throw.</p>
     *
     * @return the current, non-{@code null} {@code ProudCoreAPI} instance
     * @throws IllegalStateException if the API has not been registered yet, or has
     *                               already been unregistered during shutdown
     * @see #getOrNull()
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
     * Returns the global {@code ProudCoreAPI} instance, or {@code null} if unavailable.
     *
     * <p>This is the null-safe counterpart of {@link #get()}. It is intended for
     * plugins that treat ProudCore as an optional soft-dependency and need to check
     * for its presence at runtime without catching exceptions.</p>
     *
     * <pre>{@code
     * ProudCoreAPI api = ProudCoreAPI.getOrNull();
     * if (api == null) {
     *     getLogger().warning("ProudCore not found — clan features disabled.");
     *     return;
     * }
     * }</pre>
     *
     * @return the current {@code ProudCoreAPI} instance, or {@code null} if ProudCore
     *         is not loaded or has been disabled
     * @see #get()
     */
    public static ProudCoreAPI getOrNull() { return instance; }

    /**
     * Returns the {@link IClanManager} for this server.
     *
     * <p>The clan manager is the authoritative source for all clan-related
     * operations, including:</p>
     * <ul>
     *   <li>Creating and disbanding clans</li>
     *   <li>Querying clan membership and leadership</li>
     *   <li>Managing territorial chunk claims</li>
     *   <li>Reading aggregated power values</li>
     * </ul>
     *
     * @return the non-{@code null} {@link IClanManager} instance
     */
    public IClanManager getClanManager() { return clanManager; }

    /**
     * Returns the {@link ICharManager} for this server.
     *
     * <p>The character manager maintains a registry of named Unicode characters
     * (e.g. symbols, emoji, decorative glyphs) that plugins and players can use
     * in chat messages, item names, signs, and other text contexts.</p>
     *
     * @return the non-{@code null} {@link ICharManager} instance
     */
    public ICharManager getCharManager() { return charManager; }

    /**
     * Returns the {@link IPlayerManager} for this server.
     *
     * <p>The player manager handles the full lifecycle of per-player persistent
     * data, covering power levels, chunk claim limits, and statistical information.
     * Data is loaded on join, cached in memory for zero-latency reads during
     * gameplay, and flushed to the database on leave or server shutdown.</p>
     *
     * @return the non-{@code null} {@link IPlayerManager} instance
     */
    public IPlayerManager getPlayerManager() { return playerManager; }
}