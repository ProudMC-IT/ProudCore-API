package it.proud.api.data;

import java.util.UUID;

public interface IPlayerData {
    UUID getUuid();
    String getName();
    double getPower();
    double getMaxPower();
    int getChunkLimit();
    void setPower(double power);
    void setMaxPower(double maxPower);
    void setChunkLimit(int chunkLimit);
    void addPower(double amount);
    void takePower(double amount);
}