package synthesyzer.termination.network.packets.servertoclient;

import io.wispforest.owo.network.OwoNetChannel;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.hud.Hud;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import synthesyzer.termination.Termination;
import synthesyzer.termination.client.ClientTeamData;
import synthesyzer.termination.data.team.TeamData;

import java.util.Map;

public record UpdateTeamDataPacket(Map<String, TeamData> teamData) {

    private static int hudIteration = 0;

    public static void register(OwoNetChannel channel) {
        channel.registerClientbound(UpdateTeamDataPacket.class, ((message, access) -> {
            ClientTeamData.setTeamData(message.teamData());

            Identifier previousHudId = new Identifier(Termination.MOD_ID, "team_scoreboard_" + hudIteration);

            FlowLayout container = Containers.verticalFlow(Sizing.content(), Sizing.content());

            for (TeamData teamData : ClientTeamData.getTeamData().values()) {
                container = container.child(
                        Components.label(
                                Text.empty().append(teamData.getName())
                                        .append(" ")
                                        .append(Text.of(teamData.getHealth() + "/150"))
                                        .formatted(Formatting.WHITE)
                        ).horizontalTextAlignment(HorizontalAlignment.CENTER).shadow(true)
                );
            }

            Component finalContainer = container.surface(
                            Surface.flat(0x77000000)
                                    .and(Surface.outline(0xFF121212)))
                    .padding(Insets.of(5))
                    .positioning(Positioning.relative(100, 40));

            Hud.remove(previousHudId);
            Hud.add(new Identifier(Termination.MOD_ID, "team_scoreboard_" + ++hudIteration), () -> finalContainer);
        }));
    }

}
