package synthesyzer.termination.data.team;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import synthesyzer.termination.Termination;

import java.util.Optional;

public class TeamData {

    private final String name;
    private int health;
    private BlockPos spawn;
    private BlockPos nucleus;

    public TeamData(String name) {
        this.name = name;
        this.health = Termination.CONFIG.maxNucleusHealth();
    }

    /**
     * Creates a team data from a nbt compound
     * @param nbt the nbt compound containing the team data
     */
    public TeamData(NbtCompound nbt) {
        this.name = nbt.getString("name");
        this.health = nbt.getInt("health");
        if (nbt.getBoolean("hasSpawn")) {
            this.spawn = BlockPos.fromLong(nbt.getLong("spawn"));
        }

        if (nbt.getBoolean("hasNucleus")) {
            this.nucleus = BlockPos.fromLong(nbt.getLong("nucleus"));
        }
    }

    /**
     * Checks if a team is dead
     */
    public boolean isDead() {
        return health <= 0;
    }

    /**
     * @return The name of the team, which is the same as the scoreboard team name coupled to this teamData
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the spawn of the team
     * @param blockPosition the position of the spawn
     */
    public void setSpawn(BlockPos blockPosition) {
        this.spawn = blockPosition;
    }

    /**
     * @return The set spawn of the team
     */
    public Optional<BlockPos> getSpawn() {
        return Optional.ofNullable(spawn);
    }

    /**
     * Sets the nucleus of the team
     * @param pos position of the nucleus block
     */
    public void setNucleus(BlockPos pos) {
        this.nucleus = pos;
    }

    /**
     * @return The set nucleus of the team
     */
    public Optional<BlockPos> getNucleus() {
        return Optional.ofNullable(nucleus);
    }

    /**
     * @return The health of the nucleus
     */
    public int getHealth() {
        return health;
    }

    /**
     * Sets the health of the nucleus
     */
    public void setHealth(int health) {
        this.health = health;
    }

    /**
     * Damages the nucleus
     * @param damage amount of health lost
     */
    public void damage(int damage) {
        this.health -= damage;

        if (this.health < 0) {
            this.health = 0;
        }
    }

    /**
     * Saves the team data to a nbt compound
     * @return nbt compound with the team data
     */
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("name", name);
        nbt.putInt("health", health);

        boolean hasSpawn = spawn != null;

        nbt.putBoolean("hasSpawn", hasSpawn);
        if (hasSpawn) {
            nbt.putLong("spawn", spawn.asLong());
        }

        boolean hasNucleus = nucleus != null;
        nbt.putBoolean("hasNucleus", hasNucleus);
        if (hasNucleus) {
            nbt.putLong("nucleus", nucleus.asLong());
        }

        return nbt;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TeamData other) {
            return this.name.equals(other.name);
        }

        return false;
    }
}
