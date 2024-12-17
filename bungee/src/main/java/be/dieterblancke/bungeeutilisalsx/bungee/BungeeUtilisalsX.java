package be.dieterblancke.bungeeutilisalsx.bungee;

import be.dieterblancke.bungeeutilisalsx.bungee.listeners.MotdPingListener;
import be.dieterblancke.bungeeutilisalsx.bungee.listeners.PluginMessageListener;
import be.dieterblancke.bungeeutilisalsx.bungee.listeners.UserChatListener;
import be.dieterblancke.bungeeutilisalsx.bungee.listeners.UserConnectionListener;
import be.dieterblancke.bungeeutilisalsx.bungee.pluginsupports.PremiumVanishPluginSupport;
import be.dieterblancke.bungeeutilisalsx.bungee.pluginsupports.TritonBungeePluginSupport;
import be.dieterblancke.bungeeutilisalsx.bungee.utils.player.BungeePlayerUtils;
import be.dieterblancke.bungeeutilisalsx.common.AbstractBungeeUtilisalsX;
import be.dieterblancke.bungeeutilisalsx.common.IBuXApi;
import be.dieterblancke.bungeeutilisalsx.common.IPluginDescription;
import be.dieterblancke.bungeeutilisalsx.common.ServerOperationsApi;
import be.dieterblancke.bungeeutilisalsx.common.api.pluginsupport.PluginSupport;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Platform;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.StaffUser;
import be.dieterblancke.bungeeutilisalsx.common.commands.CommandManager;
import be.dieterblancke.bungeeutilisalsx.common.event.EventLoader;
import be.dieterblancke.bungeeutilisalsx.common.language.PluginLanguageManager;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.ProxyServer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class BungeeUtilisalsX extends AbstractBungeeUtilisalsX {

    private final ServerOperationsApi serverOperationsApi = new BungeeOperationsApi();
    private final CommandManager commandManager = new CommandManager();
    private final IPluginDescription pluginDescription = new BungeePluginDescription();
    private final List<StaffUser> staffMembers = new ArrayList<>();
    private final BungeeAudiences bungeeAudiences;

    public BungeeUtilisalsX() {
        this.bungeeAudiences = BungeeAudiences.create(Bootstrap.getInstance());
    }

    public static BungeeUtilisalsX getInstance() {
        return (BungeeUtilisalsX) AbstractBungeeUtilisalsX.getInstance();
    }

    @Override
    protected IBuXApi createBuXApi() {

        return new BuXApi(
                new PluginLanguageManager(),
                new EventLoader(),
                new BungeePlayerUtils()
        );
    }

    @Override
    public CommandManager getCommandManager() {
        return commandManager;
    }

    @Override
    protected void registerListeners() {
        ProxyServer.getInstance().getPluginManager().registerListener(Bootstrap.getInstance(), new UserChatListener());
        ProxyServer.getInstance().getPluginManager().registerListener(Bootstrap.getInstance(), new UserConnectionListener());
        ProxyServer.getInstance().getPluginManager().registerListener(Bootstrap.getInstance(), new PluginMessageListener());

        if (ConfigFiles.MOTD.isEnabled()) {
            ProxyServer.getInstance().getPluginManager().registerListener(Bootstrap.getInstance(), new MotdPingListener());
        }
    }

    @Override
    protected void registerPluginSupports() {
        super.registerPluginSupports();

        PluginSupport.registerPluginSupport(PremiumVanishPluginSupport.class);
        PluginSupport.registerPluginSupport(TritonBungeePluginSupport.class);
    }

    @Override
    public ServerOperationsApi serverOperations() {
        return serverOperationsApi;
    }

    @Override
    public File getDataFolder() {
        return Bootstrap.getInstance().getDataFolder();
    }

    @Override
    public String getVersion() {
        return Bootstrap.getInstance().getDescription().getVersion();
    }

    @Override
    public List<StaffUser> getStaffMembers() {
        return staffMembers;
    }

    @Override
    public IPluginDescription getDescription() {
        return pluginDescription;
    }

    @Override
    public Logger getLogger() {
        return Bootstrap.getInstance().getLogger();
    }

    @Override
    public Platform getPlatform() {
        return Platform.BUNGEECORD;
    }

    public BungeeAudiences getBungeeAudiences() {
        return bungeeAudiences;
    }

    @Override
    protected void registerMetrics() {
    }
}
