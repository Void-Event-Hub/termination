package synthesyzer.termination.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import synthesyzer.termination.data.team.TeamDataManager;
import synthesyzer.termination.network.TMNetwork;
import synthesyzer.termination.network.packets.servertoclient.UpdateTeamDataPacket;
import synthesyzer.termination.util.Messenger;

public class PlayerJoinEvent {

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ServerPlayerEntity player) {
                TMNetwork.CHANNEL.serverHandle(player).send(new UpdateTeamDataPacket(TeamDataManager.get(world).getTeamData()));
                Messenger.sendMessage(player, """

                        §7Termination's 3 Phases:\s
                        §eStart Phase§7: You're free to roam anywhere, but you can't destroy the enemy nucleus.\s

                        §6Phase 2§7: You can now destroy the enemy nucleus.

                        §eEnd Phase§7: All nuclei die, meaning you have no respawns.\s

                        §8Destroy the enemy nucleus and kill all players to win!
                        """);
            }
        });

    }

}
