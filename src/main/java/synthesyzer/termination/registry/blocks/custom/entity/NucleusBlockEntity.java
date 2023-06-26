package synthesyzer.termination.registry.blocks.custom.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;
import synthesyzer.termination.Termination;
import synthesyzer.termination.registry.blocks.TMBlockEntities;

import java.util.Random;

public class NucleusBlockEntity extends BlockEntity {

    private int tick;

    public NucleusBlockEntity(BlockPos pos, BlockState state) {
        super(TMBlockEntities.NUCLEUS_BLOCK_ENTITY, pos, state);
        this.tick = 0;
    }

    public static void tick(World level, BlockPos blockPos, BlockState blockState, NucleusBlockEntity entity) {
        if (!level.isClient()) {
            return;
        }

        Random random = new Random();

        entity.setTick(entity.getTick() + 1);


        int duration = (int) (random.nextDouble() * 2) + 1;

        if (entity.getTick() % duration != 0) {
            return;
        }

        entity.setTick(0);

        spawnParticles(Termination.CONFIG.nucleusProtectionRadius(), level, blockPos);
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    private static void spawnParticles(int range, World world, BlockPos blockPos) {
        Random random = new Random();

        DefaultParticleType[] particles = new DefaultParticleType[]{
                ParticleTypes.END_ROD,
                ParticleTypes.END_ROD,
                ParticleTypes.FIREWORK,
        };

        // as the range increase, we need to spawn more particles relative to the surface of the cylinder
        int cylinderSurface = (int) (2 * Math.PI * Math.pow(range/10f, 2));

        for (int i = 0; i < cylinderSurface / 10f; i++) {
            for (DefaultParticleType type : particles) {

                Vec2f randomDirection2D = new Vec2f(
                        (float)(random.nextDouble() * 2) - 1,
                        (float)(random.nextDouble() * 2) - 1
                ).normalize();
                Vec2f randomOffset2D = randomDirection2D.multiply((float)getOffsetedRange(range, 1));
                double randomYPosition = random.nextGaussian(-Math.sqrt(range/2f), Math.sqrt(range/2f) * 2);
                world.addParticle(
                        type,
                        blockPos.getX() + 0.5d + randomOffset2D.x,
                        blockPos.getY() + 1.5d + randomYPosition,
                        blockPos.getZ() + 0.5d + randomOffset2D.y,
                        0,
                        -0.02f,
                        0
                );
            }
        }
    }

    private static double getOffsetedRange(double range, double stdev) {
        return range + new Random().nextGaussian(0, stdev);
    }

}
