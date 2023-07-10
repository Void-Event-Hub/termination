package synthesyzer.termination.events;

import synthesyzer.termination.Termination;

public class TMEvents {
    public static void register() {
        Termination.LOGGER.info("Registering events");
        BreakBlockEvent.register();
        PlaceBlockEvent.register();
        PlayerJoinEvent.register();
        PlayerSpawnEvent.register();
        PlayerDeathEvent.register();
        ServerTickEvent.register();
        LoadServerEvent.register();
    }

}
