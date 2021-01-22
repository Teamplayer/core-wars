package io.teamplayer.corewars.listener;

import io.teamplayer.corewars.game.GameStateManager;
import io.teamplayer.corewars.player.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class JoinListener implements Listener {

    @EventHandler
    public void hidePlayers(PlayerJoinEvent event) {
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> !CorePlayer.get((Player) p).isVisible())
                .forEach(p -> event.getPlayer().hidePlayer(p));
    }

    @EventHandler
    public void kickPlayers(PlayerJoinEvent event) {
        if (GameStateManager.getInstance().getGameStage() ==
                GameStateManager.GameStage.IN_PROGRESS) {
            event.getPlayer().kickPlayer("Spectators Not Implemented");
        }
    }

}
