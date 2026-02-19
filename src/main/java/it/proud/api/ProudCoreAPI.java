package it.proud.api;

import it.proud.api.managers.IClanManager;
import it.proud.api.managers.ICharManager;
import it.proud.api.managers.IPlayerManager;

public final class ProudCoreAPI {

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

    public static void register(ProudCoreAPI api)  { instance = api; }
    public static void unregister()                { instance = null; }

    public static ProudCoreAPI get() {
        if (instance == null)
            throw new IllegalStateException(
                    "ProudCoreAPI non disponibile — ProudCore è caricato?");
        return instance;
    }

    public static ProudCoreAPI getOrNull() { return instance; }

    public IClanManager   getClanManager()   { return clanManager;   }
    public ICharManager   getCharManager()   { return charManager;   }
    public IPlayerManager getPlayerManager() { return playerManager; }
}