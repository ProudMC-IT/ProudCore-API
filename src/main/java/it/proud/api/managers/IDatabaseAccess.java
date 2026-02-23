package it.proud.api.managers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Provides external ProudCore modules with controlled access to the shared
 * MySQL database managed by ProudCore's HikariCP connection pool.
 *
 * <p>Modules obtain this handle via {@link it.proud.api.module.ProudModuleContext#getDatabaseAccess()}.
 * All operations share the same underlying pool used by ProudCore itself, so
 * modules benefit from connection reuse without managing their own datasource.</p>
 *
 * <h2>Usage guidelines</h2>
 * <ul>
 *   <li>Always prefer the helper methods ({@link #execute}, {@link #query},
 *       {@link #transaction}) over raw {@link #getConnection()} — they handle
 *       connection lifecycle and error logging automatically.</li>
 *   <li>If you must use a raw {@link Connection}, always close it in a
 *       {@code try-with-resources} block to return it to the pool.</li>
 *   <li>Name your tables with a module-specific prefix (e.g. {@code mymod_scores})
 *       to avoid collisions with ProudCore's own tables.</li>
 *   <li>Call {@link #createTableIfNotExists(String)} from {@code onEnable} to
 *       ensure your schema is ready before any queries run.</li>
 * </ul>
 *
 * <h2>Typical usage</h2>
 * <pre>{@code
 * public void onEnable(ProudModuleContext ctx) {
 *     super.onEnable(ctx);
 *     db = ctx.getDatabaseAccess();
 *
 *     db.createTableIfNotExists("""
 *         CREATE TABLE IF NOT EXISTS mymod_scores (
 *             uuid    VARCHAR(36)  NOT NULL PRIMARY KEY,
 *             score   INT          NOT NULL DEFAULT 0,
 *             updated BIGINT       NOT NULL
 *         ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
 *     """);
 * }
 *
 * public void saveScore(UUID uuid, int score) {
 *     db.execute(
 *         "INSERT INTO mymod_scores (uuid, score, updated) VALUES (?, ?, ?) " +
 *         "ON DUPLICATE KEY UPDATE score = VALUES(score), updated = VALUES(updated)",
 *         ps -> {
 *             ps.setString(1, uuid.toString());
 *             ps.setInt(2, score);
 *             ps.setLong(3, System.currentTimeMillis());
 *         }
 *     );
 * }
 *
 * public int loadScore(UUID uuid) {
 *     Integer result = db.query(
 *         "SELECT score FROM mymod_scores WHERE uuid = ?",
 *         ps -> ps.setString(1, uuid.toString()),
 *         rs -> rs.next() ? rs.getInt("score") : 0
 *     );
 *     return result != null ? result : 0;
 * }
 * }</pre>
 *
 * @author  ProudCore Team
 * @version 1.0
 * @since   1.0
 * @see     it.proud.api.module.ProudModuleContext#getDatabaseAccess()
 */
public interface IDatabaseAccess {

    /**
     * Returns {@code true} if the underlying connection pool is alive and
     * able to serve connections.
     *
     * <p>Modules can call this at startup to verify that the database is
     * reachable before scheduling any async tasks.</p>
     *
     * @return {@code true} if the pool is initialised and not closed
     */
    boolean isConnected();

    /**
     * Borrows a raw {@link Connection} from the HikariCP pool.
     *
     * <p><b>You are responsible for closing this connection.</b> Always use it
     * inside a {@code try-with-resources} block:</p>
     * <pre>{@code
     * try (Connection conn = db.getConnection()) {
     *     // ... use conn
     * }
     * }</pre>
     *
     * <p>Prefer {@link #execute}, {@link #query}, or {@link #transaction}
     * for common patterns — they manage the connection lifecycle for you.</p>
     *
     * @return a live {@link Connection} from the pool; never {@code null}
     * @throws SQLException if the pool cannot provide a connection
     */
    Connection getConnection() throws SQLException;

    /**
     * Executes a DML statement (INSERT, UPDATE, DELETE, DDL) using a
     * pre-configured {@link PreparedStatement}.
     *
     * <p>The connection is acquired, used, and returned to the pool
     * automatically. Any {@link SQLException} is caught and logged as an
     * error; the method does not rethrow.</p>
     *
     * <pre>{@code
     * db.execute(
     *     "UPDATE mymod_scores SET score = score + 1 WHERE uuid = ?",
     *     ps -> ps.setString(1, uuid.toString())
     * );
     * }</pre>
     *
     * @param sql    the SQL statement; must not be {@code null}
     * @param setter a callback that binds parameters to the statement,
     *               or {@code null} if the statement has no parameters
     */
    void execute(String sql, Consumer<PreparedStatement> setter);

    /**
     * Executes a parameter-less DML or DDL statement.
     *
     * <p>Equivalent to {@code execute(sql, null)}.</p>
     *
     * @param sql the SQL statement; must not be {@code null}
     */
    void execute(String sql);

    /**
     * Executes a SELECT query and maps the resulting {@link ResultSet} to a
     * value of type {@code T}.
     *
     * <p>The connection, statement, and result set are all closed automatically.
     * Returns {@code null} if a {@link SQLException} occurs.</p>
     *
     * <pre>{@code
     * Integer score = db.query(
     *     "SELECT score FROM mymod_scores WHERE uuid = ?",
     *     ps -> ps.setString(1, uuid.toString()),
     *     rs -> rs.next() ? rs.getInt("score") : 0
     * );
     * }</pre>
     *
     * @param sql    the SELECT statement; must not be {@code null}
     * @param setter a callback that binds parameters, or {@code null}
     * @param mapper a function that reads the {@link ResultSet} and returns
     *               the desired value; must not be {@code null}
     * @param <T>    the return type
     * @return the value produced by {@code mapper}, or {@code null} on error
     */
    <T> T query(String sql, Consumer<PreparedStatement> setter, Function<ResultSet, T> mapper);

    /**
     * Executes a block of work inside a single database transaction.
     *
     * <p>Auto-commit is disabled for the duration of the block. If the
     * {@code work} consumer completes without throwing, the transaction is
     * committed. If any exception is thrown, the transaction is rolled back
     * and the exception is logged.</p>
     *
     * <pre>{@code
     * db.transaction(conn -> {
     *     try (PreparedStatement ps1 = conn.prepareStatement("DELETE FROM mymod_data WHERE uuid = ?")) {
     *         ps1.setString(1, uuid.toString());
     *         ps1.executeUpdate();
     *     }
     *     try (PreparedStatement ps2 = conn.prepareStatement("INSERT INTO mymod_archive ...")) {
     *         // ...
     *         ps2.executeUpdate();
     *     }
     * });
     * }</pre>
     *
     * @param work a consumer that performs one or more operations on the
     *             provided {@link Connection}; must not be {@code null}
     */
    void transaction(Consumer<Connection> work);

    /**
     * Convenience method that executes a {@code CREATE TABLE IF NOT EXISTS}
     * DDL statement.
     *
     * <p>Call this from your module's {@code onEnable} to ensure the required
     * schema exists before any data operations run.</p>
     *
     * <pre>{@code
     * db.createTableIfNotExists("""
     *     CREATE TABLE IF NOT EXISTS mymod_kills (
     *         uuid  VARCHAR(36) NOT NULL PRIMARY KEY,
     *         kills INT         NOT NULL DEFAULT 0
     *     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
     * """);
     * }</pre>
     *
     * @param ddl the full DDL statement; must not be {@code null}
     */
    void createTableIfNotExists(String ddl);
}