package io.teamplayer.corewars.map;

import io.teamplayer.corewars.util.DirectionUtil;
import io.teamplayer.teamcore.immutable.ImmutableLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

import java.util.Arrays;

/**
 * Represents all of the data extracted from a sign placed in the map for configing
 */
class ConfigSign {
    /** The contents of the sign */
    private final String[] contents;
    /** The direction the sign was facing */
    private final BlockFace facing;
    /** Whether or not the sign is a post */
    private final boolean post;
    /** The location of the sign */
    private final ImmutableLocation location;

    private ConfigSign(Location location, String[] contents, BlockFace facing, boolean post) {
        this.contents = contents;
        this.facing = facing;
        this.post = post;

        location.setYaw(DirectionUtil.getYaw(facing));
        this.location = ImmutableLocation.from(location);
    }

    /**
     * Create a new config sign based off of a sign
     *
     * @param block the sign to get the config sign of
     * @return the config sign based off the sign
     * @throws IllegalArgumentException when a block is passed in that isn't a sign
     */
    static ConfigSign from(Block block) {
        if (block.getType() != Material.SIGN &&
                block.getType() != Material.SIGN_POST &&
                block.getType() != Material.WALL_SIGN) {
            throw new IllegalArgumentException("The block with material '" + block.getType().name()
                    + "' can not be converted to a ConfigSign");
        }

        Sign signContent = ((Sign) block.getState());
        org.bukkit.material.Sign signBlock = ((org.bukkit.material.Sign) block.getState().getData());

        return new ConfigSign(block.getLocation(), signContent.getLines(), signBlock.getFacing(),
                block.getType() == Material.SIGN_POST);
    }

    String getLine(byte line) {
        return contents[line];
    }

    BlockFace getFacing() {
        return facing;
    }

    boolean isPost() {
        return post;
    }

    ImmutableLocation getLocation() {
        return location;
    }

    Block getBlock() {
        return location.getBlock();
    }

    Block getAttachedBlock() {
        if (post) {
            return getBlock().getRelative(BlockFace.DOWN);
        } else {
            return getBlock().getRelative(facing.getOppositeFace());
        }
    }

    @Override
    public String toString() {
        return "ConfigSign{" +
                "contents=" + Arrays.toString(contents) +
                ", facing=" + facing +
                ", post=" + post +
                ", location=" + location +
                '}';
    }
}
