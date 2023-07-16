package synthesyzer.termination.events;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.item.Item;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;
import synthesyzer.termination.Termination;
import synthesyzer.termination.data.death.DeathTracker;
import synthesyzer.termination.data.kills.MultiKill;
import synthesyzer.termination.data.kills.MultiKillManager;
import synthesyzer.termination.network.TMNetwork;
import synthesyzer.termination.network.packets.servertoclient.PlayerKillPacket;
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
                        announceKill(killer, player);
                    }
                } else {
                    world.getPlayers().forEach(p -> Messenger.sendMessage(p, "§8" + player.getDisplayName().getString() + " §7died!"));
                }

                Messenger.sendClientMessage(player, "§4You have died!");

                if (Termination.CONFIG.clearInventoryOnDeath()) {
                    clearInventory(player);
                    Messenger.sendMessage(player, "§7Some of your items have been lost. luckily tools are indestructible!");
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

        for (int i = 0; i < player.getInventory().size(); i++) {
            if (Math.random() <= chance) {
                Item item = player.getInventory().getStack(i).getItem();

                if (!item.isDamageable()) {
                    player.getInventory().removeStack(i);
                }
            }
        }
    }

    private static void announceKill(ServerPlayerEntity killer, ServerPlayerEntity killed) {
        ServerWorld world = killer.getWorld();
        MultiKill multiKill = MultiKillManager.addKill(killer.getGameProfile().getId(), ServerTickEvent.getCurrentTick());
        TMNetwork.CHANNEL.serverHandle(killer).send(new PlayerKillPacket(multiKill, killed.getGameProfile().getName()));
        String multiKillTitle = multiKill == MultiKill.SINGLE_KILL ? "" : "§7[§3" + multiKill.getTitle() + "§7] ";
        String message = multiKillTitle + "§8"+ killer.getDisplayName().getString() + "§7 has killed §8" + killed.getDisplayName().getString() + "§7!";

        world.getPlayers().forEach(player -> Messenger.sendMessage(player, message));
    }

    private static void awardKiller(ServerPlayerEntity killer) {
        killer.addExperience(Termination.CONFIG.expAwardedOnPlayerKill());
        Messenger.sendClientMessage(killer, "+" + Termination.CONFIG.expAwardedOnPlayerKill() + " EXP");
    }

}
