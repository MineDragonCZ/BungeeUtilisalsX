package be.dieterblancke.bungeeutilisalsx.common.permission;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PermissionIntegration {

    boolean isActive();

    CompletableFuture<String> getGroup(UUID user);

    String getPrefix(UUID uuid);

    String getSuffix(UUID uuid);

    default boolean hasLowerOrEqualGroup(final UUID userUuid, final UUID otherUuid) {
        return false;
    }
}
