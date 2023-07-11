package synthesyzer.termination.client;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.hud.Hud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import synthesyzer.termination.Termination;
import synthesyzer.termination.client.ui.TeamScoreboard;
import synthesyzer.termination.data.team.TeamData;
import synthesyzer.termination.network.TMNetwork;
import synthesyzer.termination.network.packets.servertoclient.BreakNucleusPacket;
import synthesyzer.termination.network.packets.servertoclient.UpdateTeamDataPacket;
import synthesyzer.termination.util.Messenger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TerminationClient implements ClientModInitializer {

    private static int hudIteration = 0;

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(TeamScoreboard::render);
        ClientTickEvent.register();

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
                Messenger.sendClientMessage(player, "Your civilization is under attack! " + "( " + attackedTeam.getHealth() + " )");
            }

            if (playerTeam.getName().equals(attackingTeam.getName())) {
                player.playSound(SoundEvents.BLOCK_AMETHYST_CLUSTER_BREAK, 1.0F, 1.0F);
                Messenger.sendClientMessage(player, "You are attacking " + attackedTeam.getName() + " ! " + "( " + attackedTeam.getHealth() + " )");
            }
        }));
        TMNetwork.CHANNEL.registerClientbound(UpdateTeamDataPacket.class, ((message, access) -> {
            ClientTeamData.setTeamData(message.teamData());
            Identifier previousHudId = new Identifier(Termination.MOD_ID, "team_scoreboard_" + hudIteration);

            MinecraftClient client = MinecraftClient.getInstance();
            PlayerEntity player = client.player;

            if (player == null) {
                return;
            }


            FlowLayout container = Containers.verticalFlow(Sizing.content(), Sizing.content());

            List<TeamData> teamDatas = new ArrayList<>(ClientTeamData.getTeamData().values());
            teamDatas.sort((o1, o2) -> Integer.compare(o2.getHealth(), o1.getHealth()));

            for (TeamData teamData : teamDatas) {
                AbstractTeam team = player.getScoreboard().getTeam(teamData.getName());

                Formatting color = team != null ? team.getColor() : Formatting.WHITE;

                if (teamData.isDead()) {
                    color = Formatting.DARK_GRAY;
                }

                container = container.child(
                        Components.label(
                                Text.empty().append(teamData.getName())
                                        .append(" ")
                                        .append(Text.of(teamData.getHealth() + "/150"))
                                        .formatted(color != null ? color : Formatting.WHITE)
                        ).horizontalTextAlignment(HorizontalAlignment.CENTER).shadow(true)
                );
            }

            Component finalContainer = container.surface(
                            Surface.flat(0x77000000)
                                    .and(Surface.outline(0xFF121212)))
                    .padding(Insets.of(5))
                    .positioning(Positioning.relative(100, 40));

            Hud.remove(previousHudId);
            Hud.add(new Identifier(Termination.MOD_ID, "team_scoreboard_" + ++hudIteration), () -> finalContainer);
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
