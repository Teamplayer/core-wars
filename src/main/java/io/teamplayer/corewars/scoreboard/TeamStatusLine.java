package io.teamplayer.corewars.scoreboard;

import io.teamplayer.corewars.team.RespawnCore;
import io.teamplayer.corewars.team.Team;
import io.teamplayer.teamcore.scoreboard.ScoreboardLine;
import org.bukkit.ChatColor;

import java.util.Optional;

/**
 * Scoreboard line that displayed information on a specified team
 */
public class TeamStatusLine extends ScoreboardLine {

    public static final String NAME = "TeamStatus";

    private static final char ACTIVE = '◆';
    private static final char STOLEN = '◈';
    private static final char GONE = '◇';

    private final Team team;

    TeamStatusLine(Team team) {
        super(NAME, 20);
        this.team = team;
    }

    private String getTeamName() {
        final String name = team.getType().name().toLowerCase();
        return new StringBuilder()
                .append(name)
                .replace(0, 1, String.valueOf(Character.toUpperCase(name.charAt(0))))
                .toString();
    }

    @Override
    protected String getContent(long animationTick) {
        final int cores = team.getAmountOfActiveCores() + (team.isCoreStolen() ? 1 : 0);
        final StringBuilder builder = new StringBuilder()
                .append(team.getType().getChatColor())
                .append(team.getActivePlayers().size())
                .append(' ')
                .append(ChatColor.WHITE)
                .append(getTeamName())
                .append(' ');

        for (int i = 0; i < Math.max(Team.STARTING_CORES, cores); i++) {
            final boolean stolenCore = team.isCoreStolen() && i == cores - 1;
            final Optional<RespawnCore> core = stolenCore ? team.getStolenCore() : team.getCore(i);

            if (core.isPresent()) {
                builder.append(core.get().getVisualTeam().getType().getChatColor());

                if (!stolenCore) {
                    builder.append(ACTIVE);
                } else {
                    if (animationTick % 3 == 0) {
                        builder.append(STOLEN);
                    } else {
                        builder.append(GONE);
                    }
                }
            } else {
                builder.append(ChatColor.GRAY)
                        .append(GONE);
            }

            builder.append(' ');
        }

        //Delete the last space
        builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }
}
