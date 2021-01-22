package io.teamplayer.corewars.team;

import io.teamplayer.corewars.player.CorePlayer;
import io.teamplayer.corewars.player.StatType;
import io.teamplayer.corewars.util.area.SinglePlayerAreaMonitor;
import io.teamplayer.corewars.util.task.PlayerTaskTimer;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * A monitor that monitors for enemy players attempting to steal a core from the specified team
 */
public class StealDetectionArea extends SinglePlayerAreaMonitor {

    private final Team team;

    private PlayerTaskTimer captureTask;

    /**
     * A monitor for detecting stealing
     *
     * @param location the center of the stealing area
     * @param team the team this area belongs to
     */
    StealDetectionArea(Location location, Team team) {
        super(location, 3);
        this.team = team;
    }

    @Override
    protected void enteredArea(CorePlayer player) {
        if (team.getAmountOfActiveCores() > 0) {
            captureTask = new CaptureTask(player);
        }
    }

    @Override
    protected void leftArea(CorePlayer player) {
        captureTask.cancel();
    }

    @Override
    protected boolean check(CorePlayer player) {
        //Only check for players that aren't on this team
        return !player.getTeam().equals(team) && !player.hasCore();
    }

    private class CaptureTask extends PlayerTaskTimer {

        CaptureTask(CorePlayer player) {
            super(player, 6, "Capturing Core", team.getType().getChatColor());
        }

        @Override
        protected void finish() {
            player.getStats().incStat(StatType.STEALS);
            player.equipCore(team.stealCore());
            Bukkit.broadcastMessage(String.format("%s has stolen a core from %s team",
                    player.getPlayer().getDisplayName(), team.getType().name()));
        }
    }
}
