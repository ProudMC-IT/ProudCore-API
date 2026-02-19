package it.proud.api;

import it.proud.api.managers.ICharManager;
import it.proud.api.managers.IClanManager;
import it.proud.api.managers.IPlayerManager;
import it.proud.api.managers.IScoreboardManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Central access point for the ProudCore plugin API.
 *
 * <p>This class exposes a thread-safe singleton that grants external plugins
 * unified access to all ProudCore subsystems: clan management, custom characters,
 * player data, and scoreboards. The instance is created and registered by ProudCore
 * itself during startup; external code must never instantiate this class directly.</p>
 *
 * <h2>Lifecycle</h2>
 * <ol>
 *   <li>ProudCore constructs an instance via the constructor.</li>
 *   <li>ProudCore publishes it globally via {@link #register(ProudCoreAPI)}.</li>
 *   <li>External plugins retrieve it at any time with {@link #get()}.</li>
 *   <li>On server shutdown, ProudCore calls {@link #unregister()} to release the instance.</li>
 * </ol>
 *
 * <h2>Typical usage</h2>
 * <pre>{@code
 * ProudCoreAPI api = ProudCoreAPI.get();
 *
 * IClanManager       clans   = api.getClanManager();
 * ICharManager       chars   = api.getCharManager();
 * IPlayerManager     players = api.getPlayerManager();
 * IScoreboardManager sb      = api.getScoreboardManager();
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

    /** Volatile singleton — written once on {@link #register} and nulled on {@link #unregister}. */
    private static volatile ProudCoreAPI instance;

    private final IClanManager       clanManager;
    private final ICharManager       charManager;
    private final IPlayerManager     playerManager;
    private final IScoreboardManager scoreboardManager;

    /**
     * Constructs a new {@code ProudCoreAPI}.
     *
     * <p><b>Internal use only.</b> Called exclusively by ProudCore's bootstrap logic.</p>
     *
     * @param clanManager       must not be {@code null}
     * @param charManager       must not be {@code null}
     * @param playerManager     must not be {@code null}
     * @param scoreboardManager the scoreboard manager, or {@code null} if the
     *                          scoreboard module is disabled
     */
    public ProudCoreAPI(IClanManager       clanManager,
                        ICharManager       charManager,
                        IPlayerManager     playerManager,
                        IScoreboardManager scoreboardManager) {
        this.clanManager       = clanManager;
        this.charManager       = charManager;
        this.playerManager     = playerManager;
        this.scoreboardManager = scoreboardManager;
    }

    /**
     * Publishes the given {@code ProudCoreAPI} instance as the global singleton.
     *
     * @param api the fully-initialised instance to publish; must not be {@code null}
     */
    public static void register(ProudCoreAPI api) {
        instance = api;
        log.info("{}{}{}{}", PREFIX, GREEN, "✔ API registered successfully.", RESET);
        log.info("{}ClanManager       → {}", PREFIX, api.clanManager.getClass().getSimpleName());
        log.info("{}CharManager       → {}", PREFIX, api.charManager.getClass().getSimpleName());
        log.info("{}PlayerManager     → {}", PREFIX, api.playerManager.getClass().getSimpleName());
        log.info("{}ScoreboardManager → {}", PREFIX,
                api.scoreboardManager != null ? api.scoreboardManager.getClass().getSimpleName() : "disabled");
        log.info("{}{}{}{}", PREFIX, GREEN, "Ready — external plugins can now call ProudCoreAPI.get()", RESET);
    }

    /**
     * Retracts the global singleton and signals that ProudCore is shutting down.
     * This method is a no-op if the API was never registered.
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
     * @return the current, non-{@code null} instance
     * @throws IllegalStateException if the API has not been registered or has been unregistered
     */
    public static ProudCoreAPI get() {
        if (instance == null) {
            log.error("{}{}{}{}", PREFIX, RED, "get() called but API is not registered! Is ProudCore loaded?", RESET);
            throw new IllegalStateException("ProudCoreAPI non disponibile — ProudCore è caricato?");
        }
        return instance;
    }

    /** Returns the instance, or {@code null} if ProudCore is not loaded. */
    public static ProudCoreAPI getOrNull() { return instance; }


    /** Returns the {@link IClanManager}. */
    public IClanManager getClanManager()             { return clanManager;       }

    /** Returns the {@link ICharManager}. */
    public ICharManager getCharManager()             { return charManager;       }

    /** Returns the {@link IPlayerManager}. */
    public IPlayerManager getPlayerManager()         { return playerManager;     }

    /**
     * Returns the {@link IScoreboardManager}, or {@code null} if the
     * scoreboard module is disabled in {@code config.yml}.
     */
    public IScoreboardManager getScoreboardManager() { return scoreboardManager; }
}