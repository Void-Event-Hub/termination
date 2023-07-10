package synthesyzer.termination.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import synthesyzer.termination.util.ServerReference;

public class LoadServerEvent {

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(ServerReference::setServer);
    }

}
