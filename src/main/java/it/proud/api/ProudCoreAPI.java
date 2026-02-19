package it.proud.api;

import it.proud.api.managers.IClanManager;
import it.proud.api.managers.ICharManager;
import it.proud.api.managers.IPlayerManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ProudCoreAPI {

    private static final Logger log = LogManager.getLogger("ProudCoreAPI");

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
        log.info("[ProudCoreAPI] ✔ API registered successfully.");
        log.info("[ProudCoreAPI] ClanManager   → {}", api.clanManager.getClass().getSimpleName());
        log.info("[ProudCoreAPI] CharManager   → {}", api.charManager.getClass().getSimpleName());
        log.info("[ProudCoreAPI] PlayerManager → {}", api.playerManager.getClass().getSimpleName());
        log.info("[ProudCoreAPI] Ready — external plugins can now call ProudCoreAPI.get()");
    }

    public static void unregister() {
        if (instance != null) {
            log.info("[ProudCoreAPI] API unregistered — ProudCore is shutting down.");
            instance = null;
        }
    }

    public static ProudCoreAPI get() {
        if (instance == null) {
            log.error("[ProudCoreAPI] get() called but API is not registered! Is ProudCore loaded?");
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