package it.proud.api.module;

import java.util.Map;

/**
 * Optional mixin that allows a module to publish typed service objects to other modules.
 *
 * <p>Implement this interface alongside {@link IProudModule} when your module exposes one or
 * more manager interfaces that third-party plugins should be able to obtain without a hard
 * compile-time dependency on your module's implementation classes.</p>
 *
 * <h2>Publishing a service</h2>
 * <pre>{@code
 * public final class MyModule extends AbstractProudModule implements IModuleServiceProvider {
 *
 *     private MyManager manager;
 *
 *     public void onEnable(ProudModuleContext ctx) {
 *         manager = new MyManagerImpl();
 *     }
 *
 *     public Map<Class<?>, Object> getServices() {
 *         return Map.of(IMyManager.class, manager);
 *     }
 * }
 * }</pre>
 *
 * <h2>Consuming a service</h2>
 * <pre>{@code
 * ProudCoreAPI.get()
 *     .getModuleRegistry()
 *     .getService(IMyManager.class)
 *     .ifPresent(mgr -> mgr.doSomething());
 * }</pre>
 *
 * @author  ProudCore Team
 * @version 1.0
 * @since   1.0
 * @see     IModuleRegistry#getService(Class)
 */
public interface IModuleServiceProvider {

    /**
     * Returns a map of service interface classes to their concrete implementations.
     *
     * <p>Keys should be public interface types (never implementation classes), and values
     * must be non-{@code null} instances that implement the corresponding key interface.
     * The returned map itself must be non-{@code null}; return an empty map if the module
     * currently exposes no services.</p>
     *
     * @return a non-{@code null} map of {@code interfaceClass → implementation}
     */
    Map<Class<?>, Object> getServices();
}