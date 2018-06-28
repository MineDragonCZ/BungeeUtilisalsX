package com.dbsoftwares.bungeeutilisals.bungee.commands.punishments;

import com.dbsoftwares.bungeeutilisals.api.command.Command;
import com.dbsoftwares.bungeeutilisals.api.event.events.punishment.UserPunishEvent;
import com.dbsoftwares.bungeeutilisals.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;

import java.util.Arrays;
import java.util.List;

public class IPTempMuteCommand extends Command {

    public IPTempMuteCommand() {
        super("iptempmute", Arrays.asList(BungeeUtilisals.getConfiguration(FileLocation.PUNISHMENTS_CONFIG)
                        .getString("commands.iptempmute.aliases").split(", ")),
                BungeeUtilisals.getConfiguration(FileLocation.PUNISHMENTS_CONFIG).getString("commands.iptempmute.permission"));
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return null;
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length < 3) {
            user.sendLangMessage("punishments.iptempmute.usage");
            return;
        }
        String timeFormat = args[1];
        String reason = Utils.formatList(Arrays.copyOfRange(args, 2, args.length), " ");
        Long time = Utils.parseDateDiff(timeFormat);

        if (time == 0L) {
            user.sendLangMessage("punishments.iptempmute.non-valid");
            return;
        }
        if (!BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().isUserPresent(args[0])) {
            user.sendLangMessage("never-joined");
            return;
        }
        UserStorage storage = BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().getUser(args[0]);
        if (BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().isIPTempMutePresent(storage.getIp(), true)) {
            user.sendLangMessage("punishments.iptempmute.already-muted");
            return;
        }

        UserPunishEvent event = new UserPunishEvent(PunishmentType.IPTEMPMUTE, user, storage.getUuid(),
                storage.getUserName(), storage.getIp(), reason, user.getServerName(), time);
        api.getEventLoader().launchEvent(event);

        if (event.isCancelled()) {
            user.sendLangMessage("punishments.cancelled");
            return;
        }
        IPunishmentExecutor executor = api.getPunishmentExecutor();
        PunishmentInfo info = executor.addIPTempMute(storage.getUuid(), storage.getUserName(), storage.getIp(), time,
                reason, user.getServerName(), user.getName());

        api.getUser(storage.getUserName()).ifPresent(muted -> muted.sendLangMessage("punishments.iptempmute.onmute",
                executor.getPlaceHolders(info).toArray(new Object[]{})));

        user.sendLangMessage("punishments.iptempmute.executed", executor.getPlaceHolders(info));

        api.langBroadcast("punishments.iptempmute.broadcast",
                BungeeUtilisals.getConfiguration(FileLocation.PUNISHMENTS_CONFIG).getString("commands.iptempmute.broadcast"),
                executor.getPlaceHolders(info).toArray(new Object[]{}));
    }
}