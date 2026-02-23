package it.proud.api.managers;

import it.proud.api.data.ICurrency;
import it.proud.api.data.ITransaction;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Central manager for the ProudCore economy system.
 *
 * <p>Supports multiple currencies, clan banks, and full transaction history.
 * All write operations are atomic and automatically persisted.</p>
 *
 * <h2>Typical usage</h2>
 * <pre>{@code
 * IEconomyManager eco = ProudCoreAPI.get().getEconomyManager();
 *
 * // Deposit 100 coins to a player
 * eco.deposit(playerUuid, "coins", 100, "Kill reward");
 *
 * // Check balance
 * double bal = eco.getBalance(playerUuid, "coins");
 *
 * // Transfer between players
 * EconomyResult result = eco.transfer(senderUuid, receiverUuid, "coins", 50, "Gift");
 * if (!result.isSuccess()) player.sendMessage(result.getMessage());
 * }</pre>
 *
 * @author ProudCore Team
 * @version 1.0
 * @since 1.0
 */
public interface IEconomyManager {

    /**
     * Result of an economy operation.
     */
    interface EconomyResult {
        /** Returns {@code true} if the operation succeeded. */
        boolean isSuccess();
        /** Returns a human-readable outcome message. */
        String getMessage();
    }

    /**
     * Returns the currency registered under the given id, or {@code null}.
     *
     * @param currencyId the currency id; must not be {@code null}
     * @return the {@link ICurrency}, or {@code null}
     */
    ICurrency getCurrency(String currencyId);

    /**
     * Returns all registered currencies.
     *
     * @return unmodifiable collection; never {@code null}
     */
    Collection<ICurrency> getCurrencies();

    /**
     * Returns the primary currency that is bridged to Vault.
     *
     * @return non-{@code null} primary currency
     */
    ICurrency getPrimaryCurrency();

    /**
     * Returns the player's balance for the given currency.
     * If the player has no account yet, a default one is created automatically.
     *
     * @param uuid       player UUID
     * @param currencyId the currency id
     * @return balance; always {@code >= 0}
     */
    double getBalance(UUID uuid, String currencyId);

    /**
     * Returns all balances for the given player, keyed by currency id.
     *
     * @param uuid player UUID
     * @return unmodifiable map of {@code currencyId → balance}
     */
    Map<String, Double> getAllBalances(UUID uuid);

    /**
     * Returns {@code true} if the player has at least {@code amount} of the given currency.
     *
     * @param uuid       player UUID
     * @param currencyId the currency id
     * @param amount     the amount to check
     * @return {@code true} if balance >= amount
     */
    boolean has(UUID uuid, String currencyId, double amount);

    /**
     * Deposits {@code amount} into the player's account.
     *
     * @param uuid       player UUID
     * @param currencyId the currency id
     * @param amount     amount to deposit; must be {@code > 0}
     * @param reason     short reason string, or {@code null}
     * @return {@link EconomyResult} indicating success or failure
     */
    EconomyResult deposit(UUID uuid, String currencyId, double amount, String reason);

    /**
     * Withdraws {@code amount} from the player's account.
     * Fails if the player does not have sufficient funds.
     *
     * @param uuid       player UUID
     * @param currencyId the currency id
     * @param amount     amount to withdraw; must be {@code > 0}
     * @param reason     short reason string, or {@code null}
     * @return {@link EconomyResult} indicating success or failure
     */
    EconomyResult withdraw(UUID uuid, String currencyId, double amount, String reason);

    /**
     * Sets the player's balance to exactly {@code amount}, bypassing all limits.
     * Should only be called from admin commands.
     *
     * @param uuid       player UUID
     * @param currencyId the currency id
     * @param amount     new balance; must be {@code >= 0}
     * @param reason     short reason string, or {@code null}
     * @return {@link EconomyResult} indicating success or failure
     */
    EconomyResult set(UUID uuid, String currencyId, double amount, String reason);

    /**
     * Transfers {@code amount} from sender to receiver atomically.
     * Fails if the sender does not have sufficient funds.
     *
     * @param from       sender UUID
     * @param to         receiver UUID
     * @param currencyId the currency id
     * @param amount     amount to transfer; must be {@code > 0}
     * @param reason     short reason string, or {@code null}
     * @return {@link EconomyResult} indicating success or failure
     */
    EconomyResult transfer(UUID from, UUID to, String currencyId, double amount, String reason);

    /**
     * Returns the clan bank balance for the given currency.
     *
     * @param clanName   the clan's internal name
     * @param currencyId the currency id
     * @return balance; always {@code >= 0}
     */
    double getClanBalance(String clanName, String currencyId);

    /**
     * Deposits {@code amount} into the clan bank.
     *
     * @param clanName   the clan's internal name
     * @param currencyId the currency id
     * @param amount     amount to deposit; must be {@code > 0}
     * @param depositor  UUID of the player making the deposit
     * @return {@link EconomyResult}
     */
    EconomyResult clanDeposit(String clanName, String currencyId, double amount, UUID depositor);

    /**
     * Withdraws {@code amount} from the clan bank.
     * Only the clan leader should call this; the manager does not enforce leader checks.
     *
     * @param clanName   the clan's internal name
     * @param currencyId the currency id
     * @param amount     amount to withdraw; must be {@code > 0}
     * @param withdrawer UUID of the player making the withdrawal
     * @return {@link EconomyResult}
     */
    EconomyResult clanWithdraw(String clanName, String currencyId, double amount, UUID withdrawer);

    /**
     * Returns the top {@code limit} players by balance for the given currency,
     * ordered descending.
     *
     * @param currencyId the currency id
     * @param limit      maximum entries to return
     * @return ordered list of UUID → balance entries; never {@code null}
     */
    List<Map.Entry<UUID, Double>> getTopBalances(String currencyId, int limit);

    /**
     * Returns the last {@code limit} transactions for the given player,
     * most-recent first.
     *
     * @param uuid  player UUID
     * @param limit maximum records to return
     * @return ordered list; never {@code null}
     */
    List<ITransaction> getTransactions(UUID uuid, int limit);


    /**
     * Forces an immediate flush of all cached balances to the database.
     */
    void saveAll();
}