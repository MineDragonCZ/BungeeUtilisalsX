package be.dieterblancke.bungeeutilisalsx.bungee;

import be.dieterblancke.bungeeutilisalsx.bungee.user.BungeeConsoleUser;
import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.IBuXApi;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.IEventLoader;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.BroadcastLanguageMessageJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.BroadcastMessageJob;
import be.dieterblancke.bungeeutilisalsx.common.api.language.ILanguageManager;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.AbstractStorageManager;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.StaffUser;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.HasMessagePlaceholders;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.player.IPlayerUtils;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class BuXApi implements IBuXApi {

    private final ILanguageManager languageManager;
    private final IEventLoader eventLoader;
    private final IPlayerUtils playerUtils;
    private final User consoleUser = new BungeeConsoleUser();
    private final List<User> users = Lists.newCopyOnWriteArrayList();

    @Override
    public Optional<User> getUser(String name) {
        for (User user : users) {
            if (user.getName().equalsIgnoreCase(name)) {
                return Optional.of(user);
            }
        }
        return Optional.ofNullable(null);
    }

    @Override
    public Optional<User> getUser(UUID uuid) {
        for (User user : users) {
            if (user.getUuid().equals(uuid)) {
                return Optional.of(user);
            }
        }
        return Optional.ofNullable(null);
    }

    @Override
    public List<User> getUsers() {
        return Collections.unmodifiableList(users);
    }


    @Override
    public void addUser(User user) {
        this.users.add(user);
    }

    @Override
    public void removeUser(User user) {
        this.users.remove(user);
    }

    @Override
    public List<User> getUsers(String permission) {
        final List<User> result = Lists.newArrayList();

        for (User user : this.getUsers()) {
            if (user.hasPermission(permission)) {
                result.add(user);
            }
        }
        return result;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return BuX.getInstance().getAbstractStorageManager().getConnection();
    }

    @Override
    public void broadcast(final String message) {
        BuX.getInstance().getJobManager().executeJob(new BroadcastMessageJob(null, message, ""));
    }

    @Override
    public void broadcast(final String message, final String permission) {
        BuX.getInstance().getJobManager().executeJob(new BroadcastMessageJob(null, message, permission));
    }

    @Override
    public void announce(final String prefix, final String message) {
        BuX.getInstance().getJobManager().executeJob(new BroadcastMessageJob(prefix, message, ""));
    }

    @Override
    public void announce(final String prefix, final String message, final String permission) {
        BuX.getInstance().getJobManager().executeJob(new BroadcastMessageJob(prefix, message, permission));
    }

    @Override
    public void langBroadcast(final String message, final HasMessagePlaceholders placeholders) {
        BuX.getInstance().getJobManager().executeJob(new BroadcastLanguageMessageJob(message, "", placeholders.getMessagePlaceholders()));
    }

    @Override
    public void langPermissionBroadcast(final String message, final String permission, final HasMessagePlaceholders placeholders) {
        BuX.getInstance().getJobManager().executeJob(new BroadcastLanguageMessageJob(message, permission, placeholders.getMessagePlaceholders()));
    }

    @Override
    public AbstractStorageManager getStorageManager() {
        return BuX.getInstance().getAbstractStorageManager();
    }

    @Override
    public List<StaffUser> getStaffMembers() {
        return BuX.getInstance().getStaffMembers();
    }
}
