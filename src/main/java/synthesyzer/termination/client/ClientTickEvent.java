package synthesyzer.termination.client;

import io.wispforest.owo.ui.hud.Hud;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.util.Identifier;
import synthesyzer.termination.Termination;
import synthesyzer.termination.util.Time;

public class ClientTickEvent {

    private static int tick;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            tick++;

            // otherwise first multi kill will removed immediately
            if (ClientMultiKillData.getTickOfMultiKill() == 0) {
                return;
            }

            if ((tick - ClientMultiKillData.getTickOfMultiKill()) >= (Time.TICKS_PER_SECOND * 4)) {
                Hud.remove(new Identifier(Termination.MOD_ID, "multi_kill"));
            }
        });
    }

    public static int getCurrentTick() {
        return tick;
    }

}
