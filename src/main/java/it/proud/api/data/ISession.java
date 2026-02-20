package it.proud.api.data;

import java.util.Set;
import java.util.UUID;

public interface ISession {

    UUID getHost();

    String getWorld();

    Set<UUID> getPlayers();

    Set<UUID> getSpectators();

    String getState();

    boolean hasPlayer(UUID uuid);

    boolean hasSpectator(UUID uuid);

    boolean canJoin();

    int getAliveCount();

    boolean areAllDead();
}