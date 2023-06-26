package synthesyzer.termination.client;

import synthesyzer.termination.data.team.TeamData;

import java.util.HashMap;
import java.util.Map;

public class ClientTeamData {

    private static Map<String, TeamData> teamData = new HashMap<>();

    public static Map<String, TeamData> getTeamData() {
        return teamData;
    }

    public static TeamData getTeamData(String teamName) {
        return teamData.get(teamName);
    }

    public static void setTeamData(Map<String, TeamData> teamData) {
        ClientTeamData.teamData = teamData;
    }
}
