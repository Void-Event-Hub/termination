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

    /**
     * Gets the death tracker from a world
     */
    public static DeathTracker get(World world) {
        if (world instanceof ServerWorld serverWorld) {
            var stateManager = serverWorld.getPersistentStateManager();
            return stateManager.getOrCreate(DeathTracker::new, DeathTracker::new, "deathtracker");
        }

        throw new RuntimeException("Can't access data from client side");
    }

    /**
     * Creates a death tracker from a saved tag
     */
    public DeathTracker(NbtCompound savedTag) {
        NbtList deathTimersTagList = savedTag.getList("deathTimers", NbtElement.COMPOUND_TYPE);

        deathTimersTagList.forEach(tag -> {
            NbtCompound compoundTag = (NbtCompound) tag;
            UUID id = compoundTag.getUuid("uuid");
            int remainingTicks = compoundTag.getInt("remainingTicks");
            deathTimers.put(id, remainingTicks);
        });
    }

    /**
     * Writes the death tracker to a compunt tag
     * @param nbt the tag to write to
     * @return the tag with all death timers
     */
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

    /**
     * Updates the death timers of all currently dead players
     */
    public void update() {
        deathTimers.forEach((gameProfile, remainingTicks) -> {
            if (remainingTicks > 0) {
                deathTimers.put(gameProfile, remainingTicks - 1);
            }
        });
        setDirty(true);
    }

    /**
     * Adds a player to the death tracker
     * @param playerId the id of the player to add
     */
    public void addPlayerDeath(UUID playerId) {
        final int ticksPerSecond = 20;
        deathTimers.put(playerId, Termination.CONFIG.playerDeathCooldown() * ticksPerSecond);
        setDirty(true);
    }

    /**
     * Removes a player from the death tracker, meaning they are no longer dead
     * @param playerId the id of the player to remove
     */
    public void removePlayerDeath(UUID playerId) {
        deathTimers.remove(playerId);
        setDirty(true);
    }

    /**
     * Gets the death timers
     * @return map of death timers, with the key being the player id and the value being the remaining ticks
     */
    public HashMap<UUID, Integer> getDeathTimers() {
        return deathTimers;
    }

    /**
     * Gets all players that are currently dead and their death timer is 0
     * @return list of player ids
     */
    public List<UUID> getPlayersToBeRevived() {
        return deathTimers.entrySet().stream()
                .filter(entry -> entry.getValue() <= 0)
                .map(HashMap.Entry::getKey)
                .toList();
    }

    /**
     * Checks if a player is dead
     * @param id the id of the player to check
     * @return true if the player is dead, false otherwise
     */
    public boolean isPlayerDead(UUID id) {
        return deathTimers.containsKey(id) && deathTimers.get(id) > 0;
    }

    /**
     * Gets the remaining ticks of a player's death timer
     * @param id the id of the player to check
     * @return the remaining ticks of the player's death timer
     */
    public int getDeathTimer(UUID id) {
        return deathTimers.get(id);
    }
}
