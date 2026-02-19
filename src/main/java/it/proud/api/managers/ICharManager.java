package it.proud.api.managers;

import java.util.Map;
import java.util.Set;

public interface ICharManager {
    String getChar(String name);
    boolean exists(String name);
    Set<String> getAllNames();
    String addChar(String name);
    boolean removeChar(String name);
    int getCharsCount();
    int getAvailableCharsCount();
    Map<String, String> getCharInfo(String name);
}