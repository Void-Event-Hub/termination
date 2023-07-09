package synthesyzer.termination.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import synthesyzer.termination.Termination;

public class TMCommands {

    public static void register() {
        Termination.LOGGER.info("Registering commands");
        CommandRegistrationCallback.EVENT.register(SetTeamHealthCommand::register);
        CommandRegistrationCallback.EVENT.register(SetSpawnCommand::register);
        CommandRegistrationCallback.EVENT.register(StartEventCommand::register);
    }

}
