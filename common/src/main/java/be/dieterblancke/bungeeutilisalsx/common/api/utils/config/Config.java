package be.dieterblancke.bungeeutilisalsx.common.api.utils.config;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.FileUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.exception.InvalidConfigFileException;
import be.dieterblancke.configuration.api.ConfigurationOptions;
import be.dieterblancke.configuration.api.FileStorageType;
import be.dieterblancke.configuration.api.IConfiguration;
import be.dieterblancke.configuration.json.JsonConfigurationOptions;
import be.dieterblancke.configuration.yaml.YamlConfigurationOptions;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Getter
public class Config
{

    private final String location;
    private final FileStorageType storageType;
    protected IConfiguration config;

    public Config( String location )
    {
        this( FileStorageType.YAML, location );
    }

    public Config( FileStorageType storageType, String location )
    {
        this.storageType = storageType;
        this.location = location;
    }

    @SneakyThrows
    public void load()
    {
        File file = new File( BuX.getInstance().getDataFolder(), location.replaceFirst( "configurations/", "" ) );

        if ( !file.exists() )
        {
            InputStream inputStream = FileUtils.getResourceAsStream( location );

            if ( inputStream == null )
            {
                throw new InvalidConfigFileException();
            }

            IConfiguration.createDefaultFile( inputStream, file );
        }

        this.config = IConfiguration.loadConfiguration(
                storageType,
                file,
                this.createConfigurationOptions()
        );

        this.setup();
        BuX.debug( "Successfully loaded config file: " + location.replaceFirst( "configurations/", "" ).substring( 1 ) );
    }

    public void reload()
    {
        try
        {
            this.config.reload();
            this.purge();
            this.setup();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    protected void purge()
    {
        // do nothing
    }

    protected void setup()
    {
        // do nothing
    }

    public boolean isEnabled()
    {
        if ( config == null )
        {
            return false;
        }
        if ( config.exists( "enabled" ) )
        {
            return config.getBoolean( "enabled" );
        }
        return true;
    }

    public boolean isEnabled( String path )
    {
        return isEnabled( path, true );
    }

    public boolean isEnabled( String path, boolean def )
    {
        if ( config.exists( path + ".enabled" ) )
        {
            return config.getBoolean( path + ".enabled" );
        }
        return def;
    }

    private ConfigurationOptions createConfigurationOptions()
    {
        return storageType == FileStorageType.YAML
                ? YamlConfigurationOptions.builder().useComments( true ).build()
                : JsonConfigurationOptions.builder().build();
    }
}
