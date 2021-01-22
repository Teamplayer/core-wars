package io.teamplayer.corewars.listener;

import io.teamplayer.corewars.CoreWars;
import io.teamplayer.corewars.game.GameStateManager;
import io.teamplayer.corewars.player.CorePlayer;
import io.teamplayer.teamcore.util.TeamRunnable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveListener implements Listener {

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        if (GameStateManager.getInstance().getGameStage() == GameStateManager.GameStage
                .IN_PROGRESS) {
            final CorePlayer player = CorePlayer.get(event.getPlayer());

            if (player.getState().isActive()) {
                if (player.hasCore()) player.dropCore();

                ((TeamRunnable) () -> GameStateManager.getInstance().checkForWin())
                        .runTaskLater(CoreWars.getInstance(), 1);
            }

        }
    }

}
