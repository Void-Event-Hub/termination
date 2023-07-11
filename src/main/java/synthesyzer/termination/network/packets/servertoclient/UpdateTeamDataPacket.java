package synthesyzer.termination.network.packets.servertoclient;

import synthesyzer.termination.data.team.TeamData;

import java.util.Map;

public record UpdateTeamDataPacket(Map<String, TeamData> teamData) {

}
