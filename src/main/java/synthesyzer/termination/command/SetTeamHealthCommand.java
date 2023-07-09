package synthesyzer.termination.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.TeamArgumentType;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import synthesyzer.termination.Termination;
import synthesyzer.termination.data.team.TeamDataManager;
import synthesyzer.termination.network.TMNetwork;
import synthesyzer.termination.network.packets.servertoclient.UpdateTeamDataPacket;

public class SetTeamHealthCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("team").requires(source -> source.hasPermissionLevel(3))
                .then(CommandManager.literal("sethealth")
                        .then(CommandManager.argument("name", TeamArgumentType.team())
                                .then(CommandManager.argument("health", IntegerArgumentType.integer(0, Termination.CONFIG.maxNucleusHealth()))
                                        .executes(context -> {
                                            var src = context.getSource();
                                            ServerWorld world = src.getWorld();
                                            AbstractTeam team = TeamArgumentType.getTeam(context, "name");
                                            int health = IntegerArgumentType.getInteger(context, "health");

                                            var teamData = TeamDataManager.get(world).getTeamData(team);

                                            if (teamData.isEmpty()) {
                                                src.sendFeedback(Text.of("Team " + team.getName() + " does not exist"), true);
                                                return 0;
                                            }

                                            teamData.get().setHealth(health);

                                            src.sendFeedback(Text.of("Set team " + team.getName() + " health to " + health), true);
                                            TMNetwork.CHANNEL.serverHandle(src.getServer()).send(
                                                    new UpdateTeamDataPacket(TeamDataManager.get(world).getTeamData())
                                            );
                                            return 1;
                                        })
                                )
                        )
                )

        );
    }

}
