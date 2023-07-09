package synthesyzer.termination.data.team;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Optional;

public class TeamDataManager extends PersistentState {

    private final HashMap<String, TeamData> teamData = new HashMap<>();

    public TeamDataManager() {
        super();
    }

    /**
     * Gets the team data manager from a world
     * @param world the world to get the team data manager from
     * @return the team data manager
     */
    public static TeamDataManager get(World world) {
        if (world instanceof ServerWorld serverWorld) {
            var stateManager = serverWorld.getPersistentStateManager();
            return stateManager.getOrCreate(TeamDataManager::new, TeamDataManager::new, "teamdata");
        }

        throw new RuntimeException("Can't access data from client side");
    }

    /**
     * Creates a new team data
     * @param name the name of the team, which is the same as the scoreboard team name coupled to this teamData
     */
    public void createTeamData(String name) {
        teamData.put(name, new TeamData(name));
        setDirty(true);
    }

    /**
     * Removes a team data
     * @param name the name of the team, which is the same as the scoreboard team name coupled to this teamData
     * @return true if the team data was removed, false if it wasn't
     */
    public boolean removeTeamData(String name) {
        setDirty(true);
        return teamData.remove(name) != null;
    }

    /**
     * @param name the name of the team, which is the same as the scoreboard team name coupled to this teamData
     * @return the team data of that team
     */
    public Optional<TeamData> getTeamData(String name) {
        return Optional.ofNullable(teamData.get(name));
    }

    /**
     * @param team the team to get the team data from
     * @return the team data of that team
     */
    public Optional<TeamData> getTeamData(@Nullable AbstractTeam team) {
        if (team == null) {
            return Optional.empty();
        }

        return getTeamData(team.getName());
    }

    /**
     * @return map with all team data where the key is the name of the team,
     * which is the same as the scoreboard team name coupled to this teamData
     */
    public HashMap<String, TeamData> getTeamData() {
        return teamData;
    }

    /**
     * Creates a team data manager from a saved tag containg all team data
     * @param savedTag compound tag containing all team data
     */
    public TeamDataManager(NbtCompound savedTag) {
        NbtList teamTagList = savedTag.getList("teamdata", NbtElement.COMPOUND_TYPE);
        teamTagList.forEach(tag -> {
            TeamData team = new TeamData((NbtCompound) tag);
            teamData.put(team.getName(), team);
        });
    }

    /**
     * Writes all team data to a compound tag
     * @param nbt the compound tag to write the team data to
     * @return the compound tag containing all team data
     */
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList teamTagList = new NbtList();
        teamData.values().forEach(team -> teamTagList.add(team.toNbt()));

        nbt.put("teamdata", teamTagList);
        return nbt;
    }

    /**
     * @param pos the position of the nucleus
     * @return the team data of the team that has a nucleus at the given position
     */
    public Optional<TeamData> getTeamDataByNucleus(BlockPos pos) {
        return teamData.values().stream()
                .filter(civilization -> civilization.getNucleus().filter(pos::equals).isPresent())
                .findFirst();
    }

    /**
     *
     * @param pos the position of the nucleus
     * @param range the range in which the nucleus should be
     * @return the position of the nucleus of the team that has a nucleus in a certain area
     */
    public Optional<BlockPos> getNucleusInRange(BlockPos pos, float range) {
        return teamData.values()
                .stream()
                .map(TeamData::getNucleus)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(nucleus -> planeDistance(pos, nucleus) < range)
                .findFirst();
    }

    /**
     * Calculates the distance between two positions in the x and z plane
     */
    private float planeDistance(BlockPos a, BlockPos b) {
        return (float) Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getZ() - b.getZ(), 2));
    }
}
