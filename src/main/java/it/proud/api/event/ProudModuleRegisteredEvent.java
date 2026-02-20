package it.proud.api.event;

import it.proud.api.module.IProudModule;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired on the Bukkit event bus after a module has been successfully registered and enabled.
 *
 * <p>Listeners can use this event to react when a specific module becomes available,
 * for example to set up cross-module integrations.</p>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * @EventHandler
 * public void onModuleRegistered(ProudModuleRegisteredEvent event) {
 *     if (event.getModuleId().equals("myplugin:arena")) {
 *         // integrate with the arena module
 *     }
 * }
 * }</pre>
 *
 * @author  ProudCore Team
 * @version 1.0
 * @since   1.0
 */
public final class ProudModuleRegisteredEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final IProudModule module;

    public ProudModuleRegisteredEvent(IProudModule module) {
        this.module = module;
    }

    /**
     * Returns the module that was just registered.
     *
     * @return the non-{@code null} module
     */
    public IProudModule getModule() {
        return module;
    }

    /**
     * Convenience accessor for the registered module's id.
     *
     * @return the non-{@code null} module id
     */
    public String getModuleId() {
        return module.getId();
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}