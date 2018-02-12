package io.teamplayer.corewars.util.area;

import io.teamplayer.corewars.player.CorePlayer;

/**
 * Monitor only affects other players on the same team as the player specified in the constructor
 */
public abstract class PlayerTeamAreaMonitor extends AreaMonitor {

    private final CorePlayer player;

    public PlayerTeamAreaMonitor(CorePlayer player, double radius) {
        super(player.getPlayer(), radius);
        this.player = player;
    }

    @Override
    protected boolean check(CorePlayer player) {
        return player.getTeam().equals(player.getTeam());
    }
}
