package it.proud.api;

import it.proud.api.managers.ICharManager;
import it.proud.api.managers.IClanManager;
import it.proud.api.managers.IPlayerManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    public ProudCoreAPI(IClanManager clanManager,
                        ICharManager charManager,
                        IPlayerManager playerManager) {
        this.clanManager   = clanManager;
        this.charManager   = charManager;
        this.playerManager = playerManager;
    }

    public static void register(ProudCoreAPI api) {
        instance = api;
        log.info("{}{}{}{}", PREFIX, GREEN, "✔ API registered successfully.", RESET);
        log.info("{}ClanManager   → {}", PREFIX, api.clanManager.getClass().getSimpleName());
        log.info("{}CharManager   → {}", PREFIX, api.charManager.getClass().getSimpleName());
        log.info("{}PlayerManager → {}", PREFIX, api.playerManager.getClass().getSimpleName());
        log.info("{}{}{}{}", PREFIX, GREEN, "Ready — external plugins can now call ProudCoreAPI.get()", RESET);
    }

    public static void unregister() {
        if (instance != null) {
            log.info("{}{}API unregistered — ProudCore is shutting down.{}", PREFIX, GRAY, RESET);
            instance = null;
        }
    }

    public static ProudCoreAPI get() {
        if (instance == null) {
            log.error("{}{}{}{}", PREFIX, RED, "get() called but API is not registered! Is ProudCore loaded?", RESET);
            throw new IllegalStateException(
                    "ProudCoreAPI non disponibile — ProudCore è caricato?");
        }
        return instance;
    }

    public static ProudCoreAPI getOrNull() { return instance; }

    public IClanManager   getClanManager()   { return clanManager;   }
    public ICharManager   getCharManager()   { return charManager;   }
    public IPlayerManager getPlayerManager() { return playerManager; }
}