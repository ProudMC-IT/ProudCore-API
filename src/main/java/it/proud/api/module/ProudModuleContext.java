package it.proud.api.module;

import it.proud.api.ProudCoreAPI;
import org.apache.logging.log4j.Logger;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Contextual handle passed to a module during {@link IProudModule#onEnable(ProudModuleContext)}.
 *
 * <p>Provides access to the ProudCore API, a dedicated logger, an isolated data folder,
 * and the core {@link JavaPlugin} instance needed to register Bukkit listeners, tasks,
 * and commands.</p>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * public void onEnable(ProudModuleContext ctx) {
 *     this.api    = ctx.getApi();
 *     this.logger = ctx.getLogger();
 *     ctx.getCorePlugin().getServer().getPluginManager()
 *         .registerEvents(new MyListener(), ctx.getCorePlugin());
 * }
 * }</pre>
 *
 * @author  ProudCore Team
 * @version 1.0
 * @since   1.0
 */
public interface ProudModuleContext {

    /**
     * Returns the fully-initialised {@link ProudCoreAPI} singleton.
     *
     * @return the non-{@code null} API instance
     */
    ProudCoreAPI getApi();

    /**
     * Returns a {@link Logger} pre-configured with a prefix that identifies the owning module.
     *
     * @return the non-{@code null} module-scoped logger
     */
    Logger getLogger();

    /**
     * Returns a {@link File} pointing to an isolated data directory for this module.
     *
     * <p>The directory is created automatically if it does not yet exist. External modules
     * should store all persistent resources (configs, caches, etc.) inside this folder.</p>
     *
     * @return the non-{@code null} module data folder
     */
    File getDataFolder();

    /**
     * Returns the core {@link JavaPlugin} instance.
     *
     * <p>Use this to register Bukkit event listeners, schedule tasks, or access the
     * server without requiring a direct dependency on a concrete plugin class.</p>
     *
     * @return the non-{@code null} ProudCore plugin instance
     */
    JavaPlugin getCorePlugin();
}