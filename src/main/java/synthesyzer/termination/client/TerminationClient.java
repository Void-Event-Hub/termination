package synthesyzer.termination.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import synthesyzer.termination.client.ui.TeamScoreboard;
import synthesyzer.termination.data.team.TeamData;
import synthesyzer.termination.network.TMNetwork;
import synthesyzer.termination.network.packets.servertoclient.BreakNucleusPacket;
import synthesyzer.termination.util.Messenger;

import java.util.Random;

public class TerminationClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(TeamScoreboard::render);

        // Server cannot register clientbound packets, so we define the handler here
        TMNetwork.CHANNEL.registerClientbound(BreakNucleusPacket.class, ((message, access) -> {
            TeamData attackingTeam = message.attackingTeam();
            TeamData attackedTeam = message.attackedTeam();
            PlayerEntity player = access.player();
            World world = player.getEntityWorld();

            if (attackedTeam.isDead()) {
                handleTeamDeath(player, world, attackingTeam, attackedTeam);
                return;
            }

            if (attackedTeam.getHealth() % 10 == 0) {
                Messenger.sendMessage(player, attackedTeam.getName() + " is under attack! [ " + attackedTeam.getHealth() + " ]");
            }

            spawnBreakParticles(world, attackedTeam.getNucleus().get());

            AbstractTeam playerTeam = player.getScoreboardTeam();

            if (playerTeam == null) {
                return;
            }

            if (playerTeam.getName().equals(attackedTeam.getName())) {
                player.playSound(SoundEvents.BLOCK_ANVIL_PLACE, 0.7F, 1.0F);
                Messenger.sendClientMessage(player, "Your civilization is under attack! " +  "( " + attackedTeam.getHealth() + " )");
            }

            if (playerTeam.getName().equals(attackingTeam.getName())) {
                player.playSound(SoundEvents.BLOCK_AMETHYST_CLUSTER_BREAK, 1.0F, 1.0F);
                Messenger.sendClientMessage(player, "You are attacking " + attackedTeam.getName() + " ! " + "( " + attackedTeam.getHealth() + " )");
            }
        }));
    }

    private static void handleTeamDeath(PlayerEntity player, World world, TeamData attackingTeam, TeamData attackedTeam) {
        Messenger.sendMessage(player, attackedTeam.getName() + " has been destroyed by " + attackingTeam.getName() + "!");
        player.playSound(SoundEvents.ENTITY_WITHER_DEATH, 1.0F, 1.0F);
        BlockPos nucleus = attackedTeam.getNucleus().get();
        spawnBreakParticles(world, nucleus);
        world.createExplosion(null, nucleus.getX(), nucleus.getY(), nucleus.getZ(), 3, true, Explosion.DestructionType.NONE);

        AbstractTeam playerTeam = player.getScoreboardTeam();

        if (playerTeam == null) {
            return;
        }

        if (playerTeam.getName().equals(attackedTeam.getName())) {
            Messenger.sendMessage(player, "Your civilization has been destroyed !");
        }

        if (playerTeam.getName().equals(attackingTeam.getName())) {
            Messenger.sendMessage(player, "You have destroyed " + attackedTeam.getName() + "!");
            world.addFireworkParticle(nucleus.getX(), nucleus.getY(), nucleus.getZ(), 0, 0, 0, null);
        }
    }

    private static void spawnBreakParticles(World level, BlockPos position) {
        for (int i = 0; i < 360; i++) {
            double distanceFromCenter = 3;

            if (i % 20 == 0) {
                level.addParticle(
                        ParticleTypes.ELECTRIC_SPARK,
                        position.getX() + 0.5d + rand() * distanceFromCenter,
                        position.getY() + 0.5d + rand() * distanceFromCenter,
                        position.getZ() + 0.5d + rand() * distanceFromCenter,
                        Math.cos(i) * (0.1d + rand() / 3d),
                        0.1 + rand() / 2.0,
                        Math.sin(i) * (0.1d + rand() / 3d)
                );
            }

            distanceFromCenter = 3;
            if (i % 30 == 0) {
                level.addParticle(
                        ParticleTypes.FIREWORK,
                        position.getX() + 0.5d + rand() * distanceFromCenter,
                        position.getY() + 0.5d + rand() * distanceFromCenter,
                        position.getZ() + 0.5d + rand() * distanceFromCenter,
                        Math.cos(i) * (0.1d + rand() / 3d),
                        0.1 + rand() / 2.0,
                        Math.sin(i) * (0.1d + rand() / 3d)
                );
            }
        }
    }

    private static double rand() {
        Random rand = new Random();
        return (rand.nextDouble() * 2 - 1) / 3.0;
    }

}
