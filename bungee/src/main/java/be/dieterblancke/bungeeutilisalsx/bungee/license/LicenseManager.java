package be.dieterblancke.bungeeutilisalsx.bungee.license;

import be.dieterblancke.bungeeutilisalsx.bungee.Bootstrap;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;

public class LicenseManager {
    private static final Bootstrap plugin = Bootstrap.getInstance();

    public static boolean checkLicense() {
        String ip = getMachineIP() + ":" + plugin.getProxy().getConfig().getUuid();
        String key = ConfigFiles.CONFIG.getConfig().getString("license-key");
        String[] keySplit = Objects.requireNonNull(key).split("-");
        StringBuilder keyHashed = new StringBuilder();
        for (int i = 0; i < keySplit.length; i++) {
            if (i < (keySplit.length - 1)) {
                keyHashed.append("*".repeat(keySplit[i].length())).append("-");
            } else {
                keyHashed.append(keySplit[i]);
            }
        }
        plugin.getLogger().info("Attempting to start BuX on " + ip + " with license-key " + keyHashed + " and plugin id " + plugin.getLogger().getName().toLowerCase());
        String gotData = HTTPGetter.getSiteContent("https://zabak.eu/api/?t=license&key=" + key + "&plugin=" + plugin.getLogger().getName().toLowerCase() + "&ip=" + ip);
        if (gotData.equalsIgnoreCase("true")) {
            String gotTimeEnd = HTTPGetter.getSiteContent("https://zabak.eu/api/?t=license&key=" + key + "&plugin=" + plugin.getLogger().getName().toLowerCase() + "&ip=" + ip + "&getTime=1");
            plugin.getLogger().info("License is VALID! Starting plugin...");
            plugin.getLogger().info("License validity ends: " + gotTimeEnd);
            return true;
        }
        plugin.getLogger().info("License is INVALID! Error: " + gotData);
        return false;
    }

    public static String getMachineIP() {
        try {
            URL ipCheckerURL = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(ipCheckerURL.openStream()));
            String ip = in.readLine();
            in.close();
            return ip;
        } catch (Exception ignored) {
        }
        return "0.0.0.0";
    }
}
