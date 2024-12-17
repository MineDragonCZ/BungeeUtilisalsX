package be.dieterblancke.bungeeutilisalsx.common;

import be.dieterblancke.bungeeutilisalsx.common.api.event.event.IEventHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.other.ProxyMotdPingEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.*;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobManager;
import be.dieterblancke.bungeeutilisalsx.common.api.language.Language;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.xml.XMLPlaceHolders;
import be.dieterblancke.bungeeutilisalsx.common.api.pluginsupport.PluginSupport;
import be.dieterblancke.bungeeutilisalsx.common.api.scheduler.IScheduler;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.AbstractStorageManager;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.StorageType;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Platform;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.StaffUser;
import be.dieterblancke.bungeeutilisalsx.common.commands.CommandManager;
import be.dieterblancke.bungeeutilisalsx.common.executors.*;
import be.dieterblancke.bungeeutilisalsx.common.job.SingleProxyJobManager;
import be.dieterblancke.bungeeutilisalsx.common.migration.MigrationManager;
import be.dieterblancke.bungeeutilisalsx.common.migration.MigrationManagerFactory;
import be.dieterblancke.bungeeutilisalsx.common.permission.PermissionIntegration;
import be.dieterblancke.bungeeutilisalsx.common.permission.integrations.DefaultPermissionIntegration;
import be.dieterblancke.bungeeutilisalsx.common.permission.integrations.LuckPermsPermissionIntegration;
import be.dieterblancke.bungeeutilisalsx.common.placeholders.*;
import be.dieterblancke.bungeeutilisalsx.common.pluginsupport.TritonPluginSupport;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.ProtocolizeManager;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.SimpleProtocolizeManager;
import be.dieterblancke.bungeeutilisalsx.common.scheduler.Scheduler;
import be.dieterblancke.configuration.api.FileStorageType;
import com.google.common.collect.Lists;
import lombok.Data;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Data
public abstract class AbstractBungeeUtilisalsX {

    private static AbstractBungeeUtilisalsX INSTANCE;
    private final IScheduler scheduler = new Scheduler();
    private final String name = "BungeeUtilisalsX";
    protected IBuXApi api;
    private AbstractStorageManager abstractStorageManager;
    private PermissionIntegration activePermissionIntegration;
    private JobManager jobManager;
    private ProtocolizeManager protocolizeManager;

    public AbstractBungeeUtilisalsX() {
        INSTANCE = this;
    }

    public static AbstractBungeeUtilisalsX getInstance() {
        return INSTANCE;
    }

    public void initialize() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        this.migrateConfigs();
        this.loadConfigs();

        this.loadPlaceHolders();
        this.loadDatabase();
        this.migrate();

        this.api = this.createBuXApi();

        this.jobManager = new SingleProxyJobManager();

        this.detectPermissionIntegration();
        this.registerProtocolizeSupport();
        this.registerLanguages();
        this.registerListeners();
        this.registerExecutors();
        this.registerCommands();
        this.registerPluginSupports();

        this.setupTasks();
        this.registerMetrics();
    }

    public PermissionIntegration getActivePermissionIntegration() {
        return activePermissionIntegration;
    }

    protected void loadConfigs() {
        ConfigFiles.loadAllConfigs();
    }

    protected abstract IBuXApi createBuXApi();

    public abstract CommandManager getCommandManager();

    protected void loadPlaceHolders() {
        final XMLPlaceHolders xmlPlaceHolders = new XMLPlaceHolders();

        xmlPlaceHolders.addXmlPlaceHolder(new CenterPlaceHolder());

        PlaceHolderAPI.addPlaceHolder(xmlPlaceHolders);

        PlaceHolderAPI.loadPlaceHolderPack(new DefaultPlaceHolders());
        PlaceHolderAPI.loadPlaceHolderPack(new InputPlaceHolders());
        PlaceHolderAPI.loadPlaceHolderPack(new UserPlaceHolderPack());
        PlaceHolderAPI.loadPlaceHolderPack(new PermissionPlaceHolderPack());
    }

    protected void registerLanguages() {
        this.getApi().getLanguageManager().addPlugin(this.getName(), new File(getDataFolder(), "languages"), FileStorageType.YAML);
        this.getApi().getLanguageManager().loadLanguages(this.getClass(), this.getName());
    }

    protected abstract void registerListeners();

    @SuppressWarnings("unchecked")
    protected void registerExecutors() {
        this.api.getEventLoader().register(new UserExecutor(), UserLoadEvent.class, UserUnloadEvent.class, UserServerConnectedEvent.class);
        this.api.getEventLoader().register(new SpyEventExecutor(), UserPrivateMessageEvent.class, UserCommandEvent.class);
        this.api.getEventLoader().register(new UserPluginMessageReceiveEventExecutor(), UserPluginMessageReceiveEvent.class);
        this.api.getEventLoader().register(new UserCommandExecutor(), UserCommandEvent.class);

        if (ConfigFiles.MOTD.isEnabled()) {
            this.api.getEventLoader().register(new ProxyMotdPingExecutor(), ProxyMotdPingEvent.class);
        }
    }

    protected void setupTasks() {
        // do nothing
    }

    public void reload() {
        ConfigFiles.reloadAllConfigs();

        for (Language language : this.api.getLanguageManager().getLanguages()) {
            this.api.getLanguageManager().reloadConfig(this.getName(), language);
        }

        this.getCommandManager().load();
    }

    protected void registerCommands() {
        this.getCommandManager().load();
    }

    protected void registerPluginSupports() {
        PluginSupport.registerPluginSupport(TritonPluginSupport.class);
    }

    protected void loadDatabase() {
        StorageType type;
        try {
            type = StorageType.valueOf(ConfigFiles.CONFIG.getConfig().getString("storage.type").toUpperCase());
        } catch (IllegalArgumentException e) {
            type = StorageType.MYSQL;
        }
        try {
            this.abstractStorageManager = type.getStorageManagerSupplier().get();
        } catch (Exception e) {
            BuX.getLogger().log(Level.SEVERE, "An error occured while initializing the storage manager: ", e);
        }
    }

    protected void detectPermissionIntegration() {
        final List<PermissionIntegration> integrations = Lists.newArrayList(
                new LuckPermsPermissionIntegration()
        );

        for (PermissionIntegration integration : integrations) {
            if (integration.isActive()) {
                activePermissionIntegration = integration;
                return;
            }
        }
        activePermissionIntegration = new DefaultPermissionIntegration();
    }

    public abstract ServerOperationsApi serverOperations();

    public abstract File getDataFolder();

    public abstract String getVersion();

    public abstract List<StaffUser> getStaffMembers();

    public abstract IPluginDescription getDescription();

    public abstract Logger getLogger();

    public abstract Platform getPlatform();

    public void shutdown() {
        Lists.newArrayList(this.api.getUsers()).forEach(User::unload);
        try {
            abstractStorageManager.close();
        } catch (SQLException e) {
            BuX.getLogger().log(Level.SEVERE, "An error occured: ", e);
        }
        api.getEventLoader().getHandlers().forEach(IEventHandler::unregister);
    }

    public boolean isRedisManagerEnabled() {
        return false;
    }

    private void registerProtocolizeSupport() {
        if (BuX.getInstance().serverOperations().getPlugin("Protocolize").isPresent()) {
            this.protocolizeManager = new SimpleProtocolizeManager();
        }
    }

    public boolean isProtocolizeEnabled() {
        return protocolizeManager != null;
    }

    public boolean isPartyManagerEnabled() {
        return false;
    }

    protected abstract void registerMetrics();

    protected void migrate() {
        final MigrationManager migrationManager = MigrationManagerFactory.createMigrationManager();
        migrationManager.initialize();
        try {
            migrationManager.migrate();
        } catch (Exception e) {
            BuX.getLogger().log(Level.SEVERE, "Could not execute migrations", e);
        }
    }

    protected void migrateConfigs() {
        final MigrationManager migrationManager = MigrationManagerFactory.createConfigMigrationManager();
        migrationManager.initialize();
        try {
            migrationManager.migrate();
        } catch (Exception e) {
            BuX.getLogger().log(Level.SEVERE, "Could not execute config migrations", e);
        }
    }
}
