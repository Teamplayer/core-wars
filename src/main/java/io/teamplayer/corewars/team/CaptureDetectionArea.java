package io.teamplayer.corewars.team;

import io.teamplayer.corewars.player.CorePlayer;
import io.teamplayer.corewars.player.StatType;
import io.teamplayer.corewars.util.area.AreaMonitor;
import io.teamplayer.corewars.util.task.PlayerTaskTimer;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

/**
 * The area the monitors for a player of the corresponding team that's trying to return a core
 * return a core back to their team
 */
public class CaptureDetectionArea extends AreaMonitor {

    private final Team team;
    private final Map<CorePlayer, ReturnTask> tasks = new HashMap<>();

    CaptureDetectionArea(Location location, Team team) {
        super(location, 3);
        this.team = team;
    }

    @Override
    protected void enteredArea(CorePlayer player) {
        tasks.put(player, new ReturnTask(player));
    }

    @Override
    protected void leftArea(CorePlayer player) {
        if (tasks.containsKey(player)) {
            tasks.remove(player).cancel();
        }
    }

    @Override
    protected boolean check(CorePlayer player) {
        return player.getTeam().equals(team) && player.hasCore();
    }

    private class ReturnTask extends PlayerTaskTimer {

        ReturnTask(CorePlayer player) {
            super(player, 3, "Returning Core", team.getType().getChatColor());
        }

        @Override
        protected void finish() {
            player.getStats().incStat(StatType.CAPTURES);
            team.giveCore(player.unequipCore());
            tasks.remove(player);
        }
    }
}
