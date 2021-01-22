package io.teamplayer.corewars.player;

import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerEntityStatus;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import io.teamplayer.corewars.CoreWars;
import io.teamplayer.corewars.game.GameStateManager;
import io.teamplayer.corewars.util.task.PlayerTaskTimer;
import io.teamplayer.teamcore.util.TeamRunnable;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

/**
 * Monitors all damage done and prevents actual Minecraft death and instead replaces it with this
 * psudodeath
 */
public class DeathHandler implements Listener {

    private final Map<Player, DamageInfo> lastDamages = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            final Player damager = ((Player) event.getDamager());
            final Player damaged = ((Player) event.getEntity());

            lastDamages.put(damaged, new DamageInfo(damager, System.currentTimeMillis()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player) || event.isCancelled()) {
            return;
        }

        final Player player = ((Player) event.getEntity());

        if (player.getHealth() - event.getFinalDamage() <= 0) {
            event.setCancelled(true);

            pseudoKill(CorePlayer.get(player));
        }

    }

    private void pseudoKill(CorePlayer player) {
        final DamageInfo lastDamage = lastDamages.getOrDefault(player.getPlayer(),
                new DamageInfo(null, 0L));
        final boolean killed = lastDamage.time + 2000 > System.currentTimeMillis();

        killEffect(player);

        if (player.hasCore()) player.dropCore();

        player.getStats().incStat(StatType.DEATHS);

        if (killed) {
            CorePlayer.get(lastDamage.getDamager()).getStats().incStat(StatType.KILLS);
        }

        if (player.getTeam().canRespawn()) {
            player.setState(CorePlayer.PlayerState.DEAD);
            player.getPlayer().getInventory().clear();

            new RespawnTask(player, 12 - (2 * player.getTeam().getAmountOfActiveCores()));
        } else {
            player.setState(CorePlayer.PlayerState.ELIMINATED);

            player.getPlayer().sendMessage("You cannot respawn as your team has no more " +
                    "RespawnCores");

            if (killed) {
                CorePlayer.get(lastDamage.getDamager()).getStats().incStat(StatType.FINAL_KILLS);
            }

            GameStateManager.getInstance().checkForWin();
        }

    }

    private void killEffect(CorePlayer player) {
        final Player realPlayer = player.getPlayer();
        final long freezeTime = 20;

        realPlayer.addPotionEffect(
                new PotionEffect(PotionEffectType.BLINDNESS, (int) freezeTime * 2, 0));
        realPlayer.playSound(realPlayer.getLocation(), Sound.ENTITY_PLAYER_DEATH, 1.4F, .8F);

        player.freezePlayer(); //We don't want the player to move during the death animation
        playFakeDeathAnimation(realPlayer);

        ((TeamRunnable) () -> {
            player.hidePlayer();
            player.unfreezePlayer();

            realPlayer.setAllowFlight(true);
            realPlayer.setFlying(true);
            realPlayer.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,
                    100000, 0));
        }).runTaskLater(CoreWars.getInstance(), freezeTime);

    }

    /**
     * Plays the little red falling over animation on the player for everyone but the player
     **/
    private void playFakeDeathAnimation(Player player) {
        WrapperPlayServerEntityStatus damagePacket = new WrapperPlayServerEntityStatus();

        damagePacket.setEntityID(player.getEntityId());
        damagePacket.setEntityStatus((byte) 2); //Status 2 is damage

        //Making the client think an entity's health is 0 automatically plays death animation
        WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata();
        WrappedDataWatcher metadata = new WrappedDataWatcher();
        metadata.setObject(8, WrappedDataWatcher.Registry.get(Float.class), ((float) 0));
        metadataPacket.setEntityID(player.getEntityId());
        metadataPacket.setMetadata(metadata.getWatchableObjects());

        Bukkit.getOnlinePlayers().stream()
                .filter(p -> !p.equals(player.getPlayer()))
                .forEach(metadataPacket::sendPacket);
        damagePacket.sendPacket(player);
    }

    private void respawnPlayer(CorePlayer player) {
        final Location location = player.getTeam().getSpawn();

        player.setState(CorePlayer.PlayerState.ALIVE);

        player.giveItems();

        player.getPlayer().setHealth(player.getPlayer().getMaxHealth());
        player.getPlayer().teleport(location);

        respawnEffect(player, location);
    }

    private void respawnEffect(CorePlayer player, Location respawnLocation) {
        player.showPlayer();

        player.getPlayer().setAllowFlight(false);
        player.getPlayer().setFlying(false);

        player.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);

        respawnLocation.getWorld().spawnParticle(Particle.CLOUD, respawnLocation, 50);
        respawnLocation.getWorld().playSound(respawnLocation, Sound.ENTITY_CHICKEN_EGG, .7F, 1F);
    }

    private class RespawnTask extends PlayerTaskTimer {

        RespawnTask(CorePlayer player, int duration) {
            super(player, duration, "Respawning", ChatColor.GRAY, false);
        }

        @Override
        protected void finish() {
            respawnPlayer(player);
        }
    }

    private static class DamageInfo {

        private final Player damager;
        private final long time;

        DamageInfo(Player damager, long time) {
            this.damager = damager;
            this.time = time;
        }

        Player getDamager() {
            return damager;
        }

        long getTime() {
            return time;
        }
    }

}
