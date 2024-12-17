package be.dieterblancke.bungeeutilisalsx.common.api.job.jobs;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.job.MultiProxyJob;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
public class UserKickJob implements MultiProxyJob {

    private final UUID uuid;
    private final String userName;
    private final boolean ip;
    private final String ipAddress;
    private final String languagePath;
    private final MessagePlaceholders placeholders;
    private final String reason;

    @Override
    public boolean isAsync() {
        return true;
    }

    public List<User> getUsers() {
        return BuX.getApi().getUsers().stream()
                .filter(user -> this.isIp()
                        ? user.getIp().equalsIgnoreCase(this.ipAddress)
                        : user.getUuid().equals(this.getUuid()) || user.getName().equalsIgnoreCase(this.getUserName()))
                .collect(Collectors.toList());

    }
}
