package synthesyzer.termination.mixin;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import synthesyzer.termination.data.team.TeamDataManager;
import synthesyzer.termination.network.TMNetwork;
import synthesyzer.termination.network.packets.servertoclient.UpdateTeamDataPacket;
import synthesyzer.termination.util.ServerReference;

@Mixin(Scoreboard.class)
public class ScoreboardMixin {

    @Inject(method = "addPlayerToTeam", at = @At("TAIL"))
    private void addPlayerToTeam(String playerName, Team team, CallbackInfoReturnable<Boolean> cir) {
        MinecraftServer server = ServerReference.getServer();

        if (server == null) {
            return;
        }

        ServerWorld world = server.getOverworld();
        TMNetwork.CHANNEL.serverHandle(server).send(new UpdateTeamDataPacket(TeamDataManager.get(world).getTeamData()));
    }

}
