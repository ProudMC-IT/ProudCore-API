package it.proud.api.managers;

import java.util.Map;
import java.util.Set;

/**
 * Manager for custom special characters usable on the server.
 * <p>
 * This manager provides a system to store and retrieve special Unicode
 * characters or emojis through identifying names. Characters can be
 * used in chat, signs, items, and other text contexts.
 * </p>
 * <p>
 * <b>Usage example:</b>
 * </p>
 * <pre>{@code
 * ICharManager charManager = api.getCharManager();
 * 
 * // Retrieve a special character
 * String heart = charManager.getChar("heart");
 * player.sendMessage("I love you! " + heart);
 * 
 * // Check existence
 * if (charManager.exists("star")) {
 *     String star = charManager.getChar("star");
 * }
 * 
 * // Add new character
 * charManager.addChar("smile");  // returns the Unicode character
 * }</pre>
 *
 * @author ProudCore Team
 * @version 1.0
 * @since 1.0
 */
public interface ICharManager {
    /**
     * Gets the special character associated with the specified name.
     * <p>
     * Retrieves the Unicode representation of the character identified by the name.
     * </p>
     *
     * @param name the identifying name of the character
     * @return the Unicode character as a string, or {@code null} if it doesn't exist
     */
    String getChar(String name);
    
    /**
     * Checks if a character with the specified name exists.
     *
     * @param name the name of the character to check
     * @return {@code true} if the character exists, {@code false} otherwise
     */
    boolean exists(String name);
    
    /**
     * Gets all available character names.
     * <p>
     * Returns an immutable set containing all identifiers
     * of special characters registered in the system.
     * </p>
     *
     * @return a set of all available character names
     */
    Set<String> getAllNames();
    
    /**
     * Adds a new special character to the system.
     * <p>
     * Registers a new character with the specified name. The actual
     * Unicode character is generated or assigned automatically.
     * </p>
     *
     * @param name the identifying name for the new character
     * @return the assigned Unicode character, or {@code null} if the operation fails
     */
    String addChar(String name);
    
    /**
     * Removes a special character from the system.
     * <p>
     * Deletes the registration of the character identified by the specified name.
     * </p>
     *
     * @param name the name of the character to remove
     * @return {@code true} if the character was removed, {@code false} if it didn't exist
     */
    boolean removeChar(String name);
    
    /**
     * Gets the total number of registered characters.
     * <p>
     * Includes all characters, both in use and available.
     * </p>
     *
     * @return the total character count
     */
    int getCharsCount();
    
    /**
     * Gets the number of characters still available for registration.
     * <p>
     * Returns how many characters can still be added to the system
     * before reaching the maximum limit, if present.
     * </p>
     *
     * @return the number of available slots for new characters
     */
    int getAvailableCharsCount();
    
    /**
     * Gets detailed information about a specific character.
     * <p>
     * Returns a map containing metadata about the character, such as the Unicode
     * code, category, creation date, and other properties.
     * </p>
     *
     * @param name the character name
     * @return a map with character information, or {@code null} if it doesn't exist
     */
    Map<String, String> getCharInfo(String name);
}