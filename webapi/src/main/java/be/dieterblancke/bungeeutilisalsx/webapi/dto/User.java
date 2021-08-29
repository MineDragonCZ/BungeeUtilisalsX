package be.dieterblancke.bungeeutilisalsx.webapi.dto;

import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import lombok.Value;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Value
public class User
{

    UUID uuid;
    String userName;
    String ip;
    String languageId;
    LocalDateTime firstLogin;
    LocalDateTime lastLogout;
    List<String> ignoredUsers;
    String joinedHost;

    public static User of( final UserStorage storage )
    {
        return new User(
                storage.getUuid(),
                storage.getUserName(),
                storage.getIp(),
                storage.getLanguage().getName(),
                new Timestamp( storage.getFirstLogin().getTime() ).toLocalDateTime(),
                new Timestamp( storage.getLastLogout().getTime() ).toLocalDateTime(),
                storage.getIgnoredUsers(),
                storage.getJoinedHost()
        );
    }

    public static User console()
    {
        return new User(
                UUID.randomUUID(),
                "CONSOLE",
                "127.0.0.1",
                null,
                null,
                null,
                new ArrayList<>(),
                null
        );
    }
}
