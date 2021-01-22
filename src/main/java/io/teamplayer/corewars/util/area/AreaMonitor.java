package io.teamplayer.corewars.util.area;

import io.teamplayer.corewars.CoreWars;
import io.teamplayer.corewars.player.CorePlayer;
import io.teamplayer.corewars.player.PlayerGetter;
import io.teamplayer.teamcore.util.TeamRunnable;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

/**
 * Monitor a specific area and call 'enteredArea' on every alive player that enters the area and
 * 'leftArea' on every player that leaves/dies
 */
public abstract class AreaMonitor {

    /**
     * Amount of times to check for players leaving or entering the monitor, measured in ticks
     */
    private static final long CHECK_FREQUENCY = 10;

    /**
     * Collection of all of the monitors so they can be updated with the players inside of them
     */
    private static final Set<AreaMonitor> areaMonitors = new HashSet<>();

    /**
     * The radius of the monitor
     */
    private final double radius;

    /**
     * The entity the monitor is centered around. This value is null when just a location is being
     * used
     */
    private final Entity entityFocus;

    /**
     * The location the monitor is focused around
     */
    private final Location locationFocus;

    /**
     * Collection of the players inside of the monitor
     */
    private final Set<CorePlayer> affectedPlayers = new HashSet<>();

    /**
     * How many times larger the area is for leaving the monitor than it is for entering the monitor
     * (Setting this value below '1' will make a player constantly enter and leave the monitor once
     * past the linger distance)
     */
    protected float lingerDistance = 1.0F;

    public AreaMonitor(Entity center, double radius) {
        this.entityFocus = center;
        this.locationFocus = center.getLocation();
        this.radius = radius;
    }

    public AreaMonitor(Location location, double radius) {
        this.locationFocus = location;
        this.entityFocus = null;
        this.radius = radius;
    }

    static {

        //Runnable that updates the monitors
        ((TeamRunnable) () -> areaMonitors.forEach(AreaMonitor::checkMonitor)).runTaskTimer
                (CoreWars.getInstance(), CHECK_FREQUENCY);
    }

    {
        //Add this monitor to the static collection of all monitors
        areaMonitors.add(this);
    }

    /**
     * Keep the monitor from being updated any further
     */
    public void disable() {
        areaMonitors.remove(this);
    }

    /**
     * Allows the monitor to be updated again if it was previously disabled
     */
    public void enable() {
        areaMonitors.add(this);
        checkMonitor(this);
    }

    private Location getCenter() {
        if (entityFocus != null) {
            return entityFocus.getLocation();
        } else {
            return locationFocus;
        }
    }

    void callLeft(CorePlayer player) {
        leftArea(player);
    }

    void callEntered(CorePlayer player) {
        enteredArea(player);
    }

    /**
     * A player comes into the effect of the monitor
     *
     * @param player the player who came into the monitor
     */
    protected abstract void enteredArea(CorePlayer player);

    /**
     * A player leaves the monitor after being affected by it
     *
     * @param player the player leaving the monitor
     */
    protected abstract void leftArea(CorePlayer player);

    /**
     * Check whether or not an alive player is allowed to be affected by the monitor
     *
     * @param player player to be checked
     * @return whether or not the player can be affected by the monitor
     */
    protected boolean check(CorePlayer player) {
        return true;
    }

    private static void checkMonitor(AreaMonitor monitor) {
        final Location monitorCenter = monitor.getCenter();
        final List<CorePlayer> removedPlayers = new ArrayList<>(); /* When removing a
                    player in the first part of checking we won't need to check if they are in
                    the area for the second part of checking */

        for (Iterator<CorePlayer> players = monitor.affectedPlayers.iterator();
             players.hasNext(); ) {
            CorePlayer player = players.next();

            boolean remove = false;

            if (player.getState() != CorePlayer.PlayerState.ALIVE || !monitor.check(player)) {
                remove = true;
            }

            final double distanceSqr = monitorCenter.distanceSquared(player.getPlayer().getLocation());

            if (remove || distanceSqr > Math.pow(monitor.radius * monitor.lingerDistance, 2)) {
                remove = true;
            }

            if (remove) {
                removedPlayers.add(player);
                monitor.callLeft(player);
                monitor.affectedPlayers.remove(player);
            }
        }

        for (CorePlayer player : PlayerGetter.getAlivePlayers()) {
            if (removedPlayers.contains(player) || monitor.affectedPlayers.contains(player)) {
                continue;
            }

            if (monitor.check(player)) {

                final double distanceSqr = monitorCenter.distanceSquared(player.getPlayer().getLocation());

                if (distanceSqr < Math.pow(monitor.radius, 2)) {
                    monitor.callEntered(player);
                    monitor.affectedPlayers.add(player);
                }

            }
        }
    }

    private class LeaveListener implements Listener {

        @EventHandler
        public void onLeave(PlayerQuitEvent event) {
            final CorePlayer player = CorePlayer.get(event.getPlayer());

            for (AreaMonitor monitor : areaMonitors) {
                monitor.leftArea(player);
            }
        }

    }
}
