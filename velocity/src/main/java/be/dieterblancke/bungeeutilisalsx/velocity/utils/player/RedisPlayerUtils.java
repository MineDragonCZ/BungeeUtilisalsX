package be.dieterblancke.bungeeutilisalsx.velocity.utils.player;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.server.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.player.IPlayerUtils;
import be.dieterblancke.bungeeutilisalsx.velocity.Bootstrap;
import com.google.common.collect.Lists;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RedisPlayerUtils implements IPlayerUtils
{

    @Override
    public int getPlayerCount( String server )
    {
        final Optional<RegisteredServer> registeredServer = Bootstrap.getInstance().getProxyServer().getServer( server );

        return registeredServer.isEmpty() ? 0 : RedisBungeeAPI.getRedisBungeeApi().getPlayersOnServer( server ).size();
    }

    @Override
    public List<String> getPlayers( String server )
    {
        final List<String> players = Lists.newArrayList();
        final Optional<RegisteredServer> registeredServer = Bootstrap.getInstance().getProxyServer().getServer( server );

        if ( registeredServer.isPresent() )
        {
            RedisBungeeAPI.getRedisBungeeApi().getPlayersOnServer( server ).forEach( uuid ->
                    players.add( RedisBungeeAPI.getRedisBungeeApi().getNameFromUuid( uuid ) ) );
        }

        return players;
    }

    @Override
    public int getTotalCount()
    {
        return RedisBungeeAPI.getRedisBungeeApi().getPlayerCount();
    }

    @Override
    public List<String> getPlayers()
    {
        final List<String> players = Lists.newArrayList();

        RedisBungeeAPI.getRedisBungeeApi().getPlayersOnline().forEach( uuid -> players.add( RedisBungeeAPI.getRedisBungeeApi().getNameFromUuid( uuid ) ) );

        return players;
    }

    @Override
    public IProxyServer findPlayer( String name )
    {
        final UUID uuid = RedisBungeeAPI.getRedisBungeeApi().getUuidFromName( name );

        if ( RedisBungeeAPI.getRedisBungeeApi().isPlayerOnline( uuid ) )
        {
            return Optional.ofNullable( RedisBungeeAPI.getRedisBungeeApi().getServerFor( uuid ) )
                    .map( ServerInfo::getName )
                    .map( BuX.getInstance().proxyOperations()::getServerInfo )
                    .orElse( null );
        }

        return null;
    }

    @Override
    public boolean isOnline( String name )
    {
        final UUID uuid = RedisBungeeAPI.getRedisBungeeApi().getUuidFromName( name );

        if ( uuid == null )
        {
            return false;
        }
        return RedisBungeeAPI.getRedisBungeeApi().isPlayerOnline( uuid );
    }

    @Override
    public UUID getUuidNoFallback( String targetName )
    {
        return RedisBungeeAPI.getRedisBungeeApi().getUuidFromName( targetName, false );
    }
}