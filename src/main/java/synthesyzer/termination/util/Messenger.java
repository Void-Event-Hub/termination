package synthesyzer.termination.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class Messenger {

    private Messenger(){
    }

    public static void sendError(PlayerEntity player, String message) {
        sendMessage(player, message);
    }

    public static void sendSuccess(PlayerEntity player, String message) {
        sendMessage(player, message);
    }

    public static void sendMessage(PlayerEntity player, String message) {
        player.sendMessage(Text.literal("").append(Text.of(message)));
    }

    public static void sendClientMessage(PlayerEntity player, String message) {
        player.sendMessage(Text.literal("").append(Text.of(message)), true);
    }

    public static void sendMessageToEveryoneExcept(PlayerEntity player, String message) {
        player.getWorld()
                .getPlayers()
                .forEach(p -> {
                    if (p != player) {
                        sendMessage(p, message);
                    }
                });
    }



}
