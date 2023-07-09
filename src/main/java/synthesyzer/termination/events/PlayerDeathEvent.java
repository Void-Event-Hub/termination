package synthesyzer.termination.events;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;
import synthesyzer.termination.Termination;
import synthesyzer.termination.data.death.DeathTracker;
import synthesyzer.termination.util.Messenger;

public class PlayerDeathEvent {

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) -> {
            if (entity instanceof ServerPlayerEntity player) {
                ServerWorld world = player.getWorld();
                AbstractTeam team = player.getScoreboardTeam();

                if (team == null) {
                    return true;
                }

                if (source.getSource() instanceof ServerPlayerEntity killer) {
                    if (killer.getScoreboardTeam() != team) {
                        awardKiller(killer);
                    }
                }

                Messenger.sendClientMessage(player, "You have died!");

                if (Termination.CONFIG.clearInventoryOnDeath()) {
                    clearInventory(player);
                    Messenger.sendMessage(player, "Some of your items have been lost, and your tools have taken a beating.");
                }

                if (Termination.CONFIG.playerDeathCooldown() > 0) {
                    DeathTracker.get(world).addPlayerDeath(player.getGameProfile().getId());
                }

                player.changeGameMode(GameMode.SPECTATOR);
                player.setHealth(player.getMaxHealth());
                player.getHungerManager().setFoodLevel(20);

                return false;
            }
            return true;
        });
    }

    private static void clearInventory(ServerPlayerEntity player) {
        double chance = Termination.CONFIG.chanceToDropItemOnDeath();

        if (chance <= 0) {
            return;
        }


//        player.inventoryMenu.slots.forEach(slot -> {
//            if (!slot.hasItem()) {
//                return;
//            }
//
//            ItemStack item = slot.getItem();
//
//            if (item.isDamageableItem()) {
//                int damage = (int) (item.getMaxDamage() * VoidCivilization.config.damageDealtToToolsOnDeath);
//                item.hurtAndBreak(damage, player, (playerEntity) -> {
//                });
//            } else if (Math.random() <= chance) {
//                slot.set(ItemStack.EMPTY);
//            }
//        });
    }

    private static void awardKiller(ServerPlayerEntity killer) {
        killer.addExperience(Termination.CONFIG.expAwardedOnPlayerKill());
        Messenger.sendClientMessage(killer, "+" + Termination.CONFIG.expAwardedOnPlayerKill() + " EXP");
    }

}
