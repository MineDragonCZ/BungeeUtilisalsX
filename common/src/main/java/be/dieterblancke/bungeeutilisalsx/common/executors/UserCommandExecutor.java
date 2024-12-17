package be.dieterblancke.bungeeutilisalsx.common.executors;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.Event;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.EventExecutor;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserCommandEvent;

public class UserCommandExecutor implements EventExecutor {

    @Event
    public void onListenerCommand(final UserCommandEvent event) {
        final String commandName = event.getActualCommand().replaceFirst("/", "");
        BuX.getInstance().getCommandManager().findCommandByName(commandName).ifPresent(command ->
        {
            if (command.isListenerBased() && !command.isDisabledInServer(event.getUser().getServerName())) {
                BuX.debug("Executing listener command " + commandName);
                event.setCancelled(true);
                command.execute(event.getUser(), event.getArguments());
            }
        });
    }
}
