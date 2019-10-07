/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals.utils;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class MojangUtils {

    private static final Gson gson = new Gson();

    public static String getUuid(final String name) {
        try {
            final URL url = new URL(getNameToUuidUrl() + name);
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestProperty("User-Agent", "BungeeUtilisals/" + BungeeUtilisals.getInstance().getDescription().getVersion());

            try (final InputStream input = conn.getInputStream();
                 final InputStreamReader isr = new InputStreamReader(input)) {
                final MojangProfile profile = gson.fromJson(isr, MojangProfile.class);

                if (profile != null) {
                    return profile.getUuid();
                }
            }
        } catch (final IOException e) {
            BUCore.getLogger().warn("Could not retrieve uuid of " + name);
            BUCore.getLogger().error("An error occured: ", e);
        }
        return null;
    }

    public static String getName(final UUID uuid) {
        try {
            final URL url = new URL(getUuidToNameUrl() + uuid.toString().replace("-", ""));
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestProperty("User-Agent", "BungeeUtilisals/" + BungeeUtilisals.getInstance().getDescription().getVersion());

            try (final InputStream input = conn.getInputStream();
                 final InputStreamReader isr = new InputStreamReader(input)) {
                final MojangProfile profile = gson.fromJson(isr, MojangProfile.class);

                if (profile != null) {
                    return profile.getName();
                }
            }
        } catch (final IOException e) {
            BUCore.getLogger().error("An error occured: ", e);
            BUCore.getLogger().warn("Could not retrieve name of " + uuid.toString());
        }
        return null;
    }

    private static String getNameToUuidUrl() {
        return BungeeUtilisals.getInstance().getConfig().get("urls.name-to-uuid", "https://api.minetools.eu/uuid/");
    }

    private static String getUuidToNameUrl() {
        return BungeeUtilisals.getInstance().getConfig().get("urls.uuid-to-name", "https://api.minetools.eu/uuid/");
    }

    public class MojangProfile {

        private String id;
        private String name;
        private String uuid;
        private String uuid_formatted;

        public String getName() {
            return name;
        }

        public String getUuid() {
            if (id != null) {
                return id;
            }
            if (uuid != null) {
                return uuid;
            }
            if (uuid_formatted != null) {
                return uuid_formatted;
            }
            return null;
        }
    }
}