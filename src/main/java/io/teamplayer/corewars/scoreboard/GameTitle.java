package io.teamplayer.corewars.scoreboard;

import io.teamplayer.teamcore.scoreboard.ScoreboardLine;
import org.bukkit.ChatColor;

/**
 * The title of the game that's always displayed as the scoreboard title
 */
public class GameTitle extends ScoreboardLine {

    public GameTitle() {
        super("title", 1);
    }

    @Override
    protected String getContent(long updateAmount) {
        return ChatColor.GOLD.toString() + ChatColor.BOLD + "CoreWars";
    }
}
