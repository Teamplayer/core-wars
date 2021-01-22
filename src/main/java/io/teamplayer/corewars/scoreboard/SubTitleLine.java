package io.teamplayer.corewars.scoreboard;

import io.teamplayer.corewars.CoreWars;
import io.teamplayer.teamcore.scoreboard.ScoreboardLine;
import org.bukkit.ChatColor;

/**
 * Scoreboard line used to title groups in the scorebaord
 */
public class SubTitleLine extends ScoreboardLine {

    public SubTitleLine(String content) {
        super("sub:" + content, CoreWars.SEC_COLOR + ChatColor.BOLD.toString() + content);
    }
}

