package io.teamplayer.corewars.scoreboard;

import io.teamplayer.corewars.CoreWars;
import io.teamplayer.corewars.lobby.CountdownManager;
import io.teamplayer.corewars.player.PlayerGetter;
import io.teamplayer.teamcore.scoreboard.ScoreboardFrame;
import io.teamplayer.teamcore.scoreboard.ScoreboardLine;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;

/**
 * The scoreboard frame used in the lobby
 */
public class LobbyFrame extends ScoreboardFrame {

    public static final String NEEDED_NAME = "needed";

    {
        setTitle(new GameTitle());
        insert(" ");

        insert(new SubTitleLine("Status"));
        insert(new ScoreboardLine("status",10) {
            @Override
            protected String getContent(long animationCycle) {
                final CountdownManager listener = CoreWars.getInstance().getCountdownManager();
                if (listener.isRunning()) {
                    final int timeLeft = listener.getCountdown().getSecondsRemaining();
                    return "Starting in " + CoreWars.PRI_COLOR + timeLeft + 's';

                } else {
                    return "Waiting" + StringUtils.repeat('.', (int) (animationCycle % 4));
                }
            }
        });

        insert(" ");

        insert(new SubTitleLine("Players"));
        insert(new ScoreboardLine("current"){
            @Override
            protected String getContent(long animationCycle) {
                return "Current: " + PlayerGetter.getActivePlayers().size() + '/' +
                        CoreWars.MAX_COUNT;
            }
        });
        insert(new ScoreboardLine(NEEDED_NAME){
            @Override
            protected String getContent(long animationCycle) {
                final ChatColor color = CoreWars.getInstance().getCountdownManager()
                        .isRunning() ? ChatColor.GRAY : CoreWars.PRI_COLOR;

                return "Needed: " + color + Math.max(CoreWars.MIN_COUNT - PlayerGetter
                        .getActivePlayers().size(), 0);
            }
        });
    }

}
