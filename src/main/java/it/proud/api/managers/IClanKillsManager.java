package it.proud.api.managers;

import java.util.Map;
import java.util.UUID;

public interface IClanKillsManager {

    long getClanKills(String clanName);

    long getPlayerKills(UUID uuid);

    Map<String, Long> getAllClanKills();

    void refreshClan(String clanName);

    void refreshAll();
}