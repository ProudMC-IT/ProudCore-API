package it.proud.api.data;

/**
 * Descriptor for a registered currency.
 *
 * <p>Currencies are defined in {@code economy/config.yml} and loaded at startup.
 * The {@link it.proud.api.managers.IEconomyManager} always operates against a
 * specific currency identified by {@link #id()}.</p>
 *
 * @author ProudCore Team
 * @version 1.0
 * @since 1.0
 */
public interface ICurrency {

    /**
     * Returns the unique, lowercase identifier for this currency (e.g. {@code "coins"}).
     *
     * @return non-{@code null}, non-blank id
     */
    String id();

    /**
     * Returns the singular display name (e.g. {@code "Coin"}).
     *
     * @return non-{@code null} singular name
     */
    String nameSingular();

    /**
     * Returns the plural display name (e.g. {@code "Coins"}).
     *
     * @return non-{@code null} plural name
     */
    String namePlural();

    /**
     * Returns the symbol used for short display (e.g. {@code "⛃"}).
     *
     * @return non-{@code null} symbol
     */
    String symbol();

    /**
     * Returns the starting balance given to a player on first join.
     *
     * @return starting balance; always {@code >= 0}
     */
    double startingBalance();

    /**
     * Returns the maximum balance a player can hold, or {@code -1} for unlimited.
     *
     * @return max balance or {@code -1}
     */
    double maxBalance();

    /**
     * Returns the number of decimal places shown when formatting this currency.
     *
     * @return decimal places; typically {@code 0} or {@code 2}
     */
    int decimalPlaces();

    /**
     * Returns {@code true} if this is the primary currency bridged to Vault.
     *
     * @return {@code true} for the Vault-bridged currency
     */
    boolean vaultPrimary();

    /**
     * Formats the given amount according to this currency's display settings.
     *
     * @param amount the amount to format
     * @return formatted string, e.g. {@code "⛃ 1,250"}
     */
    String format(double amount);
}