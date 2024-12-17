package be.dieterblancke.bungeeutilisalsx.common.api.utils.config;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs.*;
import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.util.List;

public class ConfigFiles
{

    public static MainConfig CONFIG = new MainConfig( "/configurations/config.yml" );
    public static ServerGroupsConfig SERVERGROUPS = new ServerGroupsConfig( "/configurations/servergroups.yml" );
    public static MotdConfig MOTD = new MotdConfig( "/configurations/motd/motd.yml" );
    public static Config CUSTOMCOMMANDS = new Config( "/configurations/commands/customcommands.yml" );
    public static Config GENERALCOMMANDS = new Config( "/configurations/commands/generalcommands.yml" );
    public static Config LANGUAGES_CONFIG = new Config( "/configurations/languages/config.yml" );
    public static RanksConfig RANKS = new RanksConfig( "/configurations/chat/ranks.yml" );

    public static void loadAllConfigs()
    {
        final List<Config> configs = getAllConfigs();

        for ( Config config : configs )
        {
            config.load();
        }
        BuX.getLogger().info( "Finished loaded config files" );
    }

    public static void reloadAllConfigs()
    {
        final List<Config> configs = getAllConfigs();

        for ( Config config : configs )
        {
            config.reload();
        }
    }

    public static List<Config> getAllConfigs()
    {
        final List<Config> configs = Lists.newArrayList();

        for ( Field field : ConfigFiles.class.getFields() )
        {
            try
            {
                final Object value = field.get( null );

                if ( value instanceof Config )
                {
                    configs.add( (Config) value );
                }
            }
            catch ( IllegalAccessException e )
            {
                // ignore
            }
        }
        return configs;
    }
}
