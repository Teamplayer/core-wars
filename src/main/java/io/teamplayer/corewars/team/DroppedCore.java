package io.teamplayer.corewars.team;

import io.teamplayer.corewars.player.CorePlayer;
import io.teamplayer.corewars.util.area.AreaMonitor;
import io.teamplayer.corewars.util.area.SinglePlayerAreaMonitor;
import io.teamplayer.corewars.util.task.MultiPlayerTaskTimer;
import io.teamplayer.corewars.util.task.PlayerTaskTimer;
import io.teamplayer.teamcore.holo.Hologram;
import io.teamplayer.teamcore.immutable.ImmutableLocation;
import org.bukkit.ChatColor;
import org.bukkit.Location;

/**
 * The object that holds a RepsawnCore when it is on the ground and not being held by a team or a
 * player
 */
public class DroppedCore {

    private static final double INTERACT_RADIUS = 3;

    private final RespawnCore core;
    private final Team team;
    private final ImmutableLocation location;

    private final CaptureArea captureArea;
    private final ReturnArea returnArea;
    private final Hologram hologram;
    private final FloatingCore floatingCore;

    public DroppedCore(RespawnCore core, Location location) {
        this.core = core;
        this.team = core.getOwningTeam();
        this.location = ImmutableLocation.from(location);
        this.floatingCore = new FloatingCore(location.add(0, 1, 0), core);

        captureArea = new CaptureArea();
        returnArea = new ReturnArea();

        final TeamType type = team.getType();

        hologram = new Hologram(this.location.mutable().add(0, 1, 0),
                type.getChatColor() + ChatColor.BOLD.toString() + type.name() +
                        " TEAM'S CORE");
        hologram.spawn();
    }

    private RespawnCore destroy() {
        captureArea.disable();
        returnArea.disable();
        hologram.destroy();
        floatingCore.destroy();

        return core;
    }

    private class CaptureArea extends SinglePlayerAreaMonitor {

        private PlayerTaskTimer captureTask;

        CaptureArea() {
            super(location, INTERACT_RADIUS);
        }

        @Override
        protected void enteredArea(CorePlayer player) {
            captureTask = new CaptureTask(player);
        }

        @Override
        protected void leftArea(CorePlayer player) {
            captureTask.cancel();
        }

        @Override
        protected boolean check(CorePlayer player) {
            return !player.getTeam().equals(team);
        }

        @Override
        public void disable() {
            super.disable();
            if (captureTask != null) captureTask.cancel();
        }
    }

    private class CaptureTask extends PlayerTaskTimer {

        CaptureTask(CorePlayer player) {
            super(player, 3, "Picking up core", team.getType().getChatColor());
        }

        @Override
        protected void finish() {
            player.equipCore(destroy());
        }
    }

    private class ReturnArea extends AreaMonitor {

        private final MultiPlayerTaskTimer returnTask = new ReturnTask();

        ReturnArea() {
            super(location, INTERACT_RADIUS);
        }

        @Override
        protected void enteredArea(CorePlayer player) {
            returnTask.addPlayer(player);
        }

        @Override
        protected void leftArea(CorePlayer player) {
            returnTask.removePlayer(player);
        }

        @Override
        protected boolean check(CorePlayer player) {
            return player.getTeam().equals(team);
        }

        @Override
        public void disable() {
            super.disable();
            returnTask.cancel();
        }
    }

    private class ReturnTask extends MultiPlayerTaskTimer {

        ReturnTask() {
            super(20, "Returning Core", team.getType().getChatColor());
        }

        @Override
        protected void finish() {
            team.giveCore(destroy());
        }
    }
}
