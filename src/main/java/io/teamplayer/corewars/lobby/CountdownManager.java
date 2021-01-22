package io.teamplayer.corewars.lobby;

import io.teamplayer.corewars.CoreWars;
import io.teamplayer.corewars.game.GameStateManager;
import io.teamplayer.corewars.player.CorePlayer;
import io.teamplayer.corewars.player.PlayerGetter;
import io.teamplayer.corewars.scoreboard.LobbyFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class CountdownManager implements Listener {

    private StartingCountdown countdown;

    public boolean isRunning() {
        return countdown != null && countdown.isRunning();
    }

    public StartingCountdown getCountdown() {
        return countdown;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().teleport(event.getPlayer().getWorld().getSpawnLocation());

        CoreWars.getInstance().getScoreboardManager().updateLine(LobbyFrame.NEEDED_NAME);

        if (PlayerGetter.getActivePlayers().size() >= CoreWars.MAX_COUNT) {
            event.getPlayer().kickPlayer("This game is full");
        } else {
            checkPlayers();
            CorePlayer.get(event.getPlayer());
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        //The player count check won't be properly updated at this point so wait a tick to do the player count check
        new BukkitRunnable() {
            @Override
            public void run() {
                checkPlayers();
            }
        }.runTaskLater(CoreWars.getInstance(), 1);
    }

    private void checkPlayers() {
        if (GameStateManager.getInstance().getGameStage() != GameStateManager.GameStage.LOBBY) {
            return;
        }

        final short playerCount = (short) PlayerGetter.getActivePlayers().size();

        if (playerCount >= CoreWars.MIN_COUNT) {
            if (countdown == null || !countdown.isRunning()) {
                countdown = new StartingCountdown();
            }
        } else {
            if (countdown != null)
                countdown.cancel();
        }
    }

}
