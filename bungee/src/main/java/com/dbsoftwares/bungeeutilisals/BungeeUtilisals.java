/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals;

import com.dbsoftwares.bungeeutilisals.announcers.ActionBarAnnouncer;
import com.dbsoftwares.bungeeutilisals.announcers.BossBarAnnouncer;
import com.dbsoftwares.bungeeutilisals.announcers.ChatAnnouncer;
import com.dbsoftwares.bungeeutilisals.announcers.TitleAnnouncer;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.announcer.Announcer;
import com.dbsoftwares.bungeeutilisals.api.command.BUCommand;
import com.dbsoftwares.bungeeutilisals.api.data.StaffUser;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventHandler;
import com.dbsoftwares.bungeeutilisals.api.event.event.IEventLoader;
import com.dbsoftwares.bungeeutilisals.api.event.events.network.NetworkStaffJoinEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.network.NetworkStaffLeaveEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.punishment.UserPunishmentFinishEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserChatEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserCommandEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserLoadEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserUnloadEvent;
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.storage.AbstractStorageManager;
import com.dbsoftwares.bungeeutilisals.api.storage.AbstractStorageManager.StorageType;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.api.utils.reflection.JarClassLoader;
import com.dbsoftwares.bungeeutilisals.api.utils.reflection.ReflectionUtils;
import com.dbsoftwares.bungeeutilisals.commands.addons.AddonCommand;
import com.dbsoftwares.bungeeutilisals.commands.friends.FriendsCommand;
import com.dbsoftwares.bungeeutilisals.commands.general.*;
import com.dbsoftwares.bungeeutilisals.commands.general.message.IgnoreCommand;
import com.dbsoftwares.bungeeutilisals.commands.general.message.MsgCommand;
import com.dbsoftwares.bungeeutilisals.commands.general.message.ReplyCommand;
import com.dbsoftwares.bungeeutilisals.commands.plugin.PluginCommand;
import com.dbsoftwares.bungeeutilisals.commands.punishments.*;
import com.dbsoftwares.bungeeutilisals.commands.punishments.removal.UnbanCommand;
import com.dbsoftwares.bungeeutilisals.commands.punishments.removal.UnbanIPCommand;
import com.dbsoftwares.bungeeutilisals.commands.punishments.removal.UnmuteCommand;
import com.dbsoftwares.bungeeutilisals.commands.punishments.removal.UnmuteIPCommand;
import com.dbsoftwares.bungeeutilisals.executors.*;
import com.dbsoftwares.bungeeutilisals.library.Library;
import com.dbsoftwares.bungeeutilisals.library.StandardLibrary;
import com.dbsoftwares.bungeeutilisals.listeners.MotdPingListener;
import com.dbsoftwares.bungeeutilisals.listeners.PunishmentListener;
import com.dbsoftwares.bungeeutilisals.listeners.UserChatListener;
import com.dbsoftwares.bungeeutilisals.listeners.UserConnectionListener;
import com.dbsoftwares.bungeeutilisals.manager.CommandManager;
import com.dbsoftwares.bungeeutilisals.packet.PacketRegistry;
import com.dbsoftwares.bungeeutilisals.packet.event.PacketReceiveEvent;
import com.dbsoftwares.bungeeutilisals.packet.event.PacketUpdateEvent;
import com.dbsoftwares.bungeeutilisals.packet.executors.PacketUpdateExecutor;
import com.dbsoftwares.bungeeutilisals.packet.listeners.SimplePacketListener;
import com.dbsoftwares.bungeeutilisals.placeholders.DefaultPlaceHolders;
import com.dbsoftwares.bungeeutilisals.placeholders.InputPlaceHolders;
import com.dbsoftwares.bungeeutilisals.placeholders.javascript.JavaScriptPlaceHolder;
import com.dbsoftwares.bungeeutilisals.placeholders.javascript.Script;
import com.dbsoftwares.bungeeutilisals.redis.RedisMessenger;
import com.dbsoftwares.bungeeutilisals.redis.handlers.*;
import com.dbsoftwares.bungeeutilisals.runnables.TPSRunnable;
import com.dbsoftwares.bungeeutilisals.runnables.UserMessageQueueRunnable;
import com.dbsoftwares.bungeeutilisals.updater.Updatable;
import com.dbsoftwares.bungeeutilisals.updater.Updater;
import com.dbsoftwares.bungeeutilisals.utils.MessageBuilder;
import com.dbsoftwares.configuration.api.FileStorageType;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;

import javax.script.ScriptException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Updatable(url = "https://api.dbsoftwares.eu/plugin/BungeeUtilisals/")
public class BungeeUtilisals extends Plugin {

    private static final String ERROR_STRING = "An error occured: ";
    private static final String ENABLED_CONFIG_KEY = "enabled";

    @Getter
    private static BungeeUtilisals instance;

    @Getter
    private static BUtilisalsAPI api;

    @Getter
    private JarClassLoader jarClassLoader;

    @Getter
    private AbstractStorageManager databaseManagement;

    @Getter
    private List<BUCommand> generalCommands = Lists.newArrayList();

    @Getter
    private List<BUCommand> customCommands = Lists.newArrayList();

    @Getter
    private RedisMessenger redisMessenger;

    @Getter
    private CommandManager commandManager;

    @Getter
    private List<Script> scripts = Lists.newArrayList();

    @Getter
    private List<StaffUser> staffMembers = Lists.newArrayList();

    @Override
    public void onEnable() {
        if (ReflectionUtils.getJavaVersion() < 8) {
            BUCore.getLogger().warn("You are running a Java version lower then Java 8.");
            BUCore.getLogger().warn("Please upgrade to Java 8 or newer.");
            BUCore.getLogger().warn("BungeeUtilisalsX is not able to start up on Java versions lower then Java 8.");
            return;
        }
        // Setting instance
        instance = this;

        // Creating datafolder if not exists.
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // Loading setting files ...
        createAndLoadFiles();

        // Loading default PlaceHolders. Must be done BEFORE API / database loads.
        PlaceHolderAPI.loadPlaceHolderPack(new DefaultPlaceHolders());
        PlaceHolderAPI.loadPlaceHolderPack(new InputPlaceHolders());
        new JavaScriptPlaceHolder().register();
        loadScripts();

        // Loading libraries
        loadLibraries();

        // Loading database
        loadDatabase();

        // Initializing API
        api = new BUtilisalsAPI(this);

        // Loading language chat
        api.getLanguageManager().addPlugin(getDescription().getName(), new File(getDataFolder(), "languages"), FileStorageType.YAML);
        api.getLanguageManager().loadLanguages(getDescription().getName());

        // Loading & enabling addons
        if (getConfig().getBoolean("addons")) {
            api.getAddonManager().findAddons(api.getAddonManager().getAddonsFolder());
            api.getAddonManager().loadAddons();
            api.getAddonManager().enableAddons();
        }

        // Initialize metric system
        new Metrics(this);

        if (getConfig().getBoolean("redis")) {
            registerRedisMessenger();
        }

        // Register executors & listeners
        ProxyServer.getInstance().getPluginManager().registerListener(this, new UserConnectionListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new UserChatListener());

        if (FileLocation.MOTD.getConfiguration().getBoolean(ENABLED_CONFIG_KEY)) {
            ProxyServer.getInstance().getPluginManager().registerListener(this, new MotdPingListener());
        }

        final IEventLoader loader = api.getEventLoader();

        final UserExecutor userExecutor = new UserExecutor();
        loader.register(UserLoadEvent.class, userExecutor);
        loader.register(UserUnloadEvent.class, userExecutor);
        loader.register(UserChatEvent.class, new UserChatExecutor());
        loader.register(UserChatEvent.class, new StaffCharChatExecutor());

        final StaffNetworkExecutor staffNetworkExecutor = new StaffNetworkExecutor();
        loader.register(NetworkStaffJoinEvent.class, staffNetworkExecutor);
        loader.register(NetworkStaffLeaveEvent.class, staffNetworkExecutor);

        // Loading Punishment system
        if (FileLocation.PUNISHMENTS.getConfiguration().getBoolean(ENABLED_CONFIG_KEY)) {
            ProxyServer.getInstance().getPluginManager().registerListener(this, new PunishmentListener());

            loader.register(UserPunishmentFinishEvent.class, new UserPunishExecutor());

            MuteCheckExecutor muteCheckExecutor = new MuteCheckExecutor();
            loader.register(UserChatEvent.class, muteCheckExecutor);
            loader.register(UserCommandEvent.class, muteCheckExecutor);
        }

        if (FileLocation.FRIENDS_CONFIG.getConfiguration().getBoolean(ENABLED_CONFIG_KEY)) {
            loader.register(UserLoadEvent.class, new FriendsExecutor());
        }

        // Loading Announcers
        Announcer.registerAnnouncers(ActionBarAnnouncer.class, ChatAnnouncer.class, TitleAnnouncer.class, BossBarAnnouncer.class);

        // Loading all (enabled) Commands
        commandManager = new CommandManager();
        loadCommands();

        // Loading packet system (if enabled)
        if (getConfig().getBoolean("packets")) {
            PacketRegistry.registerPackets();
            ProxyServer.getInstance().getPluginManager().registerListener(this, new SimplePacketListener());

            PacketUpdateExecutor packetUpdateExecutor = new PacketUpdateExecutor();
            loader.register(PacketUpdateEvent.class, packetUpdateExecutor);
            loader.register(PacketReceiveEvent.class, packetUpdateExecutor);
        }

        ProxyServer.getInstance().getScheduler().schedule(this, new TPSRunnable(), 50, TimeUnit.MILLISECONDS);
        ProxyServer.getInstance().getScheduler().schedule(this, new UserMessageQueueRunnable(), 1, TimeUnit.MINUTES);

        if (getConfig().getBoolean("updater.enabled")) {
            Updater.initialize(this);
        }

        // Some more random stuff
        sendBuyerMessage();
    }

    @Override
    public void onDisable() {
        if (getConfig().getBoolean("addons")) {
            api.getAddonManager().disableAddons();
        }
        BUCore.getApi().getUsers().forEach(User::unload);
        try {
            databaseManagement.close();
        } catch (SQLException e) {
            BUCore.getLogger().error(ERROR_STRING, e);
        }

        scripts.forEach(Script::unload);
        api.getEventLoader().getHandlers().forEach(EventHandler::unregister);
        Updater.shutdownUpdaters();
    }

    public void sendBuyerMessage() {
        final String userid = "%%USER_ID%%";
        String username = "Unknown User";

        // retrieving username ...
        if (!userid.equals("%%__USER__%")) {
            try {
                URL url = new URL("https://www.spigotmc.org/members/" + userid);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0");

                try (InputStream inputStream = connection.getInputStream();
                     InputStreamReader isr = new InputStreamReader(inputStream);
                     BufferedReader br = new BufferedReader(isr)) {

                    String line;
                    final StringBuilder builder = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        builder.append(line);
                    }
                    username = builder.toString().split("<title>")[1].split("</title>")[0].split(" | ")[0];
                }
            } catch (IOException e) {
                // do nothing
            }
        }

        if (!username.equals("Unknown User")) {
            api.getConsole().sendLangMessage(false, "general.startup-message", "{buyer_name}", username);
        }
    }

    public void reload() {
        loadCommands();

        Announcer.getAnnouncers().values().forEach(Announcer::reload);

        for (Language language : BUCore.getApi().getLanguageManager().getLanguages()) {
            BUCore.getApi().getLanguageManager().reloadConfig(getDescription().getName(), language);
        }

        loadScripts();
        api.getChatManager().reload();
    }

    private void registerRedisMessenger() {
        redisMessenger = new RedisMessenger();
        ProxyServer.getInstance().getPluginManager().registerListener(this, redisMessenger);

        redisMessenger.registerMessageHandlers(
                AnnounceMessageHandler.class, BroadcastMessageHandler.class,
                ChatLockMessageHandler.class, ClearChatMessageHandler.class,
                StaffChatMessageHandler.class, StaffRedisMessageHandler.class,
                MsgMessageHandler.class
        );
    }

    private void loadScripts() {
        scripts.forEach(Script::unload);
        scripts.clear();
        final File scriptsFolder = new File(getDataFolder(), "scripts");

        if (!scriptsFolder.exists()) {
            scriptsFolder.mkdir();

            IConfiguration.createDefaultFile(
                    getResourceAsStream("scripts/hello.js"), new File(scriptsFolder, "hello.js")
            );
            IConfiguration.createDefaultFile(
                    getResourceAsStream("scripts/coins.js"), new File(scriptsFolder, "coins.js")
            );
        }

        for (final File file : scriptsFolder.listFiles()) {
            if (file.isDirectory()) {
                continue;
            }
            try {
                final String code = new String(Files.readAllBytes(file.toPath()));
                final Script script = new Script(file.getName(), code);

                this.scripts.add(script);
            } catch (IOException | ScriptException e) {
                BUCore.getLogger().error("Could not load script " + file.getName(), e);
            }
        }
    }

    private void loadDatabase() {
        StorageType type;
        try {
            type = StorageType.valueOf(getConfig().getString("storage.type").toUpperCase());
        } catch (IllegalArgumentException e) {
            type = StorageType.MYSQL;
        }
        try {
            databaseManagement = type.getManager().getConstructor(Plugin.class).newInstance(this);
            databaseManagement.initialize();
        } catch (Exception e) {
            BUCore.getLogger().error(ERROR_STRING, e);
        }
    }

    private void loadLibraries() {
        BUCore.getLogger().info("Loading libraries ...");
        jarClassLoader = new JarClassLoader(this);

        for (StandardLibrary standardLibrary : StandardLibrary.values()) {
            final Library library = standardLibrary.getLibrary();

            if (library.isToLoad() && !library.isPresent()) {
                library.load();
            }
        }
        BUCore.getLogger().info("Libraries have been loaded.");
    }

    public IConfiguration getConfig() {
        return FileLocation.CONFIG.getConfiguration();
    }

    private void createAndLoadFiles() {
        for (FileLocation location : FileLocation.values()) {
            File file = new File(getDataFolder(), location.getPath());

            if (!file.exists()) {
                IConfiguration.createDefaultFile(getResourceAsStream(location.getPath()), file);

                location.loadConfiguration(file);
            } else {
                // update configurations ...

                location.loadConfiguration(file);
                try {
                    location.getConfiguration().copyDefaults(
                            IConfiguration.loadYamlConfiguration(getResourceAsStream(location.getPath()))
                    );
                } catch (IOException e) {
                    BUCore.getLogger().error("Could not update configurations: ", e);
                }
            }
            location.loadData();
        }
    }

    private void loadCommands() {
        commandManager.load();

        loadGeneralCommands();

        if (FileLocation.PUNISHMENTS.getConfiguration().getBoolean(ENABLED_CONFIG_KEY)) {
            loadPunishmentCommands();
        }

        loadCustomCommands();
    }

    private void loadGeneralCommands() {
        generalCommands.forEach(BUCommand::unload);
        generalCommands.clear();

        generalCommands.add(new PluginCommand());
        loadCommand("addons", getConfig(), AddonCommand.class);

        loadGeneralCommand("glist", GListCommand.class);
        loadGeneralCommand("announce", AnnounceCommand.class);
        loadGeneralCommand("find", FindCommand.class);
        loadGeneralCommand("server", ServerCommand.class);
        loadGeneralCommand("clearchat", ClearChatCommand.class);
        loadGeneralCommand("chatlock", ChatLockCommand.class);
        loadGeneralCommand("glag", GLagCommand.class);
        loadGeneralCommand("staffchat", StaffChatCommand.class);
        loadGeneralCommand("language", LanguageCommand.class);
        loadGeneralCommand("staff", StaffCommand.class);
        loadGeneralCommand("msg", MsgCommand.class);
        loadGeneralCommand("reply", ReplyCommand.class);
        loadGeneralCommand("ignore", IgnoreCommand.class);
        loadGeneralCommand("ping", PingCommand.class);

        if (FileLocation.FRIENDS_CONFIG.getConfiguration().getBoolean(ENABLED_CONFIG_KEY)) {
            generalCommands.add(new FriendsCommand());
        }
    }

    private void loadPunishmentCommands() {
        loadPunishmentCommand("ban", BanCommand.class);
        loadPunishmentCommand("ipban", IPBanCommand.class);
        loadPunishmentCommand("tempban", TempBanCommand.class);
        loadPunishmentCommand("iptempban", IPTempBanCommand.class);
        loadPunishmentCommand("mute", MuteCommand.class);
        loadPunishmentCommand("ipmute", IPMuteCommand.class);
        loadPunishmentCommand("tempmute", TempMuteCommand.class);
        loadPunishmentCommand("iptempmute", IPTempMuteCommand.class);

        loadPunishmentCommand("kick", KickCommand.class);
        loadPunishmentCommand("warn", WarnCommand.class);

        loadPunishmentCommand("unban", UnbanCommand.class);
        loadPunishmentCommand("unbanip", UnbanIPCommand.class);
        loadPunishmentCommand("unmute", UnmuteCommand.class);
        loadPunishmentCommand("unmuteip", UnmuteIPCommand.class);

        loadPunishmentCommand("punishmentinfo", PunishmentInfoCommand.class);
        loadPunishmentCommand("punishmenthistory", PunishmentHistoryCommand.class);
        loadPunishmentCommand("punishmentdata", PunishmentDataCommand.class);
        loadPunishmentCommand("checkip", CheckIpCommand.class);
    }

    private void loadCustomCommands() {
        customCommands.forEach(BUCommand::unload);
        customCommands.clear();

        IConfiguration config = FileLocation.CUSTOMCOMMANDS.getConfiguration();

        for (ISection section : config.getSectionList("commands")) {
            String name = section.getString("name");
            List<String> aliases = section.exists("aliases") ? section.getStringList("aliases") : Lists.newArrayList();
            String permission = section.exists("permission") ? section.getString("permission") : null;
            List<String> commands = section.exists("execute") ? section.getStringList("execute") : Lists.newArrayList();

            BUCommand command = new BUCommand(name, aliases, permission) {

                @Override
                public void onExecute(User user, String[] args) {
                    final String messagesKey = "messages";
                    final List<TextComponent> components;

                    if (section.isList(messagesKey)) {
                        components = MessageBuilder.buildMessage(user, section.getSectionList(messagesKey));
                    } else {
                        components = Lists.newArrayList(MessageBuilder.buildMessage(user, section.getSection(messagesKey)));
                    }

                    components.forEach(user::sendMessage);
                    commands.forEach(command -> ProxyServer.getInstance().getPluginManager().dispatchCommand(
                            ProxyServer.getInstance().getConsole(),
                            PlaceHolderAPI.formatMessage(user, command)
                    ));
                }

                @Override
                public List<String> onTabComplete(User user, String[] args) {
                    return ImmutableList.of();
                }
            };

            customCommands.add(command);
        }
    }

    private void loadPunishmentCommand(String name, Class<? extends BUCommand> clazz) {
        loadCommand("commands." + name + ".enabled", FileLocation.PUNISHMENTS.getConfiguration(), clazz);
    }

    private void loadGeneralCommand(String name, Class<? extends BUCommand> clazz) {
        loadCommand(name + ".enabled", FileLocation.GENERALCOMMANDS.getConfiguration(), clazz);
    }

    private void loadCommand(String enabledPath, IConfiguration configuration, Class<? extends BUCommand> clazz) {
        if (configuration.getBoolean(enabledPath)) {
            try {
                BUCommand command = clazz.newInstance();

                generalCommands.add(command);
            } catch (InstantiationException | IllegalAccessException e) {
                BUCore.getLogger().error(ERROR_STRING, e);
            }
        }
    }
}