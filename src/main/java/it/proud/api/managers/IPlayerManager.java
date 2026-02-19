package it.proud.api.managers;

import it.proud.api.data.IPlayerData;
import java.util.UUID;

public interface IPlayerManager {
    IPlayerData getPlayer(UUID uuid);
    boolean isLoaded(UUID uuid);
    void saveAll();
}