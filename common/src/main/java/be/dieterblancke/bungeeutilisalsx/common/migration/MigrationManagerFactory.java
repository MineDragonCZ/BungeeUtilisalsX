package be.dieterblancke.bungeeutilisalsx.common.migration;

import be.dieterblancke.bungeeutilisalsx.common.migration.config.ConfigMigrationManager;
import be.dieterblancke.bungeeutilisalsx.common.migration.sql.SqlMigrationManager;

public class MigrationManagerFactory {

    private MigrationManagerFactory() {
    }

    public static MigrationManager createMigrationManager() {
        return new SqlMigrationManager();
    }

    public static MigrationManager createConfigMigrationManager() {
        return new ConfigMigrationManager();
    }
}