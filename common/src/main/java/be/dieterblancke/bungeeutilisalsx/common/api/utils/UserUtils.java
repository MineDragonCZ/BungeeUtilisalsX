package be.dieterblancke.bungeeutilisalsx.common.api.utils;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;

import java.util.Optional;
import java.util.function.Consumer;

public class UserUtils
{

    private UserUtils()
    {
    }

    public static long getOnlinePlayersOnDomain( final String domain )
    {
        return BuX.getApi().getUsers().stream().filter(user -> user.getJoinedHost().equalsIgnoreCase( domain ) ).count();
    }

    public static Optional<UserStorage> getUserStorage( final String userName, final Consumer<String> onFailure )
    {
        if ( !BuX.getApi().getPlayerUtils().isOnline( userName ) || StaffUtils.isHidden( userName ) )
        {
            onFailure.accept( "offline" );
            return Optional.empty();
        }
        final UserStorage target = BuX.getApi().getStorageManager().getDao().getUserDao().getUserData( userName ).join().orElse( null );
        if ( target == null || !target.isLoaded() )
        {
            onFailure.accept( "never-joined" );
            return Optional.empty();
        }
        return Optional.of( target );
    }
}
