package synthesyzer.termination.network.packets.servertoclient;

import synthesyzer.termination.data.team.TeamData;

public record BreakNucleusPacket(TeamData attackingTeam, TeamData attackedTeam) {
}
