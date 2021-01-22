package io.teamplayer.corewars.team;

import com.google.common.collect.ImmutableSet;
import io.teamplayer.teamcore.immutable.ImmutableLocation;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.Collection;

/**
 * The locations and config data for a team base in the map
 */
public class TeamData {

    /** All the player spawn for a team */
    private final ImmutableLocation spawn;

    /** All of the blocks that can change color and are related to a team */
    private final ImmutableSet<Block> coloredBlocks;

    /** The team's pedestals for the cores*/
    private final ImmutableSet<Block> pedestals;

    /** Locations where the fireworks spawn for the team */
    private final ImmutableSet<ImmutableLocation> fireworkSpawns;

    /** The hologram that shows the starting countdown during pregame */
    private final ImmutableLocation startingHolo;

    /** The blocks for the barriers that are present during pregame */
    private final ImmutableSet<Block> startingBarriers;

    /** Banners that display the team's color */
    private final ImmutableSet<Block> banners;

    /** The center of the area a player stands to capture a team's core */
    private final ImmutableLocation captureArea;

    public TeamData(
            Location spawn,
            Collection<Block> coloredBlocks,
            Collection<Block> pedestals,
            Collection<ImmutableLocation> fireworkSpawns,
            Location startingHolo,
            Collection<Block> startingBarriers,
            Collection<Block> banners,
            Location captureArea) {

        this.spawn = ImmutableLocation.from(spawn);
        this.coloredBlocks = ImmutableSet.copyOf(coloredBlocks);
        this.pedestals = ImmutableSet.copyOf(pedestals);
        this.fireworkSpawns = ImmutableSet.copyOf(fireworkSpawns);
        this.startingHolo = ImmutableLocation.from(startingHolo);
        this.startingBarriers = ImmutableSet.copyOf(startingBarriers);
        this.banners = ImmutableSet.copyOf(banners);
        this.captureArea = ImmutableLocation.from(captureArea);
    }

    public ImmutableLocation getSpawn() {
        return spawn;
    }

    public ImmutableSet<Block> getColoredBlocks() {
        return coloredBlocks;
    }

    public ImmutableSet<Block> getPedestals() {
        return pedestals;
    }

    public ImmutableSet<ImmutableLocation> getFireworkSpawns() {
        return fireworkSpawns;
    }

    public ImmutableLocation getStartingHolo() {
        return startingHolo;
    }

    public ImmutableSet<Block> getStartingBarriers() {
        return startingBarriers;
    }

    public ImmutableSet<Block> getBanners() {
        return banners;
    }

    public ImmutableLocation getCaptureArea() {
        return captureArea;
    }
}
