package synthesyzer.termination.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import synthesyzer.termination.data.team.TeamDataManager;
import synthesyzer.termination.network.TMNetwork;
import synthesyzer.termination.network.packets.servertoclient.UpdateTeamDataPacket;

public class CreateTeamCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("team").requires(source -> source.hasPermissionLevel(3))
                .then(CommandManager.literal("create")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .executes(context -> {
                                    var src = context.getSource();
                                    ServerWorld world = src.getWorld();
                                    String name = StringArgumentType.getString(context, "name");

                                    if (world.getScoreboard().getTeam(name) != null) {
                                        src.sendFeedback(Text.of("Team " + name + " already exists"), true);
                                        return 0;
                                    }

                                    world.getScoreboard().addTeam(name);
                                    src.sendFeedback(Text.of("Created team " + name), true);
                                    TeamDataManager.get(world).createTeamData(name);
                                    TMNetwork.CHANNEL.serverHandle(src.getServer()).send(
                                            new UpdateTeamDataPacket(TeamDataManager.get(world).getTeamData())
                                    );
                                    return 1;
                                })
                        )
                )
        );
    }
}
