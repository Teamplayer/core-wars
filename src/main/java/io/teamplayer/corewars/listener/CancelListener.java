package io.teamplayer.corewars.listener;

import io.teamplayer.corewars.game.GameStateManager;
import io.teamplayer.corewars.player.CorePlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;

public final class CancelListener implements Listener {

    @EventHandler
    public void cancelBuilding(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void cancelBreaking(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void cancelDamage(EntityDamageEvent event) {
        if (!GameStateManager.getInstance().getGameStage().equals(GameStateManager.GameStage.IN_PROGRESS)
                || !canInteract(event.getEntity())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void cancelSpecDamage(EntityDamageByEntityEvent event) {
        if (!canInteract(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void cancelSpecInteract(EntityInteractEvent event) {
        if (!canInteract(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void cancelInteractWithArmorStands(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof ArmorStand) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void cancelItemMovement(InventoryDragEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void cancelFoodLoss(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void cancelItemDamage(PlayerItemDamageEvent event) {
        event.setCancelled(true);
    }

    private boolean canInteract(Entity entity) {
        return entity instanceof Player &&
                CorePlayer.get(((Player) entity)).getState().canInteract();
    }
}
