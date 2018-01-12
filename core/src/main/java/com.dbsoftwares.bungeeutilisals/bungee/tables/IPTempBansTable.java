package com.dbsoftwares.bungeeutilisals.bungee.tables;

/*
 * Created by DBSoftwares on 05/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.mysql.storage.StorageColumn;
import com.dbsoftwares.bungeeutilisals.api.mysql.storage.StorageTable;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentTable;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@StorageTable(name = "{iptempbans-table}", indexes = {"uuid", "user", "active", "ip"}, foreign = {"uuid => {users-table}(uuid)"})
public class IPTempBansTable implements PunishmentTable {

    @StorageColumn(type = "INT(11)", primary = true, nullable = false, autoincrement = true)
    private int id;

    @StorageColumn(type = "VARCHAR(64)", nullable = false)
    private String uuid;

    @StorageColumn(type = "VARCHAR(24)", nullable = false)
    private String user;

    @StorageColumn(type = "VARCHAR(32)", nullable = false)
    private String ip;

    @StorageColumn(type = "LONG", nullable = false)
    private Long removeTime;

    @StorageColumn(type = "TEXT", nullable = false)
    private String reason;

    @StorageColumn(type = "VARCHAR(32)", nullable = false)
    private String server;

    @StorageColumn(type = "DATETIME", nullable = false, def = "CURRENT_TIMESTAMP")
    private String date;

    @StorageColumn(type = "BOOLEAN", nullable = false)
    private Boolean active;

    @StorageColumn(type = "VARCHAR(32)", nullable = false)
    private String executedby;

    @StorageColumn(type = "VARCHAR(32)")
    private String removedby;

    public static IPTempBansTable fromInfo(PunishmentInfo info) {
        IPTempBansTable table = new IPTempBansTable();

        table.setUuid(info.getUuid());
        table.setUser(info.getUser());
        table.setIp(info.getIP());
        table.setRemoveTime(info.getTime());
        table.setReason(info.getReason());
        table.setServer(info.getServer());
        table.setActive(true);
        table.setExecutedby(info.getBy());

        return table;
    }

    @Override
    public PunishmentType getType() {
        return PunishmentType.IPTEMPBAN;
    }

    @Override
    public Boolean isActive() {
        return active;
    }
}