package io.teamplayer.corewars.player;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import io.teamplayer.corewars.CoreWars;
import io.teamplayer.corewars.scoreboard.StatLine;

/**
 * Contains and controls all of the player's stats for the current game
 */
public class StatManager {

    final TObjectIntMap<StatType> currentStats = new TObjectIntHashMap<>(StatType.values().length);

    StatManager() {
    }

    public void incStat(StatType type) {
        adjustStat(type, 1);
    }

    public int getStat(StatType type) {
        return currentStats.get(type);
    }

    private void adjustStat(StatType type, int adjustment) {
        currentStats.adjustOrPutValue(type, adjustment, adjustment);
        CoreWars.getInstance().getScoreboardManager().updateLine(StatLine.NAME);
    }
}
