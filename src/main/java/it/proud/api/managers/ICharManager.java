package it.proud.api.managers;

import java.util.Map;
import java.util.Set;

/**
 * Manager for the server's custom Unicode character registry.
 *
 * <p>{@code ICharManager} maintains a named catalogue of special Unicode characters
 * — symbols, decorative glyphs, custom texture-pack icons — that plugins and players
 * can reference by a human-readable name rather than a raw Unicode code point. This
 * decouples code and configuration from fragile character literals and makes it easy
 * to add, remove, or remap characters without touching call sites.</p>
 *
 * <h2>How it works</h2>
 * <p>Each entry in the registry consists of:</p>
 * <ul>
 *   <li>A <em>name</em> — a stable, lowercase string identifier (e.g. {@code "heart"},
 *       {@code "star"}, {@code "coin"}).</li>
 *   <li>A <em>character</em> — the actual Unicode string (often a single code point
 *       or a short sequence) assigned to that name.</li>
 * </ul>
 *
 * <h2>Typical usage</h2>
 * <pre>{@code
 * ICharManager chars = ProudCoreAPI.get().getCharManager();
 *
 * // Safe lookup with existence check
 * if (chars.exists("coin")) {
 *     player.sendMessage("Balance: 500 " + chars.getChar("coin"));
 * }
 *
 * // Register a new icon for a custom texture pack
 * String icon = chars.addChar("clan_shield");
 * if (icon != null) {
 *     System.out.println("Registered clan_shield as: " + icon);
 * }
 *
 * // Remove a deprecated character
 * chars.removeChar("old_icon");
 * }</pre>
 *
 * @author  ProudCore Team
 * @version 1.0
 * @since   1.0
 */
public interface ICharManager {

    /**
     * Returns the Unicode string mapped to the given name, or {@code null} if
     * no such character is registered.
     *
     * <p>The returned string is the raw Unicode representation of the character
     * (e.g. {@code "\uE001"}). It can be embedded directly in any text context
     * that supports Unicode, such as chat messages, item display names, or
     * book contents.</p>
     *
     * @param name the registry identifier to look up; must not be {@code null}
     * @return the Unicode character string, or {@code null} if the name is unknown
     * @see #exists(String)
     */
    String getChar(String name);

    /**
     * Returns {@code true} if a character is registered under the given name.
     *
     * <p>Use this method as a guard before calling {@link #getChar(String)} when
     * the existence of the character cannot be guaranteed, such as when the name
     * originates from player input or external configuration.</p>
     *
     * @param name the registry identifier to test; must not be {@code null}
     * @return {@code true} if a character with that name exists,
     *         {@code false} otherwise
     */
    boolean exists(String name);

    /**
     * Returns an unmodifiable view of all registered character names.
     *
     * <p>The returned {@link Set} reflects the current state of the registry.
     * Attempting to modify it will throw {@link UnsupportedOperationException}.
     * The set may be empty but is never {@code null}.</p>
     *
     * @return a non-{@code null}, unmodifiable set of all registered names
     */
    Set<String> getAllNames();

    /**
     * Returns a map of metadata about the specified character, or {@code null}
     * if the name is not registered.
     *
     * <p>The map may include entries such as:</p>
     * <ul>
     *   <li>{@code "unicode"} — the raw Unicode code point(s)</li>
     *   <li>{@code "category"} — a grouping label (e.g. {@code "ui"}, {@code "emoji"})</li>
     *   <li>{@code "createdAt"} — ISO-8601 timestamp of when the entry was added</li>
     * </ul>
     * <p>The exact set of keys is implementation-defined. Callers should
     * treat any key as optional and handle its absence gracefully.</p>
     *
     * @param name the registry identifier of the character; must not be {@code null}
     * @return an unmodifiable map of metadata key-value pairs,
     *         or {@code null} if the character does not exist
     */
    Map<String, String> getCharInfo(String name);

    /**
     * Returns the total number of characters currently registered.
     *
     * <p>This count includes every entry in the registry, regardless of whether
     * the associated Unicode slot is actively used or reserved for future use.</p>
     *
     * @return the total registered character count; always {@code >= 0}
     */
    int getCharsCount();

    /**
     * Returns the number of character slots still available for new registrations.
     *
     * <p>The registry may have a hard upper bound (e.g. a fixed block of private-use
     * Unicode code points). This method exposes the remaining capacity. A return
     * value of {@code 0} means {@link #addChar(String)} will fail until existing
     * characters are removed.</p>
     *
     * @return the number of slots available for new characters; always {@code >= 0}
     */
    int getAvailableCharsCount();

    /**
     * Registers a new character under the given name and returns its assigned
     * Unicode string.
     *
     * <p>The implementation automatically selects the next available Unicode code
     * point (typically from a private-use area) and maps it to the provided name.
     * The operation fails — returning {@code null} — if:</p>
     * <ul>
     *   <li>A character with the same name is already registered.</li>
     *   <li>The registry has reached its maximum capacity
     *       ({@link #getAvailableCharsCount()} returns {@code 0}).</li>
     *   <li>An internal storage error occurs.</li>
     * </ul>
     *
     * @param name the unique identifier for the new character; must not be
     *             {@code null} or blank
     * @return the Unicode string assigned to the new character, or {@code null}
     *         if the operation failed
     * @see #removeChar(String)
     */
    String addChar(String name);

    /**
     * Removes the character registered under the given name from the registry.
     *
     * <p>After removal, the name is no longer resolvable via {@link #getChar(String)}
     * and the freed slot becomes available for a future {@link #addChar(String)}
     * call. Any code that cached the Unicode character string of the removed entry
     * will continue to render it visually (the font/texture does not change), but
     * future lookups by name will return {@code null}.</p>
     *
     * @param name the registry identifier of the character to remove;
     *             must not be {@code null}
     * @return {@code true} if the character existed and was removed,
     *         {@code false} if no character with that name was found
     * @see #addChar(String)
     */
    boolean removeChar(String name);
}