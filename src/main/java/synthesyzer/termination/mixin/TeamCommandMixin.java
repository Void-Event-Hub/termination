package synthesyzer.termination.mixin;

import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TeamCommand;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import synthesyzer.termination.data.team.TeamDataManager;
import synthesyzer.termination.network.TMNetwork;
import synthesyzer.termination.network.packets.servertoclient.UpdateTeamDataPacket;

@Mixin(TeamCommand.class)
public class TeamCommandMixin {

    @Inject(at = @At("HEAD"), method = "executeAdd(Lnet/minecraft/server/command/ServerCommandSource;Ljava/lang/String;)I")
    private static void executeAdd(ServerCommandSource source, String team, CallbackInfoReturnable<Integer> cir) {
        ServerWorld world = source.getWorld();

        if (world.getScoreboard().getTeam(team) == null) {
            TeamDataManager.get(world).createTeamData(team);
            TMNetwork.CHANNEL.serverHandle(source.getServer()).send(
                    new UpdateTeamDataPacket(TeamDataManager.get(world).getTeamData())
            );
        }
    }

    @Inject(at=@At("HEAD"), method= "executeRemove")
    private static void executeRemove(ServerCommandSource source, Team team, CallbackInfoReturnable<Integer> cir) {
        ServerWorld world = source.getWorld();
        if (world.getScoreboard().getTeam(team.getName()) != null) {
            TeamDataManager.get(world).removeTeamData(team.getName());
            TMNetwork.CHANNEL.serverHandle(source.getServer()).send(
                    new UpdateTeamDataPacket(TeamDataManager.get(world).getTeamData())
            );
        }
    }
}
