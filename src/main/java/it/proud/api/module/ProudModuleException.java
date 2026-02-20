package it.proud.api.module;

/**
 * Thrown when a module registration or lifecycle operation fails for a recoverable reason.
 *
 * <p>Common causes:</p>
 * <ul>
 *   <li>Attempting to register a module whose {@link IProudModule#getId() id} is already present
 *       in the registry.</li>
 *   <li>Providing a {@code null} or blank module id.</li>
 *   <li>Attempting to unregister a module that is not registered.</li>
 * </ul>
 *
 * @author  ProudCore Team
 * @version 1.0
 * @since   1.0
 */
public class ProudModuleException extends RuntimeException {

    public ProudModuleException(String message) {
        super(message);
    }

    public ProudModuleException(String message, Throwable cause) {
        super(message, cause);
    }
}