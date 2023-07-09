package synthesyzer.termination.data.kills;

import synthesyzer.termination.util.Time;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MultiKillManager {

    private static HashMap<UUID, List<Integer>> killTicks = new HashMap<>();

    /**
     * Adds a kill to the player's kill list.
     * @param uuid The player's UUID
     * @param tick The tick the kill occurred on
     * @return The MultiKill the player is on
     */
    public static MultiKill addKill(UUID uuid, int tick) {
        if (killTicks.containsKey(uuid)) {
            killTicks.get(uuid).add(tick);
        } else {
            ArrayList<Integer> list = new ArrayList<>();
            list.add(tick);
            killTicks.put(uuid, list);
        }

        return getMultiKill(uuid);
    }

    private static MultiKill getMultiKill(UUID uuid) {
        List<Integer> playerKills = killTicks.get(uuid);

        int totalKillsWithinTimeFrame = 0;

        for (int i = (playerKills.size() - 1); i > 0; i--) {
            int killTick = playerKills.get(i);
            int previousKillTick = playerKills.get(i - 1);

            if (isWithinTimeFrame(killTick, previousKillTick)) {
                totalKillsWithinTimeFrame++;
            } else {
                break;
            }
        }

        return MultiKill.values()[Math.min(totalKillsWithinTimeFrame, MultiKill.values().length - 1)];
    }

    private static boolean isWithinTimeFrame(int tick, int previousTick) {
        return (tick - previousTick) < (Time.TICKS_PER_SECOND * 10);
    }

    private MultiKillManager() {

    }

}
