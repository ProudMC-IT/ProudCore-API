<div align="center">

# 🏰 ProudCore API

### The official public API for the **ProudCore** Minecraft plugin ecosystem

[![](https://jitpack.io/v/ProudMC-IT/ProudCore-API.svg)](https://jitpack.io/#ProudMC-IT/ProudCore-API)
![Java](https://img.shields.io/badge/Java-21+-orange?logo=openjdk)
![Minecraft](https://img.shields.io/badge/Minecraft-1.20%2B-green?logo=minecraft)
![License](https://img.shields.io/badge/License-Proprietary-red)
![Build](https://img.shields.io/badge/Build-JitPack-blue?logo=github)

> Integrate your plugins seamlessly with ProudCore's clan system, economy, scoreboards, player stats, schematics and much more — all through a clean, well-documented API.

</div>

---

## 📑 Table of Contents

- [Overview](#-overview)
- [Installation](#-installation)
    - [Gradle (Groovy)](#gradle-groovy)
    - [Gradle (Kotlin DSL)](#gradle-kotlin-dsl)
    - [Maven](#maven)
- [Architecture](#-architecture)
- [Getting Started](#-getting-started)
- [Managers Reference](#-managers-reference)
    - [IClanManager](#iclanmanager)
    - [IPlayerManager](#iplayermanager)
    - [IPlayerStatsManager](#iplayerstatsmanager)
    - [IEconomyManager](#ieconomymanager)
    - [ICharManager](#icharmanager)
    - [ISchematicsManager](#ischematicsmanager)
    - [IClanKillsManager](#iclankillsmanager)
    - [IScoreboardManager](#iscoreboardmanager)
    - [IScoreboardRegistry](#iscoreboardregistry)
- [Module System](#-module-system)
    - [Creating a Module](#creating-a-module)
    - [Module Lifecycle](#module-lifecycle)
    - [Publishing Services](#publishing-services)
    - [Database Access](#database-access)
- [Events](#-events)
- [Full Plugin Examples](#-full-plugin-examples)
    - [Example 1 — Simple Plugin using the API](#example-1--simple-plugin-using-the-api)
    - [Example 2 — Plugin that registers an External Module](#example-2--plugin-that-registers-an-external-module)
    - [Example 3 — Plugin with Custom Scoreboard Templates](#example-3--plugin-with-custom-scoreboard-templates)
- [Data Model Reference](#-data-model-reference)
- [Best Practices](#-best-practices)

---

## 🔍 Overview

**ProudCore** is a comprehensive Minecraft server plugin providing a foundation for clan-based gameplay, economy, territory claiming, scoreboards, and more. This API (`proudcore-api`) is the public interface that external plugins use to interact with all ProudCore subsystems — without needing access to the internal implementation.

**What you can do with this API:**

| System | What you can do |
|---|---|
| 🏰 **Clans** | Read clan data, check membership, manage claims, create/disband clans |
| 👤 **Players** | Read & write player power, chunk limits, and persistent data |
| 📊 **Stats** | Access Minecraft stats (kills, deaths, KDR, playtime, etc.) |
| 💰 **Economy** | Multi-currency deposits, withdrawals, transfers, clan banks, leaderboards |
| 🎨 **Characters** | Register and resolve custom Unicode glyphs for texture packs |
| 🗺️ **Schematics** | Query pasted schematic locations and block lists |
| 📋 **Scoreboards** | Display and register custom scoreboard templates |
| 🧩 **Modules** | Register full external modules with services, DB access, and lifecycle management |

---

## 📦 Installation

Add the JitPack repository and the dependency to your build tool. Replace `VERSION` with the latest badge version shown above.

### Gradle (Groovy)

`settings.gradle`:
```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

`build.gradle`:
```groovy
dependencies {
    implementation 'com.github.ProudMC-IT:ProudCore-API:VERSION'
}
```

### Gradle (Kotlin DSL)

`settings.gradle.kts`:
```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

`build.gradle.kts`:
```kotlin
dependencies {
    implementation("com.github.ProudMC-IT:ProudCore-API:VERSION")
}
```

### Maven

`pom.xml`:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.ProudMC-IT</groupId>
        <artifactId>ProudCore-API</artifactId>
        <version>VERSION</version>
    </dependency>
</dependencies>
```

> ⚠️ **Important:** `proudcore-api` should be used as a **compile-only / provided** dependency. The actual implementation is provided at runtime by the ProudCore plugin loaded on your server. Do not shade it into your plugin jar.

Also make sure ProudCore is listed as a dependency in your `plugin.yml`:
```yaml
depend: [ProudCore]
```

---

## 🏗️ Architecture

```
ProudCoreAPI (singleton)
│
├── IClanManager          → Clan CRUD, membership, territorial claims
├── IPlayerManager        → Per-player power & chunk-limit data
├── IPlayerStatsManager   → Bukkit statistics cache (kills, deaths, KDR…)
├── IEconomyManager       → Multi-currency economy + clan banks
├── ICharManager          → Unicode glyph registry
├── ISchematicsManager    → Pasted-schematic block registry
├── IClanKillsManager     → Clan & player kill counters
├── IScoreboardManager    → Sidebar scoreboard control per-player
├── IScoreboardRegistry   → Scoreboard template registry (core + external)
└── IModuleRegistry       → External module lifecycle management
```

The singleton is available from the moment `ProudCoreReadyEvent` fires and until the server shuts down.

---

## 🚀 Getting Started

The very first thing to do is obtain the API instance. There are two safe approaches:

### Approach A — Via `ProudCoreReadyEvent` (recommended)

```java
@EventHandler
public void onProudCoreReady(ProudCoreReadyEvent event) {
    ProudCoreAPI api = event.getApi();
    // Safe to use all managers from here
}
```

### Approach B — Direct access after server startup

```java
// Inside onEnable(), after ProudCore is guaranteed to be loaded:
ProudCoreAPI api = ProudCoreAPI.get(); // throws IllegalStateException if not loaded

// Or null-safe variant:
ProudCoreAPI api = ProudCoreAPI.getOrNull(); // returns null if not loaded
```

---

## 📖 Managers Reference

### IClanManager

The primary gateway for all clan operations.

```java
IClanManager clans = ProudCoreAPI.get().getClanManager();

// --- READ ---
IClan clan = clans.getClan("spartans");          // by internal name (case-sensitive)
IClan myClan = clans.getPlayerClan(playerUUID);  // null if player is clanless
boolean hasClan = clans.isInClan(playerUUID);
Map<String, ? extends IClan> allClans = clans.getAllClans();

// --- TERRITORY ---
IClan owner = clans.getClaimOwner(chunk);        // null if unclaimed
// Protect territory:
if (owner != null && !owner.isMember(attacker.getUniqueId())) {
    event.setCancelled(true);
    attacker.sendMessage("This territory belongs to " + owner.getDisplayName() + "!");
}

// --- WRITE ---
boolean created = clans.createClan("spartans", leaderUUID);
boolean disbanded = clans.disbandClan("spartans"); // irreversible!
clans.saveAll(); // force-flush to DB (use sparingly)

// --- CLAN OBJECT ---
clan.getName();           // internal key, e.g. "spartans"
clan.getDisplayName();    // may contain color codes, e.g. "&c&lSpartans"
clan.getDescription();    // motto / lore text
clan.getLeader();         // UUID of the leader
clan.getMembers();        // unmodifiable Set<UUID> (includes leader)
clan.isMember(uuid);      // true for members AND leader
clan.isLeader(uuid);
clan.getTotalPower();     // current aggregate power
clan.getTotalMaxPower();  // theoretical max
clan.getClaimsCount();    // number of claimed chunks
clan.getHome();           // Location or null
```

---

### IPlayerManager

Manages persistent per-player power and claim data.

```java
IPlayerManager players = ProudCoreAPI.get().getPlayerManager();

IPlayerData data = players.getPlayer(uuid); // never null — creates default if missing
boolean loaded = players.isLoaded(uuid);    // true if currently in memory cache

// IPlayerData read
data.getUuid();
data.getName();
data.getPower();       // current power, clamped to [0, maxPower]
data.getMaxPower();
data.getChunkLimit();  // personal chunk-claim cap (independent of power)

// IPlayerData write (all auto-persisted)
data.setPower(15.0);
data.setMaxPower(20.0);
data.setChunkLimit(10);
data.addPower(5.0);    // capped at maxPower; ignores <= 0
data.takePower(3.0);   // floored at 0; typically called on death
```

> **Performance note:** Online players are always cached. For offline players, `getPlayer()` may trigger a synchronous DB read. Guard with `isLoaded()` if you're doing bulk operations.

---

### IPlayerStatsManager

Cache-backed access to standard Bukkit statistics.

```java
IPlayerStatsManager statsManager = ProudCoreAPI.get().getPlayerStatsManager();

IPlayerStats stats = statsManager.getStats(uuid); // never null

stats.getKills();             // PvP kills (PLAYER_KILLS)
stats.getDeaths();            // total deaths (DEATHS)
stats.getMobKills();          // mob kills (MOB_KILLS)
stats.getKdr();               // kills / deaths (0.0 if no deaths)
stats.getWalkOneCm();         // cm walked
stats.getPlayTimeTicks();     // ticks of playtime
stats.getPlayTimeSeconds();   // convenience: ticks / 20
stats.getPlayTimeMinutes();   // convenience: seconds / 60
stats.getItemsCrafted();
stats.getJumps();
stats.getLastUpdated();       // unix-millis of last snapshot

// Refresh
statsManager.refreshStats(uuid);
statsManager.refreshAll();

// Shortcuts
statsManager.getKills(uuid);
statsManager.getDeaths(uuid);
statsManager.getKdr(uuid);
```

---

### IEconomyManager

Full-featured multi-currency economy with clan banks and transaction history.

```java
IEconomyManager eco = ProudCoreAPI.get().getEconomyManager();

// --- CURRENCIES ---
ICurrency coins = eco.getCurrency("coins");    // null if not found
ICurrency primary = eco.getPrimaryCurrency();  // Vault-bridged currency
Collection<ICurrency> all = eco.getCurrencies();

coins.id();              // "coins"
coins.nameSingular();    // "Coin"
coins.namePlural();      // "Coins"
coins.symbol();          // "⛃"
coins.startingBalance(); // default balance on first join
coins.maxBalance();      // -1 = unlimited
coins.format(1250.0);    // "⛃ 1,250"

// --- PLAYER BALANCES ---
double bal = eco.getBalance(uuid, "coins");
Map<String, Double> allBals = eco.getAllBalances(uuid);
boolean canAfford = eco.has(uuid, "coins", 100.0);

// --- OPERATIONS ---
EconomyResult res = eco.deposit(uuid, "coins", 500.0, "Quest reward");
EconomyResult res = eco.withdraw(uuid, "coins", 100.0, "Shop purchase");
EconomyResult res = eco.set(uuid, "coins", 0.0, "Admin reset"); // bypasses limits
EconomyResult res = eco.transfer(fromUUID, toUUID, "coins", 50.0, "Gift");

if (!res.isSuccess()) {
    player.sendMessage(res.getMessage()); // e.g. "Insufficient funds"
}

// --- CLAN BANK ---
double clanBal = eco.getClanBalance("spartans", "coins");
eco.clanDeposit("spartans", "coins", 200.0, depositorUUID);
eco.clanWithdraw("spartans", "coins", 100.0, leaderUUID); // no leader check — do it yourself!

// --- LEADERBOARD ---
List<Map.Entry<UUID, Double>> top10 = eco.getTopBalances("coins", 10);

// --- HISTORY ---
List<ITransaction> recent = eco.getTransactions(uuid, 20);
for (ITransaction t : recent) {
    t.id();           // auto-increment id
    t.playerUuid();
    t.currencyId();
    t.type();         // DEPOSIT, WITHDRAW, TRANSFER_SENT, TRANSFER_RECEIVED, ADMIN_SET, etc.
    t.amount();
    t.balanceAfter();
    t.reason();
    t.timestamp();    // unix-millis
}
```

---

### ICharManager

Register and resolve custom Unicode glyphs (useful for resource pack icons).

```java
ICharManager chars = ProudCoreAPI.get().getCharManager();

// Lookup
String glyph = chars.getChar("coin");       // "\uE001" or null
boolean exists = chars.exists("coin");
Set<String> names = chars.getAllNames();
int total = chars.getCharsCount();
int free = chars.getAvailableCharsCount();
Map<String, String> info = chars.getCharInfo("coin"); // metadata map or null

// Register a new glyph (picks next free private-use code point)
String newGlyph = chars.addChar("clan_shield"); // null if name taken or no slots left

// Remove
boolean removed = chars.removeChar("old_icon");

// Safe usage pattern
if (chars.exists("clan_shield")) {
    player.sendMessage("Your clan: " + clan.getName() + " " + chars.getChar("clan_shield"));
}
```

---

### ISchematicsManager

Query structures pasted with `/schematic load`.

```java
ISchematicsManager schematics = ProudCoreAPI.get().getSchematicsManager();

boolean loaded = schematics.isLoaded("arena_1", "world");
List<Location> blocks = schematics.getBlocks("arena_1", "world"); // unmodifiable, empty if not found
Set<String> inWorld = schematics.getNamesInWorld("world");
Set<String> allNames = schematics.getAllNames();
Map<String, String> allWithWorld = schematics.getAllWithWorld(); // "world:arena_1" -> display label
int count = schematics.count();
```

---

### IClanKillsManager

Aggregate kill counters per clan and per player.

```java
IClanKillsManager killsMgr = ProudCoreAPI.get().getClanKillsManager();

long clanKills = killsMgr.getClanKills("spartans");
long playerKills = killsMgr.getPlayerKills(uuid);
Map<String, Long> allClanKills = killsMgr.getAllClanKills();

killsMgr.refreshClan("spartans"); // re-read from DB
killsMgr.refreshAll();
```

---

### IScoreboardManager

Control per-player sidebar scoreboards.

```java
IScoreboardManager sb = ProudCoreAPI.get().getScoreboardManager();

sb.setMainScoreboard(player);                          // applies "core:main"
sb.setExternalScoreboard(player, "mymod:arena");       // applies any registered template
sb.removeScoreboard(player);                           // clears the sidebar

boolean active = sb.hasScoreboard(player);
String currentKey = sb.getCurrentTemplate(player);    // "core:main", "mymod:arena", or null

sb.reload();    // re-read core config from disk
sb.shutdown();  // internal use only
```

---

### IScoreboardRegistry

Manage scoreboard template providers.

```java
IScoreboardRegistry registry = ProudCoreAPI.get().getScoreboardRegistry();

// Register your provider (see Module System section)
registry.registerProvider(new MyScoreboardProvider());

// Query
Optional<ScoreboardTemplate> tpl = registry.getTemplate("mymod:arena");
boolean exists = registry.hasTemplate("mymod:arena");
Map<String, ScoreboardTemplate> all = registry.getAllTemplates();
Set<String> providerKeys = registry.getTemplatesByProvider("mymod");
Set<String> providers = registry.getRegisteredProviders();
boolean registered = registry.isProviderRegistered("mymod");

// Unregister
registry.unregisterProvider("mymod");

// Reload core templates (external providers unaffected)
registry.reloadCoreTemplates();
```

---

## 🧩 Module System

The module system allows external plugins to register **self-contained units of functionality** that plug directly into ProudCore. Modules get their own lifecycle, logger, data folder, and shared DB access.

### Creating a Module

Extend `AbstractProudModule` to avoid boilerplate:

```java
public final class MyArenaModule extends AbstractProudModule {

    private MyArenaManager arenaManager;

    public MyArenaModule() {
        super("myplugin:arena", "My Arena Module", "1.0.0");
    }

    @Override
    public void onEnable(ProudModuleContext ctx) {
        super.onEnable(ctx); // MUST be called first — stores context and sets ENABLED state

        getLogger().info("Arena module starting up...");

        // Access the API
        IClanManager clans = getApi().getClanManager();

        // Register Bukkit listeners using the core plugin instance
        ctx.getCorePlugin().getServer().getPluginManager()
            .registerEvents(new ArenaListener(this), ctx.getCorePlugin());

        // Access the shared database
        IDatabaseAccess db = ctx.getDatabaseAccess();
        db.createTableIfNotExists("""
            CREATE TABLE IF NOT EXISTS arena_scores (
                uuid  VARCHAR(36) NOT NULL PRIMARY KEY,
                score INT         NOT NULL DEFAULT 0
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
        """);

        arenaManager = new MyArenaManagerImpl(db);
    }

    @Override
    public void onDisable() {
        getLogger().info("Arena module shutting down...");
        // Clean up tasks, close resources, etc.
    }

    @Override
    public void onReload() {
        // Called by /core reload — reload your config here
    }
}
```

### Module Lifecycle

```
UNREGISTERED  →  (register called)  →  ENABLED  →  (unregister called)  →  DISABLED
                                    ↘  ERRORED  (onEnable threw an exception)
```

| State | Meaning |
|---|---|
| `UNREGISTERED` | Module instantiated, not yet submitted to registry |
| `ENABLED` | `onEnable` completed successfully — module is operational |
| `DISABLED` | `onDisable` was called — module was cleanly removed |
| `ERRORED` | `onEnable` threw an exception — module is not operational |

### Publishing Services

Implement `IModuleServiceProvider` to expose interfaces that other modules can consume without a hard dependency:

```java
public final class MyArenaModule extends AbstractProudModule implements IModuleServiceProvider {

    private IArenaManager arenaManager;

    @Override
    public void onEnable(ProudModuleContext ctx) {
        super.onEnable(ctx);
        arenaManager = new ArenaManagerImpl();
    }

    @Override
    public Map<Class<?>, Object> getServices() {
        return Map.of(IArenaManager.class, arenaManager);
    }
}
```

Another module consumes it:

```java
ProudCoreAPI.get()
    .getModuleRegistry()
    .getService(IArenaManager.class)
    .ifPresent(mgr -> mgr.startArena("arena_1"));
```

### Database Access

`ProudModuleContext` provides access to ProudCore's shared **HikariCP** connection pool:

```java
IDatabaseAccess db = ctx.getDatabaseAccess();

// One-shot DML (INSERT, UPDATE, DELETE)
db.execute(
    "INSERT INTO arena_scores (uuid, score) VALUES (?, ?) ON DUPLICATE KEY UPDATE score = score + ?",
    ps -> {
        ps.setString(1, uuid.toString());
        ps.setInt(2, points);
        ps.setInt(3, points);
    }
);

// SELECT with result mapping
Integer score = db.query(
    "SELECT score FROM arena_scores WHERE uuid = ?",
    ps -> ps.setString(1, uuid.toString()),
    rs -> rs.next() ? rs.getInt("score") : 0
);

// Atomic transaction
db.transaction(conn -> {
    try (PreparedStatement del = conn.prepareStatement("DELETE FROM arena_scores WHERE uuid = ?");
         PreparedStatement ins = conn.prepareStatement("INSERT INTO arena_archive SELECT * FROM arena_scores WHERE uuid = ?")) {
        del.setString(1, uuid.toString()); del.executeUpdate();
        ins.setString(1, uuid.toString()); ins.executeUpdate();
    }
});

// Check connectivity
if (!db.isConnected()) {
    getLogger().error("Database unavailable!");
}
```

---

## 📡 Events

ProudCore fires the following events on the Bukkit event bus:

### `ProudCoreReadyEvent`

Fired after ProudCore has fully initialised. **This is the recommended entry point** for any plugin that depends on ProudCore.

```java
@EventHandler
public void onProudCoreReady(ProudCoreReadyEvent event) {
    ProudCoreAPI api = event.getApi();
    IModuleRegistry registry = event.getRegistry(); // convenience shortcut

    // Safe to register modules, access managers, etc.
    registry.register(new MyModule());
}
```

### `ProudModuleRegisteredEvent`

Fired when a module is successfully registered and enabled.

```java
@EventHandler
public void onModuleRegistered(ProudModuleRegisteredEvent event) {
    if ("myplugin:arena".equals(event.getModuleId())) {
        IProudModule module = event.getModule();
        getLogger().info("Arena module is now available!");
    }
}
```

### `ProudModuleUnregisteredEvent`

Fired when a module is unregistered and disabled.

```java
@EventHandler
public void onModuleUnregistered(ProudModuleUnregisteredEvent event) {
    if ("myplugin:arena".equals(event.getModuleId())) {
        // Tear down any integrations with the arena module
    }
}
```

---

## 💡 Full Plugin Examples

### Example 1 — Simple Plugin using the API

A straightforward plugin that reads clan and economy data to send players a welcome message and reward them for kills.

**`plugin.yml`:**
```yaml
name: MyPlugin
main: com.example.myplugin.MyPlugin
version: 1.0.0
depend: [ProudCore]
```

**`MyPlugin.java`:**
```java
package com.example.myplugin;

import it.proud.api.ProudCoreAPI;
import it.proud.api.event.ProudCoreReadyEvent;
import it.proud.api.managers.IClanManager;
import it.proud.api.managers.IEconomyManager;
import it.proud.api.managers.IPlayerStatsManager;
import it.proud.api.data.IClan;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class MyPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("MyPlugin loaded. Waiting for ProudCore...");
    }

    @EventHandler
    public void onProudCoreReady(ProudCoreReadyEvent event) {
        getLogger().info("ProudCore is ready! All managers are accessible.");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ProudCoreAPI api = ProudCoreAPI.getOrNull();
        if (api == null) return;

        IClanManager clans = api.getClanManager();
        IClan clan = clans.getPlayerClan(player.getUniqueId());

        if (clan != null) {
            player.sendMessage("§aWelcome back! Your clan §e" + clan.getDisplayName() +
                    "§a has §f" + clan.getClaimsCount() + "§a claimed chunks.");
        } else {
            player.sendMessage("§7You are not in a clan. Use §f/clan create§7 to start one!");
        }

        // Show balance
        IEconomyManager eco = api.getEconomyManager();
        double coins = eco.getBalance(player.getUniqueId(), "coins");
        player.sendMessage("§6Your balance: §f" + eco.getPrimaryCurrency().format(coins));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        if (killer == null) return;

        ProudCoreAPI api = ProudCoreAPI.getOrNull();
        if (api == null) return;

        IEconomyManager eco = api.getEconomyManager();
        IPlayerStatsManager stats = api.getPlayerStatsManager();

        // Reward the killer with 25 coins
        eco.deposit(killer.getUniqueId(), "coins", 25.0, "PvP kill reward");
        killer.sendMessage("§a+25 coins for the kill! Your KDR: §f" +
                String.format("%.2f", stats.getKdr(killer.getUniqueId())));

        // Penalise the victim's power
        api.getClanManager().getPlayerData(victim.getUniqueId()).takePower(3.0);
        victim.sendMessage("§cYou lost 3 power on death.");
    }
}
```

---

### Example 2 — Plugin that registers an External Module

This example shows a full external module with DB persistence and a published service interface.

**`IQuestManager.java`** (public interface, part of your API):
```java
package com.example.questplugin.api;

import java.util.UUID;

public interface IQuestManager {
    void completeQuest(UUID player, String questId);
    int getCompletedCount(UUID player);
}
```

**`QuestModule.java`:**
```java
package com.example.questplugin;

import com.example.questplugin.api.IQuestManager;
import it.proud.api.managers.IDatabaseAccess;
import it.proud.api.managers.IEconomyManager;
import it.proud.api.module.AbstractProudModule;
import it.proud.api.module.IModuleServiceProvider;
import it.proud.api.module.ProudModuleContext;

import java.util.Map;
import java.util.UUID;

public final class QuestModule extends AbstractProudModule implements IModuleServiceProvider {

    private IDatabaseAccess db;
    private IQuestManager questManager;

    public QuestModule() {
        super("questplugin:quests", "Quest Module", "1.0.0");
    }

    @Override
    public void onEnable(ProudModuleContext ctx) {
        super.onEnable(ctx);

        db = ctx.getDatabaseAccess();

        // Create table on first enable
        db.createTableIfNotExists("""
            CREATE TABLE IF NOT EXISTS quest_completions (
                uuid     VARCHAR(36)  NOT NULL,
                quest_id VARCHAR(64)  NOT NULL,
                completed_at BIGINT   NOT NULL,
                PRIMARY KEY (uuid, quest_id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
        """);

        IEconomyManager eco = getApi().getEconomyManager();
        questManager = new QuestManagerImpl(db, eco);

        // Register Bukkit events
        ctx.getCorePlugin().getServer().getPluginManager()
            .registerEvents(new QuestListener(questManager), ctx.getCorePlugin());

        getLogger().info("Quest Module enabled successfully!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Quest Module disabled.");
    }

    @Override
    public void onReload() {
        getLogger().info("Quest Module reloading config...");
        // reload quest definitions from file
    }

    @Override
    public Map<Class<?>, Object> getServices() {
        return Map.of(IQuestManager.class, questManager);
    }

    // Inner implementation
    private static class QuestManagerImpl implements IQuestManager {
        private final IDatabaseAccess db;
        private final IEconomyManager eco;

        QuestManagerImpl(IDatabaseAccess db, IEconomyManager eco) {
            this.db = db;
            this.eco = eco;
        }

        @Override
        public void completeQuest(UUID player, String questId) {
            db.execute(
                "INSERT IGNORE INTO quest_completions (uuid, quest_id, completed_at) VALUES (?, ?, ?)",
                ps -> {
                    ps.setString(1, player.toString());
                    ps.setString(2, questId);
                    ps.setLong(3, System.currentTimeMillis());
                }
            );
            // Reward the player
            eco.deposit(player, "coins", 100.0, "Quest completed: " + questId);
        }

        @Override
        public int getCompletedCount(UUID player) {
            Integer count = db.query(
                "SELECT COUNT(*) FROM quest_completions WHERE uuid = ?",
                ps -> ps.setString(1, player.toString()),
                rs -> rs.next() ? rs.getInt(1) : 0
            );
            return count != null ? count : 0;
        }
    }
}
```

**`QuestPlugin.java`** (main plugin class):
```java
package com.example.questplugin;

import it.proud.api.event.ProudCoreReadyEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class QuestPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onProudCoreReady(ProudCoreReadyEvent event) {
        event.getRegistry().register(new QuestModule());
        getLogger().info("QuestModule registered into ProudCore!");
    }
}
```

---

### Example 3 — Plugin with Custom Scoreboard Templates

Register custom sidebar templates that can be applied to players during events.

**`ArenaScoreboardProvider.java`:**
```java
package com.example.arenaplugin;

import it.proud.api.managers.IScoreboardProvider;

import java.util.List;
import java.util.Map;

public final class ArenaScoreboardProvider implements IScoreboardProvider {

    @Override
    public String getProviderId() {
        return "arenaplugin"; // namespace — all templates become "arenaplugin:xxx"
    }

    @Override
    public Map<String, ScoreboardTemplate> getTemplates() {
        return Map.of(
            "waiting", new ScoreboardTemplate(
                "&e&lARENА &7│ &fWaiting",
                List.of(
                    "&7",
                    "&fPlayers: &e%arena_players%/%arena_max%",
                    "&fMap: &e%arena_map%",
                    "&7",
                    "&fYour Kills: &c%arena_kills%",
                    "&7",
                    "&b&lproudmc.it"
                )
            ),
            "ingame", new ScoreboardTemplate(
                "&c&lARENА &7│ &fIn Game",
                List.of(
                    "&7",
                    "&fKills: &c%arena_kills%",
                    "&fDeaths: &7%arena_deaths%",
                    "&fKDR: &e%arena_kdr%",
                    "&7",
                    "&fTime left: &a%arena_timer%",
                    "&7",
                    "&b&lproudmc.it"
                )
            ),
            "results", new ScoreboardTemplate(
                "&a&lGAME OVER",
                List.of(
                    "&7",
                    "&6Winner: &f%arena_winner%",
                    "&7",
                    "&fYour kills: &c%arena_kills%",
                    "&fCoins earned: &6+%arena_reward%",
                    "&7",
                    "&b&lproudmc.it"
                )
            )
        );
    }
}
```

**`ArenaPlugin.java`:**
```java
package com.example.arenaplugin;

import it.proud.api.ProudCoreAPI;
import it.proud.api.event.ProudCoreReadyEvent;
import it.proud.api.managers.IScoreboardManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class ArenaPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onProudCoreReady(ProudCoreReadyEvent event) {
        // Register all our scoreboard templates
        event.getApi().getScoreboardRegistry()
            .registerProvider(new ArenaScoreboardProvider());

        getLogger().info("Arena scoreboard templates registered!");
    }

    // Call these from your arena logic:

    public void onPlayerJoinArenaQueue(Player player) {
        IScoreboardManager sb = ProudCoreAPI.get().getScoreboardManager();
        sb.setExternalScoreboard(player, "arenaplugin:waiting");
    }

    public void onArenaStart(Player player) {
        IScoreboardManager sb = ProudCoreAPI.get().getScoreboardManager();
        sb.setExternalScoreboard(player, "arenaplugin:ingame");
    }

    public void onArenaEnd(Player player) {
        IScoreboardManager sb = ProudCoreAPI.get().getScoreboardManager();
        sb.setExternalScoreboard(player, "arenaplugin:results");
    }

    public void onPlayerLeaveArena(Player player) {
        IScoreboardManager sb = ProudCoreAPI.get().getScoreboardManager();
        sb.setMainScoreboard(player); // restore lobby scoreboard
    }
}
```

---

## 📐 Data Model Reference

### `IClan`
| Method | Returns | Description |
|---|---|---|
| `getName()` | `String` | Internal name (primary key, immutable) |
| `getDisplayName()` | `String` | Color-formatted display name |
| `getDescription()` | `String` | Clan motto / lore |
| `getLeader()` | `UUID` | Leader's UUID |
| `getMembers()` | `Set<UUID>` | All members including leader (unmodifiable) |
| `isMember(UUID)` | `boolean` | True for any member including leader |
| `isLeader(UUID)` | `boolean` | True only for the leader |
| `getTotalPower()` | `double` | Current aggregate power |
| `getTotalMaxPower()` | `double` | Maximum aggregate power |
| `getClaimsCount()` | `int` | Number of claimed chunks |
| `getHome()` | `Location?` | Teleport anchor, or null |

### `IPlayerData`
| Method | Returns | Description |
|---|---|---|
| `getUuid()` | `UUID` | Player UUID |
| `getName()` | `String` | Last-known username |
| `getPower()` | `double` | Current power `[0, maxPower]` |
| `getMaxPower()` | `double` | Power cap |
| `getChunkLimit()` | `int` | Personal claim limit |
| `setPower(double)` | `void` | Set power (clamped) |
| `setMaxPower(double)` | `void` | Set max power |
| `setChunkLimit(int)` | `void` | Set chunk limit |
| `addPower(double)` | `void` | Add power (capped at max) |
| `takePower(double)` | `void` | Remove power (floored at 0) |

### `IPlayerStats`
| Method | Returns | Description |
|---|---|---|
| `getKills()` | `long` | PvP kills |
| `getDeaths()` | `long` | Total deaths |
| `getMobKills()` | `long` | Mob kills |
| `getKdr()` | `double` | K/D ratio |
| `getWalkOneCm()` | `long` | Distance walked (cm) |
| `getPlayTimeTicks()` | `long` | Playtime in ticks |
| `getPlayTimeSeconds()` | `long` | Convenience: `/20` |
| `getPlayTimeMinutes()` | `long` | Convenience: `/60` |
| `getItemsCrafted()` | `long` | Total items crafted |
| `getJumps()` | `long` | Total jumps |
| `getLastUpdated()` | `long` | Snapshot unix-millis |

### `ITransaction`
| Method | Returns | Description |
|---|---|---|
| `id()` | `long` | Auto-increment ID |
| `playerUuid()` | `UUID` | Player |
| `currencyId()` | `String` | Currency |
| `type()` | `Type` | `DEPOSIT`, `WITHDRAW`, `TRANSFER_SENT`, etc. |
| `amount()` | `double` | Transaction amount (always positive) |
| `balanceAfter()` | `double` | Balance after this transaction |
| `reason()` | `String?` | Human-readable reason |
| `timestamp()` | `long` | Unix-millis |

---

## ✅ Best Practices

- **Always listen for `ProudCoreReadyEvent`** before accessing any manager. Never call `ProudCoreAPI.get()` in `onEnable()` without checking if ProudCore is already loaded.
- **Never shade `proudcore-api`** into your plugin JAR. Mark it as `compileOnly` (Gradle) or `provided` (Maven).
- **Use `getOrNull()`** when ProudCore availability is uncertain, to avoid `IllegalStateException`.
- **Prefer targeted lookups over bulk iteration** (e.g. `getPlayerClan(uuid)` vs iterating `getAllClans()`).
- **Guard offline player reads** with `isLoaded(uuid)` before calling `getPlayer(uuid)` in performance-critical loops.
- **Name your DB tables** with a plugin-specific prefix (e.g. `myplugin_data`) to avoid collisions with ProudCore tables.
- **Call `createTableIfNotExists()`** in `onEnable` before any query runs.
- **Always call `super.onEnable(ctx)` first** inside `AbstractProudModule` subclasses.
- **Namespace your scoreboard provider ID** to avoid template key collisions with other plugins.
- **Never call `saveAll()`** on the main thread in hot code paths — it may block for I/O.

---

<div align="center">

Made with ❤️ by the **ProudCore Team**

[![](https://jitpack.io/v/ProudMC-IT/ProudCore-API.svg)](https://jitpack.io/#ProudMC-IT/ProudCore-API)

</div>