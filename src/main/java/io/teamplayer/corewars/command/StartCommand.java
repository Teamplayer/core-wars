package io.teamplayer.corewars.command;

import io.teamplayer.corewars.CoreWars;
import io.teamplayer.corewars.game.GameStateManager;
import io.teamplayer.corewars.util.task.TaskTimer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Force starts the game while in lobby
 */
public class StartCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("Force starting game.");

        GameStateManager.getInstance().loadMap();
        GameStateManager.getInstance().startGame();

        final TaskTimer startingCountdown = CoreWars.getInstance().getCountdownManager()
                .getCountdown();

        if (startingCountdown != null) {
            startingCountdown.cancel();
        }

        return true;
    }
}
