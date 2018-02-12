package io.teamplayer.corewars.util;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * A utility class to get all of the signs in a bukkit world that are contained within loaded chunks
 */
public final class SignFindingUtil {

    private SignFindingUtil() {
        throw new UnsupportedOperationException("This is a utility class");
    }

    /**
     * Get all of the loaded signs inside of a world
     *
     * @param world the world you want the signs from
     * @return a collection of all the loaded signs in a world
     */
    public static Collection<Block> getLoadedSigns(World world) {
        /*
            This only gets signs that are contained within loaded chunks, so there will be an issue if the signs you
            want to get are outside of the constantly loaded chunks around spawn or if the world you want to get the
            signs of is unloaded.
         */
        return Arrays.stream(world.getLoadedChunks())
                .flatMap(c -> Arrays.stream(c.getTileEntities()))
                .map(BlockState::getBlock)
                .filter(b -> b.getType() == Material.SIGN_POST ||
                        b.getType() == Material.SIGN ||
                        b.getType() == Material.WALL_SIGN)
                .collect(Collectors.toList());
    }

}
