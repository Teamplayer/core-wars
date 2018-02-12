package io.teamplayer.corewars.scoreboard;

import io.teamplayer.corewars.player.CorePlayer;
import io.teamplayer.corewars.player.StatType;
import io.teamplayer.teamcore.scoreboard.ScoreboardLine;

/**
 * A line that displays a few stats associated with a certain player
 */
public class PlayerStatLine extends ScoreboardLine {

    private static final StatType[] visibleStats = new StatType[]{StatType.KILLS,
            StatType.CAPTURES};

    private final CorePlayer player;

    public PlayerStatLine(CorePlayer player) {
        super("pstat:" + player.getPlayer().getDisplayName());
        this.player = player;
    }

    private String getStatSnippet(StatType stat) {
        return stat.getSimpleName().substring(0,1) + ":" + player.getStats().getStat(stat);
    }

    @Override
    protected String getContent(long animationCycle) {
        final StringBuilder statLine = new StringBuilder(player.getPlayer().getDisplayName())
                .append(' ');

        for (StatType visibleStat : visibleStats) {
            statLine.append(getStatSnippet(visibleStat));
            statLine.append(' ');
        }

        return super.getContent(animationCycle);
    }
}

