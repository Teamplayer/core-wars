package io.teamplayer.corewars.util;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Predicate;

/**
 * A utility class for finding blocks that are similar and connected to each other
 */
public final class BlockFindingUtil {

    private BlockFindingUtil() {
        throw new UnsupportedOperationException("This is a utility class");
    }

    /**
     * Find all blocks of the same material of the given block that are connected to that block and
     * other connected blocks of the same material
     *
     * @param block starting block
     * @return connected blocks of the same material
     */
    public static Collection<Block> findSimmilar(Block block) {
        return findNearby(block, block.getType());
    }

    /**
     * Find all blocks of the same material and data of the given block that are connected to that
     * block and other connected blocks of the same material and data
     *
     * @param block starting block
     * @return connected blocks of the same material and data
     */
    public static Collection<Block> findExact(Block block) {
        return findNearby(block, b -> (b.getType().equals(block.getType())) && (b.getData() == block.getData()));
    }

    /**
     * Find all blocks of the specified material that are connected to that block and other
     * connected blocks of the specified material
     *
     * @param block    starting block
     * @param matching the material the blocks need to match
     * @return connected blocks of the same material
     */
    public static Collection<Block> findNearby(Block block, Material matching) {
        return findNearby(block, b -> b.getType().equals(matching));
    }

    /**
     * Find all blocks that match the predicate that are connected to that block and other connected
     * blocks of that match the predicate
     *
     * @param block     starting block
     * @param predicate the material the blocks need to match
     * @return connected blocks of the same material
     */
    private static Collection<Block> findNearby(Block block, Predicate<Block> predicate) {
        return new BlockFinder(block, predicate).getFoundBlocks();
    }

    private static Block getRelative(Block b, int i) {
        i++;
        int n = i % 18;
        switch (n) {
            case 0:
                return b.getRelative(0, 1, 0);
            case 1:
                return b.getRelative(0, 0, -1);
            case 2:
                return b.getRelative(-1, 0, 0);
            case 3:
                return b.getRelative(0, 0, 1);
            case 4:
                return b.getRelative(1, 0, 0);
            case 5:
                return b.getRelative(0, -1, 0);
            case 6:
                return b.getRelative(0, -1, 1);
            case 7:
                return b.getRelative(1, -1, 0);
            case 8:
                return b.getRelative(0, -1, -1);
            case 9:
                return b.getRelative(-1, -1, 1);
            case 10:
                return b.getRelative(-1, 0, 1);
            case 11:
                return b.getRelative(1, 0, 1);
            case 12:
                return b.getRelative(1, 0, -1);
            case 13:
                return b.getRelative(-1, 0, -1);
            case 14:
                return b.getRelative(0, 1, 1);
            case 15:
                return b.getRelative(1, 1, 0);
            case 16:
                return b.getRelative(0, 1, -1);
            case 17:
                return b.getRelative(-1, 1, 1);
        }

        return null;
    }

    private static class BlockFinder {

        private Collection<Block> foundBlocks = new HashSet<>();
        private Collection<Block> checkedBlocks = new HashSet<>();

        private final Predicate<Block> matcher;

        private BlockFinder(Block block, Predicate<Block> matcher) {
            this.matcher = matcher;
            check(block);
        }

        private void check(Block block) {
            if (checkedBlocks.contains(block)) return;
            checkedBlocks.add(block);

            if (matcher.test(block)) {
                foundBlocks.add(block);

                for (int i = 0; i < 18; i++) {
                    check(getRelative(block, i));
                }
            }
        }

        Collection<Block> getFoundBlocks() {
            return foundBlocks;
        }
    }
}
