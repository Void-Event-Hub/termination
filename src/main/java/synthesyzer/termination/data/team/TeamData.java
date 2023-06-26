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

    public boolean isDead() {
        return health <= 0;
    }

    public String getName() {
        return name;
    }

    public void setSpawn(BlockPos blockPosition) {
        this.spawn = blockPosition;
    }

    public Optional<BlockPos> getSpawn() {
        return Optional.ofNullable(spawn);
    }

    public void setNucleus(BlockPos pos) {
        this.nucleus = pos;
    }

    public Optional<BlockPos> getNucleus() {
        return Optional.ofNullable(nucleus);
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void damage(int damage) {
        this.health -= damage;

        if (this.health < 0) {
            this.health = 0;
        }
    }

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
