package synthesyzer.termination.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class StartEventCommand {

    public static boolean startEvent = false;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("startevent").requires(source -> source.hasPermissionLevel(3))
            .executes(context -> {
                if (startEvent) {
                    context.getSource().sendFeedback(Text.of("Termination Event already started!"), true);
                    return 0;
                }
                startEvent = true;
                context.getSource().sendFeedback(Text.of("Started Termination Event!"), true);
                return 1;
            }));
    }

    public static boolean startedEvent() {
        return startEvent;
    }

}
