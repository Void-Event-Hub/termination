package synthesyzer.termination.data.team;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Optional;

public class TeamDataManager extends PersistentState {

    private final HashMap<String, TeamData> teamData = new HashMap<>();

    public TeamDataManager() {
        super();
    }

    public static TeamDataManager get(World world) {
        if (world instanceof ServerWorld serverWorld) {
            var stateManager = serverWorld.getPersistentStateManager();
            return stateManager.getOrCreate(TeamDataManager::new, TeamDataManager::new, "teamdata");
        }

        throw new RuntimeException("Can't access data from client side");
    }

    public void createTeamData(String name) {
        teamData.put(name, new TeamData(name));
        setDirty(true);
    }

    public boolean removeTeamData(String name) {
        setDirty(true);
        return teamData.remove(name) != null;
    }

    public Optional<TeamData> getTeamData(String name) {
        return Optional.ofNullable(teamData.get(name));
    }

    public HashMap<String, TeamData> getTeamData() {
        return teamData;
    }

    public TeamDataManager(NbtCompound savedTag) {
        NbtList teamTagList = savedTag.getList("teamdata", NbtElement.COMPOUND_TYPE);
        teamTagList.forEach(tag -> {
            TeamData team = new TeamData((NbtCompound) tag);
            teamData.put(team.getName(), team);
        });
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList teamTagList = new NbtList();
        teamData.values().forEach(team -> teamTagList.add(team.toNbt()));

        nbt.put("teamdata", teamTagList);
        return nbt;
    }

    public Optional<TeamData> getTeamDataByNucleus(BlockPos pos) {
        return teamData.values().stream()
                .filter(civilization -> civilization.getNucleus().filter(pos::equals).isPresent())
                .findFirst();
    }


    public Optional<BlockPos> getNucleusInRange(BlockPos pos, float range) {
        return teamData.values()
                .stream()
                .map(TeamData::getNucleus)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(nucleus -> planeDistance(pos, nucleus) < range)
                .findFirst();
    }

    private float planeDistance(BlockPos a, BlockPos b) {
        return (float) Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getZ() - b.getZ(), 2));
    }
}
