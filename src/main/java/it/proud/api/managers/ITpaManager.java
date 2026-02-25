package it.proud.api.managers;

import java.time.Duration;
import java.util.UUID;

/**
 * Manager for teleport-request ({@code /tpa} / {@code /tpahere}) sessions.
 *
 * <p>{@code ITpaManager} handles the full lifecycle of pending teleport
 * requests: creation, lookup, expiry checking, and cancellation. Requests
 * are held in memory only and are never persisted to disk.</p>
 *
 * <h2>Request types</h2>
 * <ul>
 *   <li>{@link RequestType#TO_TARGET} — the sender wants to teleport
 *       <em>to</em> the target ({@code /tpa <target>}).</li>
 *   <li>{@link RequestType#HERE} — the sender wants to pull the target
 *       <em>to themselves</em> ({@code /tpahere <target>}).</li>
 * </ul>
 *
 * <h2>Typical usage</h2>
 * <pre>{@code
 * ITpaManager tpa = ProudCoreAPI.get().getTpaManager();
 *
 * // Create a request that expires after 60 seconds
 * tpa.createRequest(sender.getUniqueId(), target.getUniqueId(),
 *                   ITpaManager.RequestType.TO_TARGET, Duration.ofSeconds(60));
 *
 * // On /tpaccept — check if a valid pending request exists
 * ITpaManager.TpaRequest req = tpa.getForTarget(target.getUniqueId());
 * if (req != null) {
 *     tpa.clearRequest(req.from(), req.to());
 *     // perform teleport…
 * }
 * }</pre>
 *
 * @author  ProudCore Team
 * @version 1.0
 * @since   1.0
 */
public interface ITpaManager {

    /**
     * The direction of the teleport request.
     */
    enum RequestType {
        /**
         * The requester ({@code from}) wants to teleport to the target ({@code to}).
         * On accept: {@code from} teleports to {@code to}.
         */
        TO_TARGET,

        /**
         * The requester ({@code from}) wants the target ({@code to}) to teleport here.
         * On accept: {@code to} teleports to {@code from}.
         */
        HERE
    }

    /**
     * Immutable snapshot of a pending teleport request.
     */
    interface TpaRequest {

        /**
         * Returns the UUID of the player who sent the request.
         *
         * @return non-{@code null} sender UUID
         */
        UUID from();

        /**
         * Returns the UUID of the player who must accept or deny the request.
         *
         * @return non-{@code null} target UUID
         */
        UUID to();

        /**
         * Returns the type of teleport this request represents.
         *
         * @return non-{@code null} {@link RequestType}
         */
        RequestType type();

        /**
         * Returns the unix-millis timestamp at which this request expires.
         *
         * @return expiry timestamp in milliseconds
         */
        long expiresAt();

        /**
         * Returns {@code true} if the current time has passed {@link #expiresAt()}.
         *
         * @return {@code true} if the request has expired
         */
        boolean isExpired();
    }

    /**
     * Creates a new teleport request, replacing any existing request from the
     * same sender or to the same target.
     *
     * @param from     the UUID of the player sending the request; must not be {@code null}
     * @param to       the UUID of the player receiving the request; must not be {@code null}
     * @param type     the type of teleport; must not be {@code null}
     * @param duration how long the request remains valid; must not be {@code null}
     */
    void createRequest(UUID from, UUID to, RequestType type, Duration duration);

    /**
     * Returns the pending request addressed to the given target player, or
     * {@code null} if none exists or it has expired.
     *
     * <p>Expired requests are automatically removed when encountered.</p>
     *
     * @param target the UUID of the target player; must not be {@code null}
     * @return the pending {@link TpaRequest}, or {@code null}
     */
    TpaRequest getForTarget(UUID target);

    /**
     * Returns the pending request addressed to the given target <em>and</em>
     * originating from the specified sender, or {@code null} if none exists.
     *
     * <p>Use this overload when you need to match a specific sender (e.g. when
     * there could be multiple concurrent requests).</p>
     *
     * @param target the UUID of the target player; must not be {@code null}
     * @param from   the UUID of the expected sender; must not be {@code null}
     * @return the matching {@link TpaRequest}, or {@code null}
     */
    TpaRequest getForTarget(UUID target, UUID from);

    /**
     * Returns {@code true} if there is an active (non-expired) request pending
     * for the given target player.
     *
     * @param target the UUID of the target player; must not be {@code null}
     * @return {@code true} if a pending request exists
     */
    default boolean hasPendingRequest(UUID target) {
        return getForTarget(target) != null;
    }

    /**
     * Cancels any active request sent by the given player.
     *
     * @param from the UUID of the sender; must not be {@code null}
     */
    void clearBySender(UUID from);

    /**
     * Cancels any active request directed at the given target player.
     *
     * @param to the UUID of the target; must not be {@code null}
     */
    void clearByTarget(UUID to);

    /**
     * Cancels the given request, removing it from both the sender and
     * target indices.
     *
     * @param from the sender UUID of the request to cancel; must not be {@code null}
     * @param to   the target UUID of the request to cancel; must not be {@code null}
     */
    void clearRequest(UUID from, UUID to);
}