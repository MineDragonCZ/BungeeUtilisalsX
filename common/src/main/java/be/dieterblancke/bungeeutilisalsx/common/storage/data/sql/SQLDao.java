package be.dieterblancke.bungeeutilisalsx.common.storage.data.sql;

import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.ApiTokenDao;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.Dao;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.OfflineMessageDao;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.UserDao;
import be.dieterblancke.bungeeutilisalsx.common.storage.data.sql.dao.SqlApiTokenDao;
import be.dieterblancke.bungeeutilisalsx.common.storage.data.sql.dao.SqlOfflineMessageDao;
import be.dieterblancke.bungeeutilisalsx.common.storage.data.sql.dao.SqlUserDao;
import lombok.Getter;

@Getter
public class SQLDao implements Dao {

    private final UserDao userDao;
    private final OfflineMessageDao offlineMessageDao;
    private final ApiTokenDao apiTokenDao;

    public SQLDao() {
        this.userDao = new SqlUserDao();
        this.offlineMessageDao = new SqlOfflineMessageDao();
        this.apiTokenDao = new SqlApiTokenDao();
    }
}
