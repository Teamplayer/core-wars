package io.teamplayer.corewars.util;

import io.teamplayer.teamcore.immutable.ImmutableLocation;
import org.bukkit.Location;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A representation of an area of locations where you can get random locations within it
 */
public class LocationArea {

    private final ImmutableLocation center;
    private final double radius;

    public LocationArea(Location center, double radius) {
        this.center = ImmutableLocation.from(center);
        this.radius = radius;
    }

    /**
     * Gets a location somewhere inside of the location area on the same y coordinate as the
     * center of the location area
     *
     * @return a location in the location area
     */
    public Location getLocation() {
        return center.mutable().add(getMod(), 0, getMod());
    }

    private double getMod() {
        return ThreadLocalRandom.current().nextDouble(radius * 2) - radius;
    }

    public ImmutableLocation getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }
}
