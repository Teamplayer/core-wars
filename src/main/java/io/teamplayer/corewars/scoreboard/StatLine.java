package io.teamplayer.corewars.scoreboard;

import io.teamplayer.corewars.player.CorePlayer;
import io.teamplayer.corewars.player.StatType;
import io.teamplayer.teamcore.scoreboard.PlayerScoreboardLine;
import org.bukkit.entity.Player;

/**
 * A line that displays a single stat associated with a specified player
 */
public class StatLine extends PlayerScoreboardLine {

    public static final String NAME = "Stat";

    private final StatType statType;

    StatLine(StatType statType) {
        super(NAME);

        this.statType = statType;
    }

    @Override
    protected String getContent(long l, Player player) {
        final CorePlayer corePlayer = CorePlayer.get(player);

        return statType.getSimpleName() + ": " +
                corePlayer.getTeam().getType().getChatColor() +
                corePlayer.getStats().getStat(statType);
    }
}
