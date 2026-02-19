package it.proud.api.managers;

import it.proud.api.data.IClan;
import it.proud.api.data.IPlayerData;
import org.bukkit.Chunk;

import java.util.Map;
import java.util.UUID;

public interface IClanManager {
    IClan getClan(String name);
    IClan getPlayerClan(UUID uuid);
    boolean isInClan(UUID uuid);
    boolean createClan(String name, UUID leader);
    boolean disbandClan(String name);
    Map<String, ? extends IClan> getAllClans();
    IPlayerData getPlayerData(UUID uuid);
    IClan getClaimOwner(Chunk chunk);
    void saveAll();
}