package it.proud.api.data;

import org.bukkit.Location;
import java.util.Set;
import java.util.UUID;

public interface IClan {
    String getName();
    String getDisplayName();
    String getDescription();
    UUID getLeader();
    Set<UUID> getMembers();
    boolean isMember(UUID uuid);
    boolean isLeader(UUID uuid);
    double getTotalPower();
    double getTotalMaxPower();
    int getClaimsCount();
    Location getHome();
}