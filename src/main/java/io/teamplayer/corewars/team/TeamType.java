package io.teamplayer.corewars.team;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.material.Dye;

/**
 * The visual data that is associated randomly with a team
 */
public enum TeamType {
    //Colors (Used for armor), Chat Color, Data byte for wool/glass/clay color
    RED(Color.fromRGB(153, 51, 51), ChatColor.RED, (byte) 14, DyeColor.RED),
    YELLOW(Color.fromRGB(229, 229, 51), ChatColor.YELLOW, (byte) 4, DyeColor.YELLOW),
    BLUE(Color.fromRGB(51, 76, 178), ChatColor.BLUE, (byte) 11, DyeColor.BLUE),
    GREEN(Color.fromRGB(102, 127, 51), ChatColor.GREEN, (byte) 13, DyeColor.GREEN),
    ORANGE(Color.fromRGB(225, 140, 0), ChatColor.GOLD, (byte) 1, DyeColor.ORANGE),
    PINK(Color.fromRGB(230, 32, 144), ChatColor.LIGHT_PURPLE, (byte) 6, DyeColor.PINK);

    private final Color color;
    private final ChatColor chatColor;
    private final byte data;
    private final DyeColor dyeColor;

    TeamType(Color color, ChatColor chatColor, byte data, DyeColor dyeColor) {
        this.color = color;
        this.chatColor = chatColor;
        this.data = data;
        this.dyeColor = dyeColor;
    }

    public Color getColor() {
        return color;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public byte getData() {
        return data;
    }

    public DyeColor getDyeColor() {
        return dyeColor;
    }
}
