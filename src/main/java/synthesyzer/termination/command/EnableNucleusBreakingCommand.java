package synthesyzer.termination.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class EnableNucleusBreakingCommand {

    public static boolean EnableNucleusBreaking = false;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("team").requires(source -> source.hasPermissionLevel(3))
                .then(CommandManager.literal("togglenucleusbreak").executes(context -> {
                    EnableNucleusBreaking = !EnableNucleusBreaking;
                    context.getSource().sendFeedback(Text.of("Set nucleus breaking to " + EnableNucleusBreaking), true);
                    return 1;
                }))
        );
    }

}
