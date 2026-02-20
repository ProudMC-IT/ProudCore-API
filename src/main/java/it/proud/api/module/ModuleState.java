package it.proud.api.module;

/**
 * Represents the lifecycle state of a {@link IProudModule}.
 *
 * <p>Transitions:</p>
 * <pre>
 *   UNREGISTERED → ENABLED → DISABLED
 *                ↘ ERRORED
 * </pre>
 */
public enum ModuleState {

    /**
     * The module has been instantiated but not yet submitted to the {@link IModuleRegistry}.
     */
    UNREGISTERED,

    /**
     * The module has been registered and its {@code onEnable} completed without errors.
     */
    ENABLED,

    /**
     * The module was previously enabled and has since been unregistered or explicitly disabled.
     */
    DISABLED,

    /**
     * An unhandled exception occurred during {@code onEnable}; the module is not operational.
     */
    ERRORED
}