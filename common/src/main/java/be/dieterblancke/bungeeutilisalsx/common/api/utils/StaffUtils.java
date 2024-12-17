package be.dieterblancke.bungeeutilisalsx.common.api.utils;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.StaffRankData;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.StaffUser;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class StaffUtils {

    private StaffUtils() {
    }

    public static List<String> filterPlayerList(final Collection<String> players) {
        final List<String> temp = Lists.newArrayList();

        for (String player : players) {
            if (!isHidden(player)) {
                temp.add(player);
            }
        }
        return temp;
    }

    public static boolean isHidden(final String name) {
        for (StaffUser user : BuX.getApi().getStaffMembers()) {
            if (user.getName().equalsIgnoreCase(name) && (user.isHidden() || user.isVanished())) {
                return true;
            }
        }
        return BuX.getApi().getUser(name).map(User::isVanished).orElse(false);
    }

    public static Optional<StaffRankData> getStaffRankForUser(User user) {
        return getStaffRankForUserByPermissions(user);
    }

    private static Optional<StaffRankData> getStaffRankForUserByPermissions(User user) {
        return Optional.empty();
    }
}
