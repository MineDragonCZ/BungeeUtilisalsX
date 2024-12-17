package be.dieterblancke.bungeeutilisalsx.common.executors;

import be.dieterblancke.bungeeutilisalsx.common.api.event.event.Event;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.EventExecutor;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserLoadEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserUnloadEvent;

public class UserExecutor implements EventExecutor {

    @Event
    public void onLoad(final UserLoadEvent event) {
        event.getApi().addUser(event.getUser());
    }

    @Event
    public void onUnload(final UserUnloadEvent event) {
        event.getApi().removeUser(event.getUser());
    }
}