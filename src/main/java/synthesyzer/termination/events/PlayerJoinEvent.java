package synthesyzer.termination.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import synthesyzer.termination.data.team.TeamDataManager;
import synthesyzer.termination.network.TMNetwork;
import synthesyzer.termination.network.packets.servertoclient.UpdateTeamDataPacket;

public class PlayerJoinEvent {

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ServerPlayerEntity player) {
                TMNetwork.CHANNEL.serverHandle(player).send(new UpdateTeamDataPacket(TeamDataManager.get(world).getTeamData()));
            }
        });

    }

}
