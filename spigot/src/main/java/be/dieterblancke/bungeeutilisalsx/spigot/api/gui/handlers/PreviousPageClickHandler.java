package be.dieterblancke.bungeeutilisalsx.spigot.api.gui.handlers;

import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.Gui;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.TriConsumer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PreviousPageClickHandler implements TriConsumer<Gui, Player, InventoryClickEvent>
{

    @Override
    public void accept( final Gui gui, final Player player, final InventoryClickEvent event )
    {
        event.setCancelled( true );
        gui.setPage( gui.getPage() - 1 );
        gui.refill();
    }
}
