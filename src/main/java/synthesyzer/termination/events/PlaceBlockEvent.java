package synthesyzer.termination.events;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import synthesyzer.termination.Termination;
import synthesyzer.termination.data.team.TeamDataManager;
import synthesyzer.termination.network.TMNetwork;
import synthesyzer.termination.network.packets.servertoclient.UpdateTeamDataPacket;
import synthesyzer.termination.registry.blocks.TMBlocks;
import synthesyzer.termination.util.Messenger;

public class PlaceBlockEvent {

    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient) {
                return ActionResult.PASS;
            }

            if (!player.getMainHandStack().getItem().equals(TMBlocks.NUCLEUS_BLOCK.asItem())) {
                return handleNonNucleusBlock(hitResult.getBlockPos(), world, (ServerPlayerEntity) player);
            }

            if (!player.hasPermissionLevel(3)) {
                Messenger.sendError(player, "You don't have permission to place a nucleus");
                return ActionResult.FAIL;
            }

            var teamManager = TeamDataManager.get(world);
            var team = player.getScoreboardTeam();

            if (team == null) {
                Messenger.sendError(player, "You are not in a team");
                return ActionResult.FAIL;
            }

            var teamData = teamManager.getTeamData(team.getName()).get();

            if (teamData.getNucleus().isPresent()) {
                Messenger.sendError(player, "Your team already has a nucleus at" + teamData.getNucleus().get() + ". Remove it first");
                return ActionResult.FAIL;
            }

            teamData.setNucleus(hitResult.getBlockPos().add(hitResult.getSide().getVector()));
            teamManager.setDirty(true);
            Messenger.sendMessage(player, "Set nucleus for " + team.getName());

            TMNetwork.CHANNEL.serverHandle(player.getServer()).send(new UpdateTeamDataPacket(teamManager.getTeamData()));

            return ActionResult.PASS;
        });
    }

    private static ActionResult handleNonNucleusBlock(BlockPos pos, World world, ServerPlayerEntity serverPlayer) {
        if (serverPlayer.hasPermissionLevel(3) && serverPlayer.isCreative()) {
            return ActionResult.PASS;
        }

        var teamManager = TeamDataManager.get(world);
        var nucleusInRange = teamManager.getNucleusInRange(pos, Termination.CONFIG.nucleusProtectionRadius());

        if (nucleusInRange.isEmpty()) {
            return ActionResult.PASS;
        }

        return ActionResult.FAIL;
    }


}
