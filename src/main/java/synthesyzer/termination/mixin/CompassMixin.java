package synthesyzer.termination.mixin;

import net.minecraft.client.item.CompassAnglePredicateProvider;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CompassAnglePredicateProvider.class)
public abstract class CompassMixin {

    @Shadow protected abstract float getAngleTo(Entity entity, long time, BlockPos pos);

    @Inject(method = "getAngle", at = @At("TAIL"), cancellable = true)
    public void getAngle(ItemStack stack, ClientWorld world, int seed, Entity entity, CallbackInfoReturnable<Float> cir) {
        var ourCoord = entity.getPos();

        if (entity.getScoreboardTeam() == null) {
            return;
        }

        AbstractClientPlayerEntity closestPlayer = null;

        if (world.getPlayers().size() <= 1) {
            cir.setReturnValue(0.0F);
            return;
        }

        for(AbstractClientPlayerEntity player : world.getPlayers()) {
            if (entity.equals(player)) {
                continue;
            }

            if (player.getScoreboardTeam() == null) {
                continue;
            }

            if (player.isCreative() || player.isSpectator()) {
                continue;
            }

            if (entity.getScoreboardTeam().equals(player.getScoreboardTeam())) {
                continue;
            }

            if (closestPlayer == null) {
                closestPlayer = player;
            }

            if (ourCoord.distanceTo(player.getPos()) < ourCoord.distanceTo(closestPlayer.getPos())) {
                closestPlayer = player;
            }
        }

        if (closestPlayer == null) {
            cir.setReturnValue(0.0F);
            return;
        }

        float angle = getAngleTo(entity, world.getTime(),closestPlayer.getBlockPos());

        cir.setReturnValue(angle);
    }

}
