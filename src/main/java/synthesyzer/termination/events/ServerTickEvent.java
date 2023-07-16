package synthesyzer.termination.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import synthesyzer.termination.Termination;
import synthesyzer.termination.command.StartEventCommand;
import synthesyzer.termination.data.death.DeathTracker;
import synthesyzer.termination.data.team.TeamData;
import synthesyzer.termination.data.team.TeamDataManager;
import synthesyzer.termination.util.Messenger;
import synthesyzer.termination.util.PhaseManager;
import synthesyzer.termination.util.Time;

public class ServerTickEvent {

    private static int ticks = 0;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            ServerWorld world = server.getOverworld();

            if (world == null) {
                return;
            }

            ticks++;

            if (ticks % (Time.TICKS_PER_SECOND) == 0) {
                world.getPlayers().forEach(ServerTickEvent::boostPlayersInBase);

                if (PhaseManager.isEndPhase()) {
                    addEffectsToPlayersInTeam(world);
                }

            }

            if (StartEventCommand.startedEvent()) {
                PhaseManager.tick(world);
            }

            if (Termination.CONFIG.playerDeathCooldown() > 0) {
                handleDeathTracking(world);
            }

        });
    }

    /**
     * Gets the current tick of the server.
     * @return the current tick of the server.
     */
    public static int getCurrentTick() {
        return ticks;
    }

    private static void handleDeathTracking(ServerWorld world) {
        DeathTracker deathTracker = DeathTracker.get(world);
        deathTracker.update();

        deathTracker.getDeathTimers().forEach((uuid, remainingTicks) -> {
            var player = world.getPlayerByUuid(uuid);

            if (player instanceof ServerPlayerEntity serverPlayer) {
                if (remainingTicks % 20 == 0) {
                    Messenger.sendClientMessage(serverPlayer, "§7You will be revived in §6" + (remainingTicks / 20) + " §7seconds.");
                }
            }
        });

        deathTracker.getPlayersToBeRevived().forEach(uuid -> {
            var player = world.getPlayerByUuid(uuid);

            if (player instanceof ServerPlayerEntity serverPlayer) {
                respawnPlayer(serverPlayer);
            }
        });
    }

    private static void respawnPlayer(ServerPlayerEntity player) {
        DeathTracker deathTracker = DeathTracker.get(player.world);
        TeamDataManager teamManager = TeamDataManager.get(player.world);
        var teamData = teamManager.getTeamData(player.getScoreboardTeam());

        if (teamData.isEmpty()) {
            return;
        }

        if (!teamData.get().isDead()) {
            Messenger.sendClientMessage(player, "§aYou have been revived!");
            final int ticksPerSecond = 20;
            BlockPos spawn = getPlayerSpawn(player);
            player.teleport(spawn.getX(), spawn.getY(), spawn.getZ());
            player.changeGameMode(GameMode.SURVIVAL);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 4 * ticksPerSecond, 99));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 10 * ticksPerSecond, 1));
        } else {
            Messenger.sendMessage(player, "§4Game over!");
        }

        deathTracker.removePlayerDeath(player.getUuid());
    }

    private static BlockPos getPlayerSpawn(ServerPlayerEntity player) {
        var teamManager = TeamDataManager.get(player.world);
        var teamData = teamManager.getTeamData(player.getScoreboardTeam());

        if (teamData.isEmpty()) {
            return player.getSpawnPointPosition();
        }

        return teamData.get().getSpawn().orElse(player.world.getSpawnPos());
    }

    private static void boostPlayersInBase(ServerPlayerEntity player) {
        var teamManager = TeamDataManager.get(player.world);
        var data = teamManager.getTeamData(player.getScoreboardTeam());

        if (data.isEmpty()) {
            return;
        }

        TeamData teamData = data.get();

        var nucleus = teamData.getNucleus();

        if (nucleus.isEmpty()) {
            return;
        }

        var nucleusInRange = teamManager.getNucleusInRange(player.getBlockPos(), Termination.CONFIG.nucleusProtectionRadius());

        if (nucleusInRange.isEmpty()) {
            return;
        }

        if (!nucleusInRange.get().equals(nucleus.get())) {
            return;
        }

        player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 30, 0));
    }

    private static void addEffectsToPlayersInTeam(ServerWorld world) {
        TeamDataManager teamDataManager = TeamDataManager.get(world);

        world.getPlayers().forEach(player -> {
            var teamData = teamDataManager.getTeamData(player.getScoreboardTeam());

            if (teamData.isEmpty()) {
                return;
            }

            player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 999_999, 0));
        });
    }

}
