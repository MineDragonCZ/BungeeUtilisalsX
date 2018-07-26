package com.dbsoftwares.bungeeutilisals.user;

/*
 * Created by DBSoftwares on 04 september 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserLoadEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserPreLoadEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserUnloadEvent;
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.storage.DataManager;
import com.dbsoftwares.bungeeutilisals.api.user.UserCooldowns;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.IExperimentalUser;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import lombok.Data;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Objects;

@Data
public class BUser implements User {

    private String player;
    private Boolean socialspy = false;
    private ExperimentalUser experimental;
    private UserCooldowns cooldowns;
    private UserStorage storage;
    private PunishmentInfo mute;

    @Override
    public void load(UserPreLoadEvent event) {
        ProxiedPlayer p = event.getPlayer();
        DataManager dataManager = BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager();

        this.player = p.getName();
        this.experimental = new ExperimentalUser(this);
        this.storage = new UserStorage();
        this.cooldowns = new UserCooldowns();

        if (dataManager.isUserPresent(p.getUniqueId())) {
            storage = dataManager.getUser(p.getUniqueId());

            storage.setLanguage(BUCore.getApi().getLanguageManager().getLanguageIntegration().getLanguage(p.getUniqueId()));
        } else {
            dataManager.insertIntoUsers(p.getUniqueId().toString(),
                    p.getName(), Utils.getIP(p.getAddress()), BUCore.getApi().getLanguageManager().getDefaultLanguage().getName());
            storage = new UserStorage();
            storage.setDefaultsFor(p);
        }

        if (!storage.getUserName().equalsIgnoreCase(player)) { // Stored name != user current name | Name changed?
            storage.setUserName(player);
            dataManager.updateUser(p.getUniqueId().toString(), player, null, null);
        }

        if (FileLocation.PUNISHMENTS_CONFIG.getConfiguration().getBoolean("enabled")) {
            if (dataManager.isMutePresent(p.getUniqueId(), true)) {
                mute = dataManager.getMute(p.getUniqueId());
            } else if (dataManager.isTempMutePresent(p.getUniqueId(), true)) {
                mute = dataManager.getTempMute(p.getUniqueId());
            } else if (dataManager.isIPMutePresent(getIP(), true)) {
                mute = dataManager.getIPMute(getIP());
            } else if (dataManager.isIPTempMutePresent(getIP(), true)) {
                mute = dataManager.getIPTempMute(getIP());
            }
        }

        UserLoadEvent userLoadEvent = new UserLoadEvent(this);
        BungeeUtilisals.getApi().getEventLoader().launchEvent(userLoadEvent);
    }

    @Override
    public void unload() {
        save();
        cooldowns.remove();

        UserUnloadEvent event = new UserUnloadEvent(this);
        BungeeUtilisals.getApi().getEventLoader().launchEventAsync(event);
    }

    @Override
    public void save() {
        BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().updateUser(getIdentifier(), getName(), getIP(), getLanguage().getName());
    }

    @Override
    public String getIdentifier() {
        return getParent().getUniqueId().toString();
    }

    @Override
    public UserStorage getStorage() {
        return storage;
    }

    @Override
    public UserCooldowns getCooldowns() {
        return cooldowns;
    }

    @Override
    public String getIP() {
        return Utils.getIP(getParent().getAddress());
    }

    @Override
    public Language getLanguage() {
        return storage.getLanguage();
    }

    @Override
    public void setLanguage(Language language) {
        storage.setLanguage(language);
    }

    @Override
    public CommandSender sender() {
        return getParent();
    }

    @Override
    public void sendRawMessage(String message) {
        sendMessage(new TextComponent(PlaceHolderAPI.formatMessage(this, message)));
    }

    @Override
    public void sendRawColorMessage(String message) {
        sendMessage(Utils.format(this, message));
    }

    @Override
    public void sendMessage(String message) {
        sendMessage(getLanguageConfig().getString("prefix"), PlaceHolderAPI.formatMessage(this, message));
    }

    @Override
    public void sendLangMessage(String path) {
        if (getLanguageConfig().isList(path)) {
            for (String message : getLanguageConfig().getStringList(path)) {
                sendMessage(message);
            }
        } else {
            sendMessage(getLanguageConfig().getString(path));
        }
    }

    @Override
    public void sendLangMessage(String path, Object... placeholders) {
        if (getLanguageConfig().isList(path)) {
            for (String message : getLanguageConfig().getStringList(path)) {
                for (int i = 0; i < placeholders.length - 1; i += 2) {
                    message = message.replace(placeholders[i].toString(), placeholders[i + 1].toString());
                }

                sendMessage(message);
            }
        } else {
            String message = getLanguageConfig().getString(path);
            for (int i = 0; i < placeholders.length - 1; i += 2) {
                message = message.replace(placeholders[i].toString(), placeholders[i + 1].toString());
            }

            sendMessage(message);
        }
    }

    @Override
    public void sendMessage(String prefix, String message) {
        sendMessage(Utils.format(prefix + PlaceHolderAPI.formatMessage(this, message)));
    }

    @Override
    public void sendMessage(BaseComponent component) {
        getParent().sendMessage(component);
    }

    @Override
    public void sendMessage(BaseComponent[] components) {
        getParent().sendMessage(components);
    }

    @Override
    public void kick(String reason) {
        BUCore.getApi().getSimpleExecutor().asyncExecute(() -> getParent().disconnect(Utils.format(reason)));
    }

    @Override
    public void langKick(String path, Object... placeholders) {
        if (getLanguageConfig().isList(path)) {
            StringBuilder builder = new StringBuilder();

            for (String message : getLanguageConfig().getStringList(path)) {
                for (int i = 0; i < placeholders.length - 1; i += 2) {
                    message = message.replace(placeholders[i].toString(), placeholders[i + 1].toString());
                }

                builder.append(message).append("\n");
            }
            kick(builder.toString());
        } else {
            String message = getLanguageConfig().getString(path);
            for (int i = 0; i < placeholders.length - 1; i += 2) {
                message = message.replace(placeholders[i].toString(), placeholders[i + 1].toString());
            }

            kick(message);
        }
    }

    @Override
    public void forceKick(String reason) {
        getParent().disconnect(Utils.format(reason));
    }

    @Override
    public String getName() {
        return player;
    }

    @Override
    public void sendNoPermMessage() {
        sendLangMessage("no-permission");
    }

    @Override
    public void setSocialspy(Boolean socialspy) {
        this.socialspy = socialspy;
    }

    @Override
    public Boolean isSocialSpy() {
        return socialspy;
    }

    @Override
    public ProxiedPlayer getParent() {
        return ProxyServer.getInstance().getPlayer(player);
    }

    @Override
    public IConfiguration getLanguageConfig() {
        return BUCore.getApi().getLanguageManager().getLanguageConfiguration(BUCore.getApi().getPlugin(), this);
    }

    @Override
    public IExperimentalUser experimental() {
        return experimental;
    }

    @Override
    public boolean isConsole() {
        return false;
    }

    @Override
    public String getServerName() {
        return getParent().getServer().getInfo().getName();
    }

    @Override
    public boolean isMuted() {
        return mute != null;
    }

    @Override
    public PunishmentInfo getMuteInfo() {
        return mute;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        BUser user = (BUser) o;
        return user.getPlayer().equalsIgnoreCase(getPlayer()) && user.getIdentifier().equalsIgnoreCase(getIdentifier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, getIdentifier());
    }
}