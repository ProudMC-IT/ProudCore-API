package it.proud.api.module;

import it.proud.api.ProudCoreAPI;
import org.apache.logging.log4j.Logger;

/**
 * Convenience base class for external ProudCore modules.
 *
 * <p>Extend this class instead of implementing {@link IProudModule} directly to avoid
 * boilerplate: state tracking, context storage, and default accessor methods are all
 * provided automatically.</p>
 *
 * <h2>Minimal subclass</h2>
 * <pre>{@code
 * public final class MyModule extends AbstractProudModule {
 *
 *     public MyModule() {
 *         super("myplugin:mymodule", "My Module", "1.0.0");
 *     }
 *
 *     public void onEnable(ProudModuleContext ctx) {
 *         super.onEnable(ctx);          // must be called first
 *         getLogger().info("Enabled!");
 *     }
 *
 *     public void onDisable() {
 *         getLogger().info("Disabled!");
 *     }
 * }
 * }</pre>
 *
 * @author  ProudCore Team
 * @version 1.0
 * @since   1.0
 */
public abstract class AbstractProudModule implements IProudModule {

    private final String id;
    private final String name;
    private final String version;

    private volatile ModuleState state = ModuleState.UNREGISTERED;
    private ProudModuleContext context;

    /**
     * Constructs a new module with the given identity.
     *
     * @param id      globally unique id in the form {@code "pluginname:modulename"}
     * @param name    human-readable display name
     * @param version version string (e.g. {@code "1.0.0"})
     */
    protected AbstractProudModule(String id, String name, String version) {
        this.id      = id;
        this.name    = name;
        this.version = version;
    }

    @Override
    public final String getId()      { return id;      }

    @Override
    public final String getName()    { return name;    }

    @Override
    public final String getVersion() { return version; }

    @Override
    public ModuleState getState()    { return state;   }

    /**
     * Stores the context and transitions state to {@link ModuleState#ENABLED}.
     *
     * <p><b>Subclasses must call {@code super.onEnable(ctx)} as the very first statement</b>
     * to ensure the context is available to helper methods before any subclass code runs.</p>
     *
     * @param ctx the non-{@code null} context provided by the registry
     */
    @Override
    public void onEnable(ProudModuleContext ctx) {
        this.context = ctx;
        this.state   = ModuleState.ENABLED;
    }

    /**
     * Transitions state to {@link ModuleState#DISABLED}.
     *
     * <p>Subclasses overriding this method do not need to call {@code super.onDisable()},
     * but doing so keeps the state consistent.</p>
     */
    @Override
    public void onDisable() {
        this.state = ModuleState.DISABLED;
    }

    /**
     * Sets the module state directly.
     *
     * <p>Used internally by the registry to mark a module as {@link ModuleState#ERRORED}
     * when {@code onEnable} throws. Subclasses may also call this if needed.</p>
     *
     * @param state the new state; must not be {@code null}
     */
    public final void setState(ModuleState state) {
        this.state = state;
    }

    /**
     * Returns the {@link ProudModuleContext} provided during {@link #onEnable(ProudModuleContext)}.
     *
     * @return the context, or {@code null} if the module has not been enabled yet
     */
    protected final ProudModuleContext getContext() {
        return context;
    }

    /**
     * Shortcut for {@code getContext().getApi()}.
     *
     * @return the {@link ProudCoreAPI} instance
     * @throws IllegalStateException if called before {@link #onEnable(ProudModuleContext)}
     */
    protected final ProudCoreAPI getApi() {
        ensureEnabled();
        return context.getApi();
    }

    /**
     * Shortcut for {@code getContext().getLogger()}.
     *
     * @return the module-scoped logger
     * @throws IllegalStateException if called before {@link #onEnable(ProudModuleContext)}
     */
    protected final Logger getLogger() {
        ensureEnabled();
        return context.getLogger();
    }

    private void ensureEnabled() {
        if (context == null) {
            throw new IllegalStateException(
                    "Module '" + id + "' context is not available yet — onEnable has not been called.");
        }
    }
}