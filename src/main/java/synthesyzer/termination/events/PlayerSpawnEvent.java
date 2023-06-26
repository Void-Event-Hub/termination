package synthesyzer.termination.events;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import synthesyzer.termination.data.team.TeamData;
import synthesyzer.termination.data.team.TeamDataManager;

public class PlayerSpawnEvent {

    public static void register() {
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            handleRespawn(newPlayer, oldPlayer.getLastDeathPos().get().getPos());
        });
    }

    private static void handleRespawn(ServerPlayerEntity player, BlockPos pos) {
        ServerWorld world = player.getWorld();
        if (world == null) {
            return;
        }

        AbstractTeam team = player.getScoreboardTeam();

        if (team == null) {
            return;
        }
        player.changeGameMode(GameMode.SPECTATOR);
        player.teleport(pos.getX(), pos.getY(), pos.getZ());
    }

    private static void teleportPlayerToTeamSpawn(ServerPlayerEntity player) {
        ServerWorld world = player.getWorld();
        if (world == null) {
            return;
        }

        var teamManager = TeamDataManager.get(world);

        AbstractTeam team = player.getScoreboardTeam();

        if (team == null) {
            return;
        }

        TeamData teamData = teamManager.getTeamData(team.getName()).get();
        teamData.getSpawn().ifPresent(spawn -> player.teleport(spawn.getX(), spawn.getY(), spawn.getZ(), true));
    }

}
