<div align="center">

# 🏰 ProudCore API

### The official public API for the **ProudCore** Minecraft plugin ecosystem

[![](https://jitpack.io/v/ProudMC-IT/ProudCore-API.svg)](https://jitpack.io/#ProudMC-IT/ProudCore-API)
![Java](https://img.shields.io/badge/Java-21+-orange?logo=openjdk)
![Minecraft](https://img.shields.io/badge/Minecraft-1.20%2B-green?logo=minecraft)
![License](https://img.shields.io/badge/License-Proprietary-red)
![Build](https://img.shields.io/badge/Build-JitPack-blue?logo=github)

> Integrate your plugins seamlessly with ProudCore's clan system, economy, scoreboards, player stats, homes/warps, teleport requests, vanish/god mode, schematics and much more — all through a clean, well-documented API.

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
    - [IHomeManager](#ihomemanager)
    - [IWarpManager](#iwarpmanager)
    - [ISpawnManager](#ispawnmanager)
    - [ITpaManager](#itpamanager)
    - [IVanishManager](#ivanishmanager)
    - [IGodManager](#igodmanager)
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
| 🏠 **Homes** | Create, delete, and teleport to per-player named homes |
| 🌀 **Warps** | Create and manage global warp points |
| 📍 **Spawn** | Read and set the server-wide spawn location |
| ✨ **TPA** | Manage `/tpa` and `/tpahere` requests with expiry |
| 👻 **Vanish** | Hide/reveal players from others (in-memory state) |
| 🛡️ **God Mode** | Toggle per-player invulnerability (in-memory state) |
| 🔔 **Notifications** | Send typed notifications to players without knowing the display channel (chat, actionbar, title, bossbar, custom) |
| 📸 **Snapshots** | Save and restore complete player state: inventory, location, health, economy, gamemode, potion effects, flight |
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
├── IHomeManager          → Per-player named homes
├── IWarpManager          → Global warp registry
├── ISpawnManager         → Server-wide spawn point
├── ITpaManager           → Teleport request sessions
├── IVanishManager        → Per-player vanish state
├── IGodManager           → Per-player god mode
├── INotificationService      → Typed notification delivery across configurable channels
├── ISnapshotManager          → Complete player state save & restore (per-module ownership)
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

// Cache control
boolean cached = statsManager.isCached(uuid);
statsManager.evict(uuid);
Map<UUID, ? extends IPlayerStats> allCached = statsManager.getAllCached();

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
coins.decimalPlaces();   // e.g. 0 or 2
coins.vaultPrimary();    // true if Vault-bridged
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

// Refresh a provider whose templates changed at runtime
// Use this instead of re-registering when your provider adds/removes templates dynamically
registry.refreshProvider("mymod");

// Unregister
registry.unregisterProvider("mymod");

// Reload core templates (external providers unaffected)
registry.reloadCoreTemplates();
```

---

### IHomeManager

Manage per-player named homes (persisted automatically).

```java
IHomeManager homes = ProudCoreAPI.get().getHomeManager();

// Set or overwrite a home
homes.setHome(player.getUniqueId(), "base", player.getLocation());

// Teleport to a home
Location home = homes.getHome(player.getUniqueId(), "base");
if (home != null) player.teleport(home);

// List and delete
Map<String, Location> all = homes.getHomes(player.getUniqueId());
int count = homes.getHomeCount(player.getUniqueId());
boolean removed = homes.deleteHome(player.getUniqueId(), "base");
```

---

### IWarpManager

Manage global warp points (shared across all players, persisted automatically).

```java
IWarpManager warps = ProudCoreAPI.get().getWarpManager();

warps.setWarp("market", player.getLocation());
Location market = warps.getWarp("market");

boolean exists = warps.hasWarp("market");
int total = warps.getWarpCount();

warps.deleteWarp("oldbase");
```

---

### ISpawnManager

Access and update the server-wide spawn location.

```java
ISpawnManager spawn = ProudCoreAPI.get().getSpawnManager();

Location loc = spawn.getSpawn();
if (loc != null) player.teleport(loc);

spawn.setSpawn(player.getLocation());
boolean configured = spawn.isSpawnSet();
```

---

### ITpaManager

Create and resolve `/tpa` and `/tpahere` requests (in-memory, time-limited).

```java
ITpaManager tpa = ProudCoreAPI.get().getTpaManager();

// Create a request that expires after 60 seconds
tpa.createRequest(sender.getUniqueId(), target.getUniqueId(),
        ITpaManager.RequestType.TO_TARGET, Duration.ofSeconds(60));

// Accept: fetch pending request for target
ITpaManager.TpaRequest req = tpa.getForTarget(target.getUniqueId());
if (req != null && !req.isExpired()) {
    tpa.clearRequest(req.from(), req.to());
    // perform teleport...
}

// Cancel by sender or target
tpa.clearBySender(sender.getUniqueId());
tpa.clearByTarget(target.getUniqueId());
```

---

### IVanishManager

Hide or reveal players from others (not persisted across restarts).

```java
IVanishManager vanish = ProudCoreAPI.get().getVanishManager();

boolean nowVanished = vanish.toggle(player);
boolean isHidden = vanish.isVanished(player);

// Ensure new joiners cannot see vanished players
@EventHandler
public void onJoin(PlayerJoinEvent event) {
    vanish.handleJoin(event.getPlayer());
}

// Clean up on quit
vanish.clear(player);
```

---

### IGodManager

Toggle per-player invulnerability (not persisted across restarts).

```java
IGodManager god = ProudCoreAPI.get().getGodManager();

boolean nowGod = god.toggle(player);
boolean isGod = god.isGod(player);

// Clean up on quit
god.clear(player);
```

---

### INotificationService

Sistema di notifiche tipizzate. I moduli inviano notifiche senza sapere come verranno mostrate — il server owner configura i canali in `notifications/config.yml`.

**Canali built-in:** `chat`, `actionbar`, `title`, `bossbar`
**Canali custom:** registrabili da moduli esterni (es. Discord webhook)
```java
INotificationService notify = ProudCoreAPI.get().getNotificationService();

// Notifica singolo player
notify.send(player, Notification.builder()
    .type("economy.deposit")
    .title("&a+{amount} {currency}")
    .body("Il tuo saldo è ora &e{balance}")
    .placeholder("amount",   "500")
    .placeholder("currency", "Monete")
    .placeholder("balance",  "1500")
    .build());

// Notifica tramite UUID (player può essere offline — viene scartata silenziosamente)
notify.send(uuid, Notification.builder()
    .type("arena.eliminated")
    .body("&cSei stato eliminato!")
    .build());

// Broadcast a tutti i player online
notify.broadcast(Notification.builder()
    .type("server.announcement")
    .body("&6[Annuncio] &fEvento iniziato!")
    .build());

// Notifica a tutti i membri del clan online
notify.sendToClan("spartans", Notification.builder()
    .type("clan.alert")
    .body("&cIl vostro territorio è sotto attacco!")
    .build());

// Notifica a una lista specifica di player
notify.sendToAll(List.of(player1, player2), Notification.builder()
    .type("arena.countdown")
    .body("&eLa partita inizia tra &f{seconds}&e secondi!")
    .placeholder("seconds", "10")
    .build());

// Registra un canale custom (es. Discord)
notify.registerChannel(new NotificationChannel() {
    public String getId() { return "discord"; }
    public void deliver(Player player, Notification n) {
        // invia a Discord webhook
    }
});

// Verifica se un canale è registrato
boolean hasActionbar = notify.isChannelRegistered("actionbar"); // true
boolean hasDiscord   = notify.isChannelRegistered("discord");   // dipende

// Shortcut per notifiche semplici senza builder
Notification simple = Notification.simple("generic", "&7Messaggio rapido");
notify.send(player, simple);
```

> **Nota:** I placeholder vengono risolti automaticamente sia nel `title` che nel `body`. Il tipo (`type`) è usato dal sistema di routing per scegliere il canale di consegna — configurabile in `notifications/config.yml`.

---

### ISnapshotManager

Salva e ripristina lo stato completo (o parziale) di un player. Ogni snapshot è associato al modulo che lo ha creato — i moduli non vedono gli snapshot degli altri.

#### SnapshotPart — Parti selezionabili

| Valore | Cosa include |
|---|---|
| `INVENTORY` | 36 slot inventario principale |
| `ARMOR` | 4 slot armatura |
| `OFFHAND` | Slot mano secondaria |
| `ENDER_CHEST` | 27 slot ender chest |
| `LOCATION` | Mondo, coordinate, yaw, pitch |
| `HEALTH` | Max health e health corrente |
| `FOOD` | Food level e saturation |
| `EXPERIENCE` | XP level, progress e total XP |
| `GAME_MODE` | GameMode corrente |
| `POTION_EFFECTS` | Tutti gli effetti pozione attivi |
| `FLIGHT` | Flag allow-flight e is-flying |
| `ECONOMY` | Tutti i saldi da IEconomyManager |
| `ALL` | Tutto quanto sopra |
```java
ISnapshotManager snapshots = ProudCoreAPI.get().getSnapshotManager();

// ── SAVE ────────────────────────────────────────────────────────────────

// Salva TUTTO lo stato del player
PlayerSnapshot snap = snapshots.save(player, "pre_arena", myModule);

// Salva solo le parti che ti servono
PlayerSnapshot partial = snapshots.save(player, "inv_only", myModule,
    SnapshotPart.INVENTORY, SnapshotPart.ARMOR, SnapshotPart.OFFHAND);

// ── RESTORE ─────────────────────────────────────────────────────────────

// Ripristina TUTTO
snapshots.restore(player, snap);

// Ripristina solo alcune parti (ignora il resto)
snapshots.restore(player, snap, SnapshotPart.INVENTORY, SnapshotPart.LOCATION);

// Puoi anche ripristinare su un player diverso dall'originale (clone loadout)
snapshots.restore(otherPlayer, snap);

// ── QUERY ────────────────────────────────────────────────────────────────

// Snapshot più recente per player + modulo
Optional<PlayerSnapshot> latest = snapshots.getLatest(player.getUniqueId(), myModule);
latest.ifPresent(s -> {
    System.out.println("ID:      " + s.getId());
    System.out.println("Label:   " + s.getLabel());       // "pre_arena"
    System.out.println("Owner:   " + s.getOwnerId());
    System.out.println("Created: " + s.getCreatedAt());   // java.time.Instant
    System.out.println("Parts:   " + s.getCapturedParts());
});

// Tutti gli snapshot del player per questo modulo (più recente prima)
List<PlayerSnapshot> all = snapshots.getAll(player.getUniqueId(), myModule);

// Per ID specifico (indipendente dal modulo)
Optional<PlayerSnapshot> byId = snapshots.getById("550e8400-e29b-...");

// ── DELETE ───────────────────────────────────────────────────────────────

// Elimina un singolo snapshot
boolean deleted = snapshots.delete(snap.getId());

// Elimina tutti gli snapshot del player per questo modulo
int count = snapshots.deleteAll(player.getUniqueId(), myModule);

// ── ISPEZIONE PlayerSnapshot ─────────────────────────────────────────────

PlayerSnapshot s = latest.get();

// Controlla se una parte è stata catturata
boolean hasInv  = s.hasPart(SnapshotPart.INVENTORY);  // true se salvata
boolean hasEco  = s.hasPart(SnapshotPart.ECONOMY);

// Item data (slot → Base64 ItemStack)
Map<Integer, String> invData = s.getItemData(SnapshotPart.INVENTORY);

// Location
PlayerSnapshot.LocationData loc = s.getLocation(); // null se LOCATION non catturata
if (loc != null) {
    System.out.printf("%.2f %.2f %.2f in %s%n", loc.x(), loc.y(), loc.z(), loc.world());
}

// Stato (health, food, xp, gamemode, flight, pozioni)
PlayerSnapshot.PlayerStateData state = s.getState(); // null se nessuno stato catturato
if (state != null) {
    System.out.println("Health: " + state.health() + "/" + state.maxHealth());
    System.out.println("GameMode: " + state.gameMode());
    System.out.println("Pozioni: " + state.potionEffects()); // List<String> "TYPE:amp:dur"
}

// Saldi economia al momento del salvataggio
Map<String, Double> balances = s.getEconomyBalances(); // currencyId → amount
```

> **Ownership:** Ogni snapshot appartiene al modulo che lo ha creato. `getLatest()` e `getAll()` filtrano per modulo. Usa `getById()` solo se hai l'ID esatto e vuoi aggirare il filtro.

> **Partial restore:** Puoi salvare `ALL` e ripristinare solo `INVENTORY` + `LOCATION` — le parti non specificate vengono ignorate.

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
        INotificationService notify   = api.getNotificationService();
        ISnapshotManager     snapshots = api.getSnapshotManager();
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
| `getUuid()` | `UUID` | Player UUID |
| `getName()` | `String` | Last-known username |
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

### `ICurrency`
| Method | Returns | Description |
|---|---|---|
| `id()` | `String` | Currency id (lowercase) |
| `nameSingular()` | `String` | Singular display name |
| `namePlural()` | `String` | Plural display name |
| `symbol()` | `String` | Display symbol |
| `startingBalance()` | `double` | Balance on first join |
| `maxBalance()` | `double` | Max balance or `-1` for unlimited |
| `decimalPlaces()` | `int` | Decimal places used for formatting |
| `vaultPrimary()` | `boolean` | True if Vault-bridged currency |
| `format(double)` | `String` | Formats an amount for display |

### `ITransaction`
| Method | Returns | Description |
|---|---|---|
| `id()` | `long` | Auto-increment ID |
| `playerUuid()` | `UUID` | Player |
| `currencyId()` | `String` | Currency |
| `type()` | `Type` | `DEPOSIT`, `WITHDRAW`, `TRANSFER_SENT`, `TRANSFER_RECEIVED`, `ADMIN_SET`, `CLAN_DEPOSIT`, `CLAN_WITHDRAW` |
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
- **Use `refreshProvider()` for dynamic templates** — if your provider adds or removes templates at runtime (e.g. driven by config), call `registry.refreshProvider(providerId)` instead of re-registering the entire provider. This avoids the "already registered" warning and is semantically cleaner.
- **Never call `saveAll()`** on the main thread in hot code paths — it may block for I/O.
- **Vanish, god mode, and TPA requests are in-memory**: clear them on quit and re-apply vanish visibility with `IVanishManager.handleJoin(...)`.
- **Chiama sempre `restore` in `onDisable()`** — se il server si spegne mentre un player è in arena, il modulo deve ripristinarlo in `onDisable` altrimenti si ritrova con l'inventario arena al riavvio.
- **Usa `SnapshotPart` selettivi** quando il tuo modulo non tocca alcune parti dello stato (es. un'arena che non modifica l'economia non ha bisogno di salvare `ECONOMY`). Risparmia spazio in DB e tempo di serializzazione.
- **Non condividere snapshot tra moduli** — ogni snapshot è owned da un modulo specifico. Se hai bisogno di passare uno snapshot a un altro modulo, usa `getById()` con l'ID che ti sei salvato.
- **Il tipo di notifica (`type`) è il routing key** — sceglilo con un namespace per evitare collisioni: `"arena.join"`, `"economy.deposit"`, non `"join"` o `"deposit"`.
- **I canali di notifica sono configurabili dal server owner** — non assumere che il tuo tipo venga mostrato come title o actionbar. Il fallback è sempre `chat`. Se vuoi forzare un canale specifico per test, usa `registerChannel` con un canale dedicato al tuo modulo.

---

<div align="center">

Made with ❤️ by the **ProudCore Team**

[![](https://jitpack.io/v/ProudMC-IT/ProudCore-API.svg)](https://jitpack.io/#ProudMC-IT/ProudCore-API)

</div>
