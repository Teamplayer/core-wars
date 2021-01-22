package io.teamplayer.corewars.team;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;

/**
 * The visual data that is associated randomly with a team
 */
public enum TeamType {
    //Colors (Used for armor), Chat Color, Data byte for wool/glass/clay color
    RED(Color.fromRGB(153, 51, 51), ChatColor.RED, (byte) 14, Material.RED_WOOL),
    YELLOW(Color.fromRGB(229, 229, 51), ChatColor.YELLOW, (byte) 4, Material.YELLOW_WOOL),
    BLUE(Color.fromRGB(51, 76, 178), ChatColor.BLUE, (byte) 11, Material.BLUE_WOOL),
    GREEN(Color.fromRGB(102, 127, 51), ChatColor.GREEN, (byte) 13, Material.GREEN_WOOL),
    ORANGE(Color.fromRGB(225, 140, 0), ChatColor.GOLD, (byte) 1, Material.ORANGE_WOOL),
    PINK(Color.fromRGB(230, 32, 144), ChatColor.LIGHT_PURPLE, (byte) 6, Material.PINK_WOOL);

    private final Color color;
    private final ChatColor chatColor;
    private final byte data;
    private final Material woolType;

    TeamType(Color color, ChatColor chatColor, byte data, Material woolType) {
        this.color = color;
        this.chatColor = chatColor;
        this.data = data;
        this.woolType = woolType;
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

    public Material getWoolType() {
        return woolType;
    }
}
