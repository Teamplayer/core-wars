package io.teamplayer.corewars.util;

import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Sign;

/**
 * A utility class for converting a BlockFace to a yaw for an entity
 */
public final class DirectionUtil {

    private static final TObjectFloatMap<BlockFace> blockFaces = new TObjectFloatHashMap<>();

    private DirectionUtil() {
        throw new UnsupportedOperationException("This is a utility class");
    }

    /**
     * Get the yaw for an entity based off a blockface so that the player would be facing the same way as the blockface
     *
     * @param blockFace block face to get yaw of
     * @return yaw that will make an entity face the same as the block face
     * @throws IllegalArgumentException for blockfaces that can't be converted to yaws
     */
    public static float getYaw(BlockFace blockFace) {
        if (blockFaces.size() <= 0) populate();
        if (!blockFaces.containsKey(blockFace)) {
            throw new IllegalArgumentException("The block face '" + blockFace.name() +
                    "' can't be converted to a yaw");
        }

        return blockFaces.get(blockFace);
    }

    private static void populate() {

        for (byte i = 0; i < 16; i++) {
            final BlockFace direction = new Sign(Material.SIGN_POST, i).getFacing();
            final float yaw = (float) ((i * 22.5) - 180);

            blockFaces.put(direction, yaw);
        }

    }

}
