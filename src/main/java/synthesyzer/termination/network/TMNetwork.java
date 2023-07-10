package synthesyzer.termination.network;

import io.wispforest.owo.network.OwoNetChannel;
import io.wispforest.owo.network.serialization.PacketBufSerializer;
import net.minecraft.util.Identifier;
import synthesyzer.termination.Termination;
import synthesyzer.termination.data.team.TeamData;
import synthesyzer.termination.network.packets.servertoclient.BreakNucleusPacket;
import synthesyzer.termination.network.packets.servertoclient.PlayerKillPacket;
import synthesyzer.termination.network.packets.servertoclient.UpdateTeamDataPacket;
import synthesyzer.termination.network.packets.serializers.TeamDataReader;
import synthesyzer.termination.network.packets.serializers.TeamDataWriter;

public class TMNetwork {

    public static final OwoNetChannel CHANNEL = OwoNetChannel.create(new Identifier(Termination.MOD_ID, "main"));

    public static void register() {
        PacketBufSerializer.register(TeamData.class, new TeamDataWriter(), new TeamDataReader());
        UpdateTeamDataPacket.register(CHANNEL);
        PlayerKillPacket.register(CHANNEL);
        CHANNEL.registerClientboundDeferred(BreakNucleusPacket.class);
    }

}
