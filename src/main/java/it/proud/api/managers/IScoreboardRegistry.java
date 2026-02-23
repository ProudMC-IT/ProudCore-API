package it.proud.api.managers;

import it.proud.api.managers.IScoreboardProvider.ScoreboardTemplate;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Central registry that collects scoreboard templates from both the ProudCore
 * core config ({@code scoreboard/config.yml}) and from external
 * {@link IScoreboardProvider} modules.
 *
 * <p>Templates from the core are always present under the namespace
 * {@code "core"} (e.g. {@code "core:main"}, {@code "core:pre_lobby"},
 * {@code "core:wave_starter"}). Templates contributed by external providers
 * are stored under their own namespace (e.g. {@code "mymod:arena"}).</p>
 *
 * <h2>Registering a provider</h2>
 * <pre>{@code
 * @EventHandler
 * public void onCoreReady(ProudCoreReadyEvent event) {
 *     event.getApi().getScoreboardRegistry().registerProvider(new MyScoreboardProvider());
 * }
 * }</pre>
 *
 * <h2>Applying an external template</h2>
 * <pre>{@code
 * IScoreboardManager sb = ProudCoreAPI.get().getScoreboardManager();
 * sb.setExternalScoreboard(player, "mymod:arena");
 * }</pre>
 *
 * @author ProudCore Team
 * @version 1.0
 * @since 1.0
 * @see IScoreboardProvider
 * @see IScoreboardManager
 */
public interface IScoreboardRegistry {

    /**
     * Registers an external scoreboard provider and indexes all of its templates.
     *
     * <p>If a provider with the same {@link IScoreboardProvider#getProviderId() id}
     * is already registered, the old one is replaced and its templates are
     * overwritten. A warning is logged in that case.</p>
     *
     * @param provider the provider to register; must not be {@code null}
     * @throws IllegalArgumentException if {@code provider.getProviderId()} is
     *                                  {@code null} or blank
     */
    void registerProvider(IScoreboardProvider provider);

    /**
     * Removes the provider with the given id and unregisters all of its templates.
     *
     * <p>Any player currently displaying one of the removed templates will keep
     * the template rendered until the next scoreboard update cycle, after which
     * the lines will be empty. Callers should switch affected players to a valid
     * template before unregistering.</p>
     *
     * @param providerId the provider namespace to remove; must not be {@code null}
     * @return {@code true} if a provider with that id existed and was removed,
     *         {@code false} otherwise
     */
    boolean unregisterProvider(String providerId);

    /**
     * Returns {@code true} if a provider with the given id is currently registered.
     *
     * @param providerId the provider namespace to check; must not be {@code null}
     * @return {@code true} if registered
     */
    boolean isProviderRegistered(String providerId);

    /**
     * Returns the fully-qualified template for the given key, or
     * {@link Optional#empty()} if no template is registered under that key.
     *
     * <p>Keys are always in the form {@code "<namespace>:<templateName>"},
     * e.g. {@code "core:main"}, {@code "mymod:arena"}.</p>
     *
     * @param fullKey the namespaced template key; must not be {@code null}
     * @return an {@link Optional} containing the template, or empty
     */
    Optional<ScoreboardTemplate> getTemplate(String fullKey);

    /**
     * Returns {@code true} if a template is registered under the given fully-qualified key.
     *
     * @param fullKey the namespaced template key; must not be {@code null}
     * @return {@code true} if a template exists for that key
     */
    boolean hasTemplate(String fullKey);

    /**
     * Returns an unmodifiable snapshot of every registered template,
     * keyed by fully-qualified name.
     *
     * <p>Includes both core templates ({@code "core:*"}) and all templates
     * from registered external providers.</p>
     *
     * @return a non-{@code null}, unmodifiable map of {@code fullKey → template}
     */
    Map<String, ScoreboardTemplate> getAllTemplates();

    /**
     * Returns the set of template keys contributed by the given provider namespace.
     *
     * @param providerId the provider namespace; must not be {@code null}
     * @return a non-{@code null}, unmodifiable set of fully-qualified keys;
     *         empty if the provider is not registered
     */
    Set<String> getTemplatesByProvider(String providerId);

    /**
     * Returns the set of all currently registered provider namespaces.
     *
     * @return a non-{@code null}, unmodifiable set of provider ids
     */
    Set<String> getRegisteredProviders();

    /**
     * Forces the registry to re-read the core {@code scoreboard/config.yml}
     * and refresh the {@code "core:*"} templates accordingly.
     *
     * <p>External provider templates are unaffected by this call.</p>
     */
    void reloadCoreTemplates();
}