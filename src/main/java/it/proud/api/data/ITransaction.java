package it.proud.api.data;

import java.util.UUID;

/**
 * Immutable record of a single economic transaction.
 *
 * <p>Every balance change — deposit, withdrawal, transfer, admin set — is recorded
 * as an {@code ITransaction} and can be retrieved through
 * {@link it.proud.api.managers.IEconomyManager#getTransactions(UUID, int)}.</p>
 *
 * @author ProudCore Team
 * @version 1.0
 * @since 1.0
 */
public interface ITransaction {

    /** Identifies the kind of operation that generated this transaction. */
    enum Type {
        DEPOSIT,
        WITHDRAW,
        TRANSFER_SENT,
        TRANSFER_RECEIVED,
        ADMIN_SET,
        CLAN_DEPOSIT,
        CLAN_WITHDRAW
    }

    /**
     * Returns the unique auto-incremented id of this record.
     *
     * @return the transaction id; always {@code > 0}
     */
    long id();

    /**
     * Returns the UUID of the player this transaction belongs to.
     *
     * @return non-{@code null} player UUID
     */
    UUID playerUuid();

    /**
     * Returns the currency identifier this transaction applies to.
     *
     * @return non-{@code null}, non-blank currency id
     */
    String currencyId();

    /**
     * Returns the operation type.
     *
     * @return non-{@code null} {@link Type}
     */
    Type type();

    /**
     * Returns the amount involved in the transaction.
     *
     * <p>Always positive; the direction is conveyed by {@link #type()}.</p>
     *
     * @return amount; always {@code > 0}
     */
    double amount();

    /**
     * Returns the balance of the player <em>after</em> this transaction was applied.
     *
     * @return resulting balance; always {@code >= 0}
     */
    double balanceAfter();

    /**
     * Returns a short human-readable reason for this transaction, or {@code null}.
     *
     * @return reason string, or {@code null}
     */
    String reason();

    /**
     * Returns the unix-millis timestamp of when this transaction was recorded.
     *
     * @return timestamp in milliseconds
     */
    long timestamp();
}