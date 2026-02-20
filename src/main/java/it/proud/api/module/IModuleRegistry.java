package it.proud.api.module;

import java.util.Map;
import java.util.Optional;

/**
 * Central registry for external ProudCore modules.
 *
 * <p>{@code IModuleRegistry} manages the full lifecycle of every {@link IProudModule} that
 * plugs into ProudCore at runtime. It is exposed through
 * {@link it.proud.api.ProudCoreAPI#getModuleRegistry()} and is available as soon as
 * {@link it.proud.api.event.ProudCoreReadyEvent} has been fired.</p>
 *
 * <h2>Registering a module</h2>
 * <pre>{@code
 * @EventHandler
 * public void onCoreReady(ProudCoreReadyEvent event) {
 *     ProudCoreAPI.get().getModuleRegistry().register(new MyModule());
 * }
 * }</pre>
 *
 * <h2>Consuming a cross-module service</h2>
 * <pre>{@code
 * registry.getService(IMyManager.class)
 *         .ifPresent(mgr -> mgr.doSomething());
 * }</pre>
 *
 * @author  ProudCore Team
 * @version 1.0
 * @since   1.0
 * @see     IProudModule
 * @see     IModuleServiceProvider
 */
public interface IModuleRegistry {

    /**
     * Registers and enables the given module.
     *
     * <p>The registry will:</p>
     * <ol>
     *   <li>Validate the module id (non-null, non-blank, not already registered).</li>
     *   <li>Build a {@link ProudModuleContext} and call
     *       {@link IProudModule#onEnable(ProudModuleContext)}.</li>
     *   <li>If the module implements {@link IModuleServiceProvider}, index its services.</li>
     *   <li>Fire {@link it.proud.api.event.ProudModuleRegisteredEvent} on the Bukkit event bus.</li>
     * </ol>
     *
     * <p>If {@code onEnable} throws, the module's state is set to
     * {@link ModuleState#ERRORED} and it remains in the registry (so the error is visible
     * via {@link #getAll()}) but its services are not published.</p>
     *
     * @param module the module to register; must not be {@code null}
     * @throws ProudModuleException if the id is null/blank or is already registered
     */
    void register(IProudModule module);

    /**
     * Disables and removes the module with the given id.
     *
     * <p>{@link IProudModule#onDisable()} is always called, even if the module is in
     * {@link ModuleState#ERRORED}. Services published by the module are removed from
     * the service index. {@link it.proud.api.event.ProudModuleUnregisteredEvent} is fired
     * after cleanup.</p>
     *
     * @param moduleId the id of the module to remove; must not be {@code null}
     * @throws ProudModuleException if no module with that id is registered
     */
    void unregister(String moduleId);

    /**
     * Unregisters all currently registered modules in an unspecified order.
     *
     * <p>Called automatically by ProudCore during {@code onDisable()}; external code
     * should not normally need to invoke this.</p>
     */
    void unregisterAll();

    /**
     * Returns the module registered under the given id, if present.
     *
     * @param moduleId the id to look up; must not be {@code null}
     * @return an {@link Optional} containing the module, or empty if none is registered
     */
    Optional<IProudModule> getModule(String moduleId);

    /**
     * Returns a typed view of the module registered under the given id.
     *
     * <pre>{@code
     * registry.getModule("myplugin:arena", IArenaModule.class)
     *         .ifPresent(m -> m.startArena("arena1"));
     * }</pre>
     *
     * @param moduleId the id to look up; must not be {@code null}
     * @param type     the expected type; must not be {@code null}
     * @param <T>      the module type
     * @return an {@link Optional} containing the module cast to {@code T}, or empty
     *         if not registered or not assignable to {@code T}
     */
    <T extends IProudModule> Optional<T> getModule(String moduleId, Class<T> type);

    /**
     * Returns an unmodifiable snapshot of all registered modules keyed by their id.
     *
     * @return a non-{@code null}, unmodifiable map
     */
    Map<String, IProudModule> getAll();

    /**
     * Returns {@code true} if a module with the given id is registered and currently
     * in state {@link ModuleState#ENABLED}.
     *
     * @param moduleId the id to check; must not be {@code null}
     * @return {@code true} if the module is enabled
     */
    boolean isEnabled(String moduleId);

    /**
     * Calls {@link IProudModule#onReload()} on every module currently in
     * {@link ModuleState#ENABLED}.
     *
     * <p>Invoked automatically by ProudCore's reload command; external code may also
     * call this directly.</p>
     */
    void reloadAll();

    /**
     * Returns a service implementation published by any registered module that implements
     * {@link IModuleServiceProvider}.
     *
     * @param serviceClass the interface type to look up; must not be {@code null}
     * @param <T>          the service type
     * @return an {@link Optional} containing the service, or empty if no module
     *         has published a service for that interface
     */
    <T> Optional<T> getService(Class<T> serviceClass);
}