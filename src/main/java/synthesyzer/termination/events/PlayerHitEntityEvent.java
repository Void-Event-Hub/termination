package synthesyzer.termination.events;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import synthesyzer.termination.command.StartEventCommand;

public class PlayerHitEntityEvent {

    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (entity instanceof ServerPlayerEntity) {
                if (!StartEventCommand.startedEvent()) {
                    return ActionResult.FAIL;
                }
            }

            return ActionResult.PASS;
        });
    }

}
