package be.dieterblancke.bungeeutilisalsx.common.protocolize;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.SoundCategory;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.Sound;

import java.util.Optional;

public class SimpleProtocolizeManager implements ProtocolizeManager {

    @Override
    public void sendSound(final User user, final SoundData soundData) {
        if (user == null || soundData == null) {
            return;
        }

        final ProtocolizePlayer protocolizePlayer = this.getProtocolizePlayer(user);

        if (protocolizePlayer != null) {
            final Sound sound = Sound.valueOf(soundData.sound());
            final SoundCategory category = SoundCategory.valueOf(soundData.category());

            protocolizePlayer.playSound(sound, category, soundData.volume(), soundData.pitch());
        }
    }

    @Override
    public void closeInventory(final User user) {
        if (user == null) {
            return;
        }

        Optional.ofNullable(getProtocolizePlayer(user)).ifPresent(p -> p.closeInventory());
    }

    @Override
    public void openInventory(final User user, final Inventory inventory) {
        if (user == null) {
            return;
        }

        Optional.ofNullable(getProtocolizePlayer(user)).ifPresent(p -> p.openInventory(inventory));
    }

    private ProtocolizePlayer getProtocolizePlayer(final User user) {
        return Protocolize.playerProvider().player(user.getUuid());
    }
}
