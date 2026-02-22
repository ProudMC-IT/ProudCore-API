package it.proud.api;

import it.proud.api.managers.*;
import it.proud.api.module.IModuleRegistry;
import it.proud.api.module.IProudModule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

/**
 * Central access point for the ProudCore plugin API.
 *
 * <p>This class exposes a thread-safe singleton that grants external plugins
 * unified access to all ProudCore subsystems: clan management, custom characters,
 * player data, scoreboards, and the external module registry.</p>
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
 * IModuleRegistry    reg     = api.getModuleRegistry();
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
 * <h2>Registering an external module</h2>
 * <pre>{@code
 * @EventHandler
 * public void onCoreReady(ProudCoreReadyEvent event) {
 *     event.getRegistry().register(new MyModule());
 * }
 * }</pre>
 *
 * @author  ProudCore Team
 * @version 1.0
 * @since   1.0
 */
@Getter
@RequiredArgsConstructor
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

    private final IClanManager       clanManager;
    private final ICharManager       charManager;
    private final IPlayerManager     playerManager;
    private final IScoreboardManager scoreboardManager;
    private final ISchematicsManager schematicsManager;
    private final IEventsManager     eventsManager;
    private final IClanKillsManager  clanKillsManager;
    private final IModuleRegistry    moduleRegistry;

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
        log.info("{}SchematicsManager → {}", PREFIX,
                api.schematicsManager != null ? api.schematicsManager.getClass().getSimpleName() : "disabled");
        log.info("{}EventsManager     → {}", PREFIX,
                api.eventsManager != null ? api.eventsManager.getClass().getSimpleName() : "disabled");
        log.info("{}ClanKillsManager  → {}", PREFIX,
                api.clanKillsManager != null ? api.clanKillsManager.getClass().getSimpleName() : "disabled");
        log.info("{}ModuleRegistry    → {}", PREFIX, api.moduleRegistry.getClass().getSimpleName());
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

    public static ProudCoreAPI getOrNull() {
        return instance;
    }

    /**
     * Typed shortcut for retrieving a registered external module.
     *
     * <pre>{@code
     * ProudCoreAPI.get()
     *     .getModule("myplugin:arena", IArenaModule.class)
     *     .ifPresent(m -> m.startArena("arena1"));
     * }</pre>
     *
     * @param moduleId the module's unique id
     * @param type     the expected module type
     * @param <T>      the module type
     * @return an {@link Optional} containing the module, or empty
     */
    public <T extends IProudModule> Optional<T> getModule(String moduleId, Class<T> type) {
        return moduleRegistry.getModule(moduleId, type);
    }
}