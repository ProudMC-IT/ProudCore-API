package it.proud.api.event;

import it.proud.api.module.IProudModule;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired on the Bukkit event bus after a module has been unregistered and disabled.
 *
 * <p>Listeners can use this event to tear down cross-module integrations when a
 * dependency module is removed at runtime.</p>
 *
 * @author  ProudCore Team
 * @version 1.0
 * @since   1.0
 */
public final class ProudModuleUnregisteredEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final IProudModule module;

    public ProudModuleUnregisteredEvent(IProudModule module) {
        this.module = module;
    }

    /**
     * Returns the module that was just unregistered.
     *
     * @return the non-{@code null} module
     */
    public IProudModule getModule() {
        return module;
    }

    /**
     * Convenience accessor for the unregistered module's id.
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