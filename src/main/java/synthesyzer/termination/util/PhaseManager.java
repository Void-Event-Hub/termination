package synthesyzer.termination.util;

import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import synthesyzer.termination.Termination;
import synthesyzer.termination.data.team.TeamData;
import synthesyzer.termination.data.team.TeamDataManager;
import synthesyzer.termination.network.TMNetwork;
import synthesyzer.termination.network.packets.servertoclient.UpdateTeamDataPacket;

import java.util.HashMap;
import java.util.Map;

public class PhaseManager {

    private static int ticks;

    private static boolean startedPhase2 = false;
    private static boolean startedEndPhase = false;

    private static HashMap<Integer, Boolean> phase2AnnouncedTimers = new HashMap<>();
    private static HashMap<Integer, Boolean> endPhaseAnnouncedTimers = new HashMap<>();

    static {
        phase2AnnouncedTimers.put(30, false);
        phase2AnnouncedTimers.put(15, false);
        phase2AnnouncedTimers.put(10, false);
        phase2AnnouncedTimers.put(5, false);
        phase2AnnouncedTimers.put(2, false);
        phase2AnnouncedTimers.put(1, false);

        endPhaseAnnouncedTimers.put(30, false);
        endPhaseAnnouncedTimers.put(15, false);
        endPhaseAnnouncedTimers.put(10, false);
        endPhaseAnnouncedTimers.put(5, false);
        endPhaseAnnouncedTimers.put(1, false);
    }

    public static void tick(ServerWorld world) {
        ticks++;

        announcePhase2Timers(world);
        announceEndPhaseTimers(world);

        if (!startedPhase2 && isPhase2()) {
            startedPhase2 = true;
            startPhase2(world);
        }

        if (!startedEndPhase && isEndPhase()) {
            startedEndPhase = true;
            startEndPhase(world);
        }

    }

    public static int ticksUntilPhase2() {
        return ticksUntilPhase(Termination.CONFIG.minutesUntilPhase2());
    }

    public static int ticksUntilEndPhase() {
        return ticksUntilPhase(Termination.CONFIG.minutesUntilEndPhase());
    }

    public static boolean isPhase2() {
        return isPhase(Termination.CONFIG.minutesUntilPhase2());
    }

    public static boolean isEndPhase() {
        return isPhase(Termination.CONFIG.minutesUntilEndPhase());
    }

    private static void startPhase2(ServerWorld world) {
        world.getPlayers().forEach(player -> {
            sendTitle(player, "Phase 2", "The Nucleus is now vulnerable!");
            player.playSound(SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.MASTER, 1, 1);
            Messenger.sendMessage(player, "Phase 2 has begun! You can now attack other teams' Nuclei! Be careful, though, as your Nucleus is now vulnerable as well!");
        });
    }

    private static void startEndPhase(ServerWorld world) {
        world.getPlayers().forEach(player -> {
            sendTitle(player, "End Phase", "All nuclei have fallen");
            var teamManager = TeamDataManager.get(player.world);
            for (Map.Entry<String, TeamData> entry : teamManager.getTeamData().entrySet()) {
                var team = entry.getValue();
                team.setHealth(0);
            }
            teamManager.setDirty(true);
            TMNetwork.CHANNEL.serverHandle(player.getServer()).send(new UpdateTeamDataPacket(teamManager.getTeamData()));
            player.playSound(SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.MASTER, 1, 1);
            Messenger.sendMessage(player, "The End Phase has begun! All Nuclei have fallen, meaning everyone is on their last life! Kill all remaining players to win!");
        });
    }

    private static int ticksUntilPhase(int phaseMinutes) {
        return Math.max(phaseMinutes * Time.TICKS_PER_MINUTE - ticks, 0);
    }

    private static boolean isPhase(int phaseMinutes) {
        return ticks >= phaseMinutes * Time.TICKS_PER_MINUTE;
    }

    private static void announcePhase2Timers(ServerWorld world) {
        int minutesUntilPhase2 = Termination.CONFIG.minutesUntilPhase2();

        for (Map.Entry<Integer, Boolean> entry : phase2AnnouncedTimers.entrySet()) {
            int minutes = entry.getKey();
            boolean announced = entry.getValue();

            if (minutesUntilPhase2 >= minutes) {
                if (!announced && ticksUntilPhase2() <= minutes * Time.TICKS_PER_MINUTE) {
                    phase2AnnouncedTimers.put(minutes, true);
                    world.getPlayers().forEach(player -> {
                        sendTitle(player, minutes + " minutes until Phase 2", "The Nucleus will be open to attackers in " + minutes + " minute" + (minutes == 1 ? "" : "s"));
                    });
                }
            }
        }
    }

    private static void announceEndPhaseTimers(ServerWorld world) {
        int minutesUntilEndPhase = Termination.CONFIG.minutesUntilEndPhase();

        for (Map.Entry<Integer, Boolean> entry : endPhaseAnnouncedTimers.entrySet()) {
            int minutes = entry.getKey();
            boolean announced = entry.getValue();

            if (minutesUntilEndPhase >= minutes) {
                if (!announced && ticksUntilEndPhase() <= minutes * Time.TICKS_PER_MINUTE) {
                    endPhaseAnnouncedTimers.put(minutes, true);
                    world.getPlayers().forEach(player -> {
                        sendTitle(player, minutes + " minutes until End Phase", " All nuclei will fall in " + minutes + " minute" + (minutes == 1 ? "" : "s"));
                    });
                }
            }
        }
    }

    private static void sendTitle(ServerPlayerEntity player, String title, String subtitle) {
        player.networkHandler.sendPacket(new TitleS2CPacket(Text.of(title)));
        player.networkHandler.sendPacket(new SubtitleS2CPacket(Text.of(subtitle)));
    }

    private PhaseManager() {

    }

}
