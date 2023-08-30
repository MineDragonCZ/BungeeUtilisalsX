package be.dieterblancke.bungeeutilisalsx.bungee.listeners;

import be.dieterblancke.bungeeutilisalsx.bungee.user.BungeeUser;
import be.dieterblancke.bungeeutilisalsx.bungee.utils.BungeeServer;
import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserServerConnectEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserServerConnectedEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserServerKickEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import com.google.common.base.Strings;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.Optional;

public class UserConnectionListener implements Listener {

    @EventHandler
    public void onConnect(final PostLoginEvent event) {
        final BungeeUser user = new BungeeUser();

        user.load(event.getPlayer());
        String serverName = event.getPlayer().getServer().getInfo().getName();
        BuX.getInstance().getAbstractStorageManager().getDao().getUserDao().updateUserCurrentServer(user.getUuid(), serverName);
    }

    // Executing on LOWEST priority to get it to execute early on in the quit procedure
    @EventHandler(priority = EventPriority.LOWEST)
    public void onDisconnect(final PlayerDisconnectEvent event) {
        final Optional<User> optional = BuX.getApi().getUser(event.getPlayer().getName());

        if (optional.isEmpty()) {
            return;
        }
        final User user = optional.get();
        BuX.getInstance().getAbstractStorageManager().getDao().getUserDao().updateUserCurrentServer(user.getUuid(), "");
        user.unload();
    }

    @EventHandler
    public void onConnect(final ServerConnectEvent event) {
        final Optional<User> optional = BuX.getApi().getUser(event.getPlayer().getName());

        if (optional.isEmpty()) {
            return;
        }
        final UserServerConnectEvent userServerConnectEvent = new UserServerConnectEvent(
                optional.get(),
                BuX.getInstance().serverOperations().getServerInfo(event.getTarget().getName()),
                UserServerConnectEvent.ConnectReason.parse(event.getReason().toString())
        );
        BuX.getApi().getEventLoader().launchEvent(userServerConnectEvent);
        if (userServerConnectEvent.isCancelled()) {
            event.setCancelled(true);
        }

        event.setTarget(((BungeeServer) userServerConnectEvent.getTarget()).getServerInfo());
        String serverName = userServerConnectEvent.getTarget().getName();
        BuX.getInstance().getAbstractStorageManager().getDao().getUserDao().updateUserCurrentServer(optional.get().getUuid(), serverName);
    }

    @EventHandler
    public void onConnect(final ServerConnectedEvent event) {
        final Optional<User> optional = BuX.getApi().getUser(event.getPlayer().getName());

        if (optional.isEmpty()) {
            return;
        }
        final UserServerConnectedEvent userServerConnectedEvent = new UserServerConnectedEvent(
                optional.get(),
                Optional.ofNullable(Strings.emptyToNull(optional.get().getServerName()))
                        .map(BuX.getInstance().serverOperations()::getServerInfo),
                BuX.getInstance().serverOperations().getServerInfo(event.getServer().getInfo().getName())
        );
        BuX.getApi().getEventLoader().launchEvent(userServerConnectedEvent);

        if (!userServerConnectedEvent.isCancelled()) {
            String serverName = event.getServer().getInfo().getName();
            BuX.getInstance().getAbstractStorageManager().getDao().getUserDao().updateUserCurrentServer(optional.get().getUuid(), serverName);
        }
    }

    @EventHandler
    public void onConnect(ServerKickEvent event) {
        Optional<User> optional = BuX.getApi().getUser(event.getPlayer().getName());

        if (optional.isEmpty()) {
            return;
        }

        GsonComponentSerializer componentSerializer = GsonComponentSerializer.gson();
        UserServerKickEvent userServerKickEvent = new UserServerKickEvent(
                optional.get(),
                event.getKickedFrom() == null ? null : BuX.getInstance().serverOperations().getServerInfo(event.getKickedFrom().getName()),
                event.getCancelServer() == null ? null : BuX.getInstance().serverOperations().getServerInfo(event.getCancelServer().getName()),
                componentSerializer.deserialize(ComponentSerializer.toString(event.getKickReasonComponent()))
        );
        BuX.getApi().getEventLoader().launchEvent(userServerKickEvent);

        if (userServerKickEvent.isTargetChanged()) {
            event.setCancelServer(((BungeeServer) userServerKickEvent.getRedirectServer()).getServerInfo());
            event.setKickReasonComponent(ComponentSerializer.parse(componentSerializer.serialize(userServerKickEvent.getKickMessage())));
        }
        String serverName = userServerKickEvent.getRedirectServer().getName();
        BuX.getInstance().getAbstractStorageManager().getDao().getUserDao().updateUserCurrentServer(optional.get().getUuid(), serverName);
    }
}
