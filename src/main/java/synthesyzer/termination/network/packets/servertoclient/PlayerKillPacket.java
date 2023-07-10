package synthesyzer.termination.network.packets.servertoclient;

import io.wispforest.owo.network.OwoNetChannel;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.hud.Hud;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import synthesyzer.termination.Termination;
import synthesyzer.termination.client.ClientMultiKillData;
import synthesyzer.termination.client.ClientTickEvent;
import synthesyzer.termination.data.kills.MultiKill;

public record PlayerKillPacket(MultiKill multiKill, String victimName) {
    public static void register(OwoNetChannel channel) {
        channel.registerClientbound(PlayerKillPacket.class, ((message, access) -> {
            MultiKill multiKill = message.multiKill();

            System.out.println("multiKill = " + multiKill);

            if (multiKill == MultiKill.SINGLE_KILL) {
                return;
            }

            ClientMultiKillData.setTickOfMultiKill(ClientTickEvent.getCurrentTick());

            Hud.add(new Identifier(Termination.MOD_ID, "multi_kill"), () ->
                    Containers.verticalFlow(Sizing.content(), Sizing.content())
                            .child(
                                    Components.label(
                                                    Text.empty()
                                                            .append(Text.literal(multiKill.getTitle())
                                                                    .formatted(getMultiKillFormatting(multiKill), Formatting.BOLD))
                                            )
                                            .horizontalTextAlignment(HorizontalAlignment.CENTER).shadow(true)
                            )
                            .child(
                                    Components.label(
                                            Text.empty()
                                                    .append("You killed " + message.victimName() + "!").formatted(Formatting.WHITE)
                                    )
                                            .horizontalTextAlignment(HorizontalAlignment.CENTER).shadow(true)
                            )
                            .surface(Surface.flat(0x77000000).and(Surface.outline(0xFF121212)))
                            .padding(Insets.of(5))
                            .positioning(Positioning.relative(50, 5))
            );

        }));
    }

    private static Formatting getMultiKillFormatting(MultiKill multiKill) {
        switch (multiKill) {
            case DOUBLE_KILL -> {
                return Formatting.YELLOW;
            }
            case TRIPLE_KILL -> {
                return Formatting.RED;
            }
            case QUADRA_KILL -> {
                return Formatting.DARK_RED;
            }
            case PENTA_KILL -> {
                return Formatting.DARK_PURPLE;
            }
            default -> {
                return Formatting.WHITE;
            }
        }
    }
}
