package synthesyzer.termination.network.packets.servertoclient;

import io.wispforest.owo.network.OwoNetChannel;
import synthesyzer.termination.client.ClientTeamData;
import synthesyzer.termination.data.team.TeamData;

import java.util.Map;

public record UpdateTeamDataPacket(Map<String, TeamData> teamData) {

    public static void register(OwoNetChannel channel) {
        channel.registerClientbound(UpdateTeamDataPacket.class, ((message, access) -> ClientTeamData.setTeamData(message.teamData())));
    }

}
