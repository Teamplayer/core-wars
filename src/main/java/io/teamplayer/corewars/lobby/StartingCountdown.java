package io.teamplayer.corewars.lobby;

import io.teamplayer.corewars.game.GameStateManager;
import io.teamplayer.corewars.util.task.TaskTimer;
import io.teamplayer.teamcore.message.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

public class StartingCountdown extends TaskTimer {

    public StartingCountdown() {
        super(10, 20);
    }

    @Override
    protected void ticked() {
        final int secondsLeft = getSecondsRemaining();

        if (secondsLeft == 5) {
            GameStateManager.getInstance().loadMap();
        }

        if ((secondsLeft % 10 == 0 || secondsLeft <= 5) && secondsLeft != 0) {
            Bukkit.broadcastMessage("Starting game in " + secondsLeft + " seconds");

            Bukkit.getOnlinePlayers()
                    .forEach(p -> p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1,1));
        }

        if (secondsLeft <= 3) {
            final Title title = new Title()
                    .setDuration(20)
                    .setFadeOut(5)
                    .setTitle(ChatColor.RED.toString() + secondsLeft);

            Bukkit.getOnlinePlayers().forEach(title::send);
        }

    }

    @Override
    protected void finish() {
        GameStateManager.getInstance().startGame();
    }
}
