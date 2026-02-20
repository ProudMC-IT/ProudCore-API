package it.proud.api.event;

import it.proud.api.ProudCoreAPI;
import it.proud.api.module.IModuleRegistry;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired on the Bukkit event bus after ProudCore has fully initialised.
 *
 * <p>External plugins that depend on ProudCore should listen for this event before
 * registering their modules, ensuring that the {@link ProudCoreAPI} singleton and
 * the {@link IModuleRegistry} are both ready.</p>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * @EventHandler
 * public void onCoreReady(ProudCoreReadyEvent event) {
 *     event.getRegistry().register(new MyModule());
 * }
 * }</pre>
 *
 * @author  ProudCore Team
 * @version 1.0
 * @since   1.0
 */
public final class ProudCoreReadyEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final ProudCoreAPI api;

    public ProudCoreReadyEvent(ProudCoreAPI api) {
        this.api = api;
    }

    /**
     * Returns the fully-initialised {@link ProudCoreAPI} instance.
     *
     * @return the non-{@code null} API
     */
    public ProudCoreAPI getApi() {
        return api;
    }

    /**
     * Convenience accessor for the module registry.
     *
     * @return the non-{@code null} registry
     */
    public IModuleRegistry getRegistry() {
        return api.getModuleRegistry();
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}