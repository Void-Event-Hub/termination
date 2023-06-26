package synthesyzer.termination.network.packets.serializers;

import net.minecraft.network.PacketByteBuf;
import synthesyzer.termination.data.team.TeamData;

public class TeamDataReader implements PacketByteBuf.PacketReader<TeamData> {
    @Override
    public TeamData apply(PacketByteBuf packetByteBuf) {
        String name = packetByteBuf.readString();
        int health = packetByteBuf.readInt();
        boolean hasSpawn = packetByteBuf.readBoolean();
        boolean hasNucleus = packetByteBuf.readBoolean();

        TeamData teamData = new TeamData(name);
        teamData.setHealth(health);
        if (hasSpawn) {
            teamData.setSpawn(packetByteBuf.readBlockPos());
        }
        if (hasNucleus) {
            teamData.setNucleus(packetByteBuf.readBlockPos());
        }

        return teamData;
    }
}
