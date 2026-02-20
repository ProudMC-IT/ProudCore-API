package it.proud.api.managers;

import it.proud.api.data.ISession;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public interface IEventsManager {

    boolean hasEvent(Player player);

    boolean isInEvent(Player player);

    void start(Player host);

    void join(Player player, Player host);

    void forceStart(Player host);

    void stop(Player host);

    ISession getSession(UUID host);

    ISession getSessionByPlayer(UUID playerUuid);

    Map<UUID, ? extends ISession> getAllSessions();

    int getSingleTokens(UUID playerUuid);

    int getTeamTokens(UUID playerUuid);

    void setSingleTokens(UUID playerUuid, int amount);

    void setTeamTokens(UUID playerUuid, int amount);

    void addSingleTokens(UUID playerUuid, int amount);

    void addTeamTokens(UUID playerUuid, int amount);

    void removeSingleTokens(UUID playerUuid, int amount);

    void removeTeamTokens(UUID playerUuid, int amount);
}