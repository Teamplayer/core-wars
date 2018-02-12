package io.teamplayer.corewars.game;

import io.teamplayer.corewars.team.Team;
import io.teamplayer.corewars.util.task.TaskTimer;
import org.bukkit.Bukkit;

/**
 * The task that claims the RespawnCore every X minutes
 */
public class ClaimTask extends TaskTimer {

    public ClaimTask() {
        super(3 * 60);
    }

    @Override
    protected void finish() {
        claimCores();
        GameStateManager.getInstance().nextTask();
    }

    private void claimCores() {
        GameStateManager.getInstance().getTeamManager().getAllTeams().stream()
                .filter(Team::isTeamEnabled)
                .forEach(Team::takeCore);
        Bukkit.broadcastMessage("A core has been reclaimed from each team!");
    }
}
