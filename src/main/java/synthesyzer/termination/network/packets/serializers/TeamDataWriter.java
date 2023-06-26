package synthesyzer.termination.network.packets.serializers;

import net.minecraft.network.PacketByteBuf;
import synthesyzer.termination.data.team.TeamData;

public class TeamDataWriter implements PacketByteBuf.PacketWriter<TeamData> {

    @Override
    public void accept(PacketByteBuf packetByteBuf, TeamData teamData) {
        packetByteBuf.writeString(teamData.getName());
        packetByteBuf.writeInt(teamData.getHealth());
        if (teamData.getSpawn().isPresent()) {
            packetByteBuf.writeBoolean(true);
            packetByteBuf.writeBlockPos(teamData.getSpawn().get());
        } else {
            packetByteBuf.writeBoolean(false);
        }

        if (teamData.getNucleus().isPresent()) {
            packetByteBuf.writeBoolean(true);
            packetByteBuf.writeBlockPos(teamData.getNucleus().get());
        } else {
            packetByteBuf.writeBoolean(false);
        }
    }
}
