package it.proud.api.managers;

import java.util.Map;

/**
 * Contract for external modules that want to contribute scoreboard templates
 * to the ProudCore scoreboard system.
 *
 * <p>A module implementing this interface can register one or more named
 * {@link ScoreboardTemplate} objects into the central {@link IScoreboardRegistry}.
 * Once registered, those templates become available server-wide and can be
 * applied to any player via {@link IScoreboardManager}.</p>
 *
 * <h2>Typical usage inside a module</h2>
 * <pre>{@code
 * public final class MyModule extends AbstractProudModule implements IScoreboardProvider {
 *
 *     public String getProviderId() { return "mymodule"; }
 *
 *     public Map<String, ScoreboardTemplate> getTemplates() {
 *         return Map.of(
 *             "arena",  new ScoreboardTemplate("&c&lARENА", List.of("&7Kills: &f%mymod_kills%")),
 *             "result", new ScoreboardTemplate("&a&lGAME OVER", List.of("&7Winner: &f%mymod_winner%"))
 *         );
 *     }
 * }
 * }</pre>
 *
 * @author ProudCore Team
 * @version 1.0
 * @since 1.0
 * @see IScoreboardRegistry
 * @see IScoreboardManager
 */
public interface IScoreboardProvider {

    /**
     * Immutable scoreboard template composed of a title and a list of lines.
     *
     * <p>Both title and lines support Minecraft legacy color codes ({@code &a},
     * {@code &l}, etc.), hex colors ({@code <#RRGGBB>}, {@code &#RRGGBB}),
     * MiniMessage formatting tags, and PlaceholderAPI placeholders.</p>
     *
     * @param title the sidebar title; must not be {@code null}
     * @param lines the ordered list of sidebar lines; must not be {@code null}
     */
    record ScoreboardTemplate(String title, java.util.List<String> lines) {

        /**
         * Compact constructor — validates that neither field is {@code null}.
         *
         * @throws NullPointerException if {@code title} or {@code lines} is {@code null}
         */
        public ScoreboardTemplate {
            java.util.Objects.requireNonNull(title, "title must not be null");
            java.util.Objects.requireNonNull(lines, "lines must not be null");
            lines = java.util.List.copyOf(lines);
        }
    }

    /**
     * Returns the unique, lowercase namespace identifier for this provider.
     *
     * <p>The id is used to namespace all template keys contributed by this provider
     * so they never clash with templates from other modules or from the core.
     * Convention: use the same string as your module id without the colon
     * (e.g. if your module id is {@code "mymod:arena"}, return {@code "mymod"}).</p>
     *
     * @return the non-{@code null}, non-blank provider namespace
     */
    String getProviderId();

    /**
     * Returns all scoreboard templates contributed by this provider, keyed by
     * a short template name (without namespace prefix).
     *
     * <p>The registry will prefix each key with {@link #getProviderId()} and a
     * colon when storing them, so a key {@code "arena"} from provider {@code "mymod"}
     * is ultimately stored as {@code "mymod:arena"}.</p>
     *
     * <p>The returned map must be non-{@code null}; return an empty map if the
     * provider currently has no templates to contribute.</p>
     *
     * @return a non-{@code null} map of {@code templateName → ScoreboardTemplate}
     */
    Map<String, ScoreboardTemplate> getTemplates();
}