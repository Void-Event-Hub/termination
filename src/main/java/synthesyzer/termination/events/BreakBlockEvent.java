package synthesyzer.termination.events;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import synthesyzer.termination.Termination;
import synthesyzer.termination.command.EnableNucleusBreakingCommand;
import synthesyzer.termination.data.team.TeamDataManager;
import synthesyzer.termination.network.TMNetwork;
import synthesyzer.termination.network.packets.servertoclient.BreakNucleusPacket;
import synthesyzer.termination.network.packets.servertoclient.UpdateTeamDataPacket;
import synthesyzer.termination.registry.blocks.TMBlocks;
import synthesyzer.termination.util.Messenger;

public class BreakBlockEvent {
    public static void register() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (world.isClient) {
                return true;
            }

            if (!state.getBlock().equals(TMBlocks.NUCLEUS_BLOCK)) {
                return handleNonNucleusBlock(pos, world, (ServerPlayerEntity) player);
            }

            var teamManager = TeamDataManager.get(world);
            AbstractTeam attackingTeam = player.getScoreboardTeam();

            if (attackingTeam == null) {
                Messenger.sendMessage(player, "You are not in a team");
                return false;
            }

            var attackedTeamData = teamManager.getTeamDataByNucleus(pos);

            if (attackedTeamData.isEmpty()) {
                Messenger.sendMessage(player, "This nucleus is not part of a Team. Please contact an admin");
                return false;
            }

            AbstractTeam attackedTeam = world.getScoreboard().getTeam(attackedTeamData.get().getName());

            boolean playerBreaksOwnNucleus = attackingTeam.equals(attackedTeam);
            boolean isAdmin = player.hasPermissionLevel(3) && player.isCreative();

            if (playerBreaksOwnNucleus) {
                if (isAdmin) {
                    attackedTeamData.get().setNucleus(null);
                    teamManager.setDirty(true);
                    Messenger.sendMessage(player, "Removed nucleus from " + attackedTeamData.get().getName());
                    return true;
                } else {
                    Messenger.sendMessage(player, "You can't break your own nucleus");
                    return false;
                }
            }

            if (isAdmin) {
                Messenger.sendError(player, "You can't remove other civilizations' nucleus");
                return false;
            }

            if (!EnableNucleusBreakingCommand.EnableNucleusBreaking) {
                Messenger.sendError(player, "Can't break nucleus during starting phase");
                return false;
            }

            attackedTeamData.get().damage(Termination.CONFIG.damagePerNucleusBreak());
            teamManager.setDirty(true);

            var attackingTeamData = teamManager.getTeamData(attackingTeam.getName()).get();

            TMNetwork.CHANNEL.serverHandle(player.getServer()).send(new BreakNucleusPacket(attackingTeamData, attackedTeamData.get()));
            TMNetwork.CHANNEL.serverHandle(player.getServer()).send(new UpdateTeamDataPacket(teamManager.getTeamData()));
            player.getWorld().playSound(null, pos, SoundEvents.BLOCK_DEEPSLATE_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);

            if (attackedTeamData.get().isDead()) {
                attackedTeamData.get().setNucleus(null);
                teamManager.setDirty(true);
                return true;
            }

            return false;
        });
    }

    private static boolean handleNonNucleusBlock(BlockPos pos, World world, ServerPlayerEntity player) {
        if (player.hasPermissionLevel(3) && player.isCreative()) {
            return true;
        }

        var teamManager = TeamDataManager.get(world);
        var nucleusInRange = teamManager.getNucleusInRange(pos, Termination.CONFIG.nucleusProtectionRadius());

        if (nucleusInRange.isEmpty()) {
            return true;
        }

        return false;
    }
}
