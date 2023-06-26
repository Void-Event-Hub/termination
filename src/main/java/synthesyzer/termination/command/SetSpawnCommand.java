package synthesyzer.termination.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import synthesyzer.termination.data.team.TeamDataManager;

public class SetSpawnCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("team").requires(source -> source.hasPermissionLevel(3))
                .then(CommandManager.literal("setspawn").requires(source -> source.hasPermissionLevel(3))
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .executes(context -> {
                                    ServerPlayerEntity player = context.getSource().getPlayer();
                                    String teamName = StringArgumentType.getString(context, "name");
                                    var teamDataManager = TeamDataManager.get(context.getSource().getWorld());

                                    var teamData = teamDataManager.getTeamData(teamName);

                                    if (teamData.isEmpty()) {
                                        context.getSource().sendFeedback(Text.of("Team " + teamName + " does not exist"), false);
                                        return 1;
                                    }

                                    teamData.get().setSpawn(player.getBlockPos());
                                    teamDataManager.setDirty(true);
                                    context.getSource().sendFeedback(Text.of("Set spawn for team " + teamName), true);

                                    return 0;
                                })
                        )
                )
        );
    }

}