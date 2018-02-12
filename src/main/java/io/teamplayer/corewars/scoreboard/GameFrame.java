package io.teamplayer.corewars.scoreboard;

import io.teamplayer.corewars.game.GameStateManager;
import io.teamplayer.corewars.player.StatType;
import io.teamplayer.corewars.team.Team;
import io.teamplayer.teamcore.scoreboard.ScoreboardFrame;

/**
 * The scoreboard frame that is shown while the game is in progress
 */
public class GameFrame extends ScoreboardFrame {

    {
        setTitle(new GameTitle());
        insert(" ");

        insert(new SubTitleLine("Core Decay"));
        insert(GameStateManager.getInstance().getCountdownLine());

        insert(" ");

        for (Team team : GameStateManager.getInstance().getTeamManager().getActiveTeams()) {
            insert(new TeamStatusLine(team));
        }

        insert(" ");

        insert(new StatLine(StatType.KILLS));
        insert(new StatLine(StatType.FINAL_KILLS));
        insert(new StatLine(StatType.DEATHS));
        insert(new StatLine(StatType.CAPTURES));
    }
}
