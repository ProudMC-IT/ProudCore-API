package it.proud.api.module;

/**
 * Contract that every external ProudCore module must fulfil.
 *
 * <p>A module is a self-contained unit of functionality that plugs into ProudCore at runtime
 * through the {@link IModuleRegistry}. The registry drives the lifecycle: it calls
 * {@link #onEnable(ProudModuleContext)} when the module is accepted and
 * {@link #onDisable()} when it is removed or the server shuts down.</p>
 *
 * <h2>Lifecycle</h2>
 * <ol>
 *   <li>Instantiate your module and call
 *       {@code ProudCoreAPI.get().getModuleRegistry().register(module)}.</li>
 *   <li>The registry validates the id, builds a {@link ProudModuleContext}, and calls
 *       {@link #onEnable(ProudModuleContext)}.</li>
 *   <li>On server shutdown or explicit unregistration,
 *       {@link #onDisable()} is invoked automatically.</li>
 * </ol>
 *
 * <h2>Minimal implementation</h2>
 * <pre>{@code
 * public final class MyModule implements IProudModule {
 *
 *     public String getId()      { return "myplugin:mymodule"; }
 *     public String getName()    { return "My Module"; }
 *     public String getVersion() { return "1.0"; }
 *
 *     public void onEnable(ProudModuleContext ctx) {
 *         ctx.getLogger().info("My module is enabled!");
 *     }
 *
 *     public void onDisable() { }
 * }
 * }</pre>
 *
 * <p>Prefer extending {@link AbstractProudModule} to avoid boilerplate.</p>
 *
 * @author  ProudCore Team
 * @version 1.0
 * @since   1.0
 * @see     IModuleRegistry
 * @see     AbstractProudModule
 */
public interface IProudModule {

    /**
     * Returns the globally unique identifier for this module.
     *
     * <p>The id must be non-null, non-blank, and follow the convention
     * {@code "pluginname:modulename"} (all lowercase, no spaces). Two modules sharing the
     * same id cannot coexist in the same registry.</p>
     *
     * @return the non-{@code null} unique module identifier
     */
    String getId();

    /**
     * Returns a human-readable name for this module, used in logs and admin commands.
     *
     * @return the non-{@code null} display name
     */
    String getName();

    /**
     * Returns the version string of this module (e.g. {@code "1.0.0"}).
     *
     * @return the non-{@code null} version string
     */
    String getVersion();

    /**
     * Called by the registry immediately after the module is accepted for registration.
     *
     * <p>All initialisation — registering listeners, commands, starting tasks — should
     * happen here. Any unchecked exception thrown from this method causes the module's state
     * to transition to {@link ModuleState#ERRORED}; the module will not be usable.</p>
     *
     * @param ctx the non-{@code null} context providing access to the ProudCore API and helpers
     */
    void onEnable(ProudModuleContext ctx);

    /**
     * Called by the registry when the module is unregistered or the server shuts down.
     *
     * <p>Release all resources here (cancel tasks, close streams, etc.).
     * This method is called even if {@link #onEnable(ProudModuleContext)} threw an exception,
     * so implementations must be null-safe.</p>
     */
    void onDisable();

    /**
     * Called when a global reload is triggered (e.g. via {@code /core reload}).
     *
     * <p>The default implementation is a no-op; override if the module supports live
     * configuration reloading.</p>
     */
    default void onReload() {}

    /**
     * Returns the current lifecycle state of this module.
     *
     * <p>The default implementation always returns {@link ModuleState#UNREGISTERED}.
     * {@link AbstractProudModule} tracks state automatically; if you implement
     * {@link IProudModule} directly, you are responsible for updating this value.</p>
     *
     * @return the non-{@code null} current state
     */
    default ModuleState getState() {
        return ModuleState.UNREGISTERED;
    }
}