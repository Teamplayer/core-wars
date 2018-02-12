package io.teamplayer.corewars.util.area;

import io.teamplayer.corewars.player.CorePlayer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.LinkedList;
import java.util.List;

/**
 * Monitor can only have one player present in it at once
 */
public abstract class SinglePlayerAreaMonitor extends AreaMonitor {

    private final List<CorePlayer> playersInArea = new LinkedList<>();

    private CorePlayer affectedPlayer;

    public SinglePlayerAreaMonitor(Entity center, double radius) {
        super(center, radius);
    }

    public SinglePlayerAreaMonitor(Location location, double radius) {
        super(location, radius);
    }

    @Override
    void callEntered(CorePlayer player) {
        if (!playersInArea.contains(player)) {
            playersInArea.add(player);
        }

        if (affectedPlayer == null) {
            affectedPlayer = player;
            enteredArea(player);
        }
    }

    @Override
    void callLeft(CorePlayer player) {
        playersInArea.remove(player);

        if (affectedPlayer != null && player.equals(affectedPlayer)) {
            affectedPlayer = null;
            leftArea(player);

            if (playersInArea.size() > 0) {
                callEntered(playersInArea.get(0));
            }
        }
    }
}
