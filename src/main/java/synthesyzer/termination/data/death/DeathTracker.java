package synthesyzer.termination.data.death;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import synthesyzer.termination.Termination;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DeathTracker extends PersistentState {

    private final HashMap<UUID, Integer> deathTimers = new HashMap<>();

    public DeathTracker() {
        super();
    }

    public static DeathTracker get(World world) {
        if (world instanceof ServerWorld serverWorld) {
            var stateManager = serverWorld.getPersistentStateManager();
            return stateManager.getOrCreate(DeathTracker::new, DeathTracker::new, "deathtracker");
        }

        throw new RuntimeException("Can't access data from client side");
    }

    public DeathTracker(NbtCompound savedTag) {
        NbtList deathTimersTagList = savedTag.getList("deathTimers", NbtElement.COMPOUND_TYPE);

        deathTimersTagList.forEach(tag -> {
            NbtCompound compoundTag = (NbtCompound) tag;
            UUID id = compoundTag.getUuid("uuid");
            int remainingTicks = compoundTag.getInt("remainingTicks");
            deathTimers.put(id, remainingTicks);
        });
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList listTag = new NbtList();

        deathTimers.forEach((uuid, remainingTicks) -> {
            NbtCompound tag = new NbtCompound();
            tag.putUuid("uuid", uuid);
            tag.putInt("remainingTicks", remainingTicks);
            listTag.add(tag);
        });

        nbt.put("deathTimers", listTag);
        return nbt;
    }

    public void update() {
        deathTimers.forEach((gameProfile, remainingTicks) -> {
            if (remainingTicks > 0) {
                deathTimers.put(gameProfile, remainingTicks - 1);
            }
        });
        setDirty(true);
    }

    public void addPlayerDeath(UUID playerId) {
        final int ticksPerSecond = 20;
        deathTimers.put(playerId, Termination.CONFIG.playerDeathCooldown() * ticksPerSecond);
        setDirty(true);
    }

    public void removePlayerDeath(UUID playerId) {
        deathTimers.remove(playerId);
        setDirty(true);
    }

    public HashMap<UUID, Integer> getDeathTimers() {
        return deathTimers;
    }

    public List<UUID> getPlayersToBeRevived() {
        return deathTimers.entrySet().stream()
                .filter(entry -> entry.getValue() <= 0)
                .map(HashMap.Entry::getKey)
                .toList();
    }

    public boolean isPlayerDead(UUID id) {
        return deathTimers.containsKey(id) && deathTimers.get(id) > 0;
    }

    public int getDeathTimer(UUID id) {
        return deathTimers.get(id);
    }
}
