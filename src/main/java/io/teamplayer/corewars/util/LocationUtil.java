package io.teamplayer.corewars.util;

import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 * A utility class for manipulating bukkit locations
 */
public final class LocationUtil {

    private LocationUtil() {
        throw new UnsupportedOperationException("This is a utility class");
    }

    /**
     * Get a location that is the center of the block
     *
     * @param block the block to get the location center of
     * @return a new location which represents the center of the block
     */
    public static Location blockCenter(Block block) {
        final Location newLoc = block.getLocation().clone();

        newLoc.add(0.5, 0.5, 0.5);

        return newLoc;
    }
}
