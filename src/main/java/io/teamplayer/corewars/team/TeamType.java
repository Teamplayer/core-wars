package io.teamplayer.corewars.team;

import org.bukkit.ChatColor;
import org.bukkit.Color;

/**
 * The visual data that is associated randomly with a team
 */
public enum TeamType {
    //Colors (Used for armor), Chat Color, Data byte for wool/glass/clay color
    RED(Color.fromRGB(153, 51, 51), ChatColor.RED, (byte) 14),
    YELLOW(Color.fromRGB(229, 229, 51), ChatColor.YELLOW, (byte) 4),
    BLUE(Color.fromRGB(51, 76, 178), ChatColor.BLUE, (byte) 11),
    GREEN(Color.fromRGB(102, 127, 51), ChatColor.GREEN, (byte) 13),
    ORANGE(Color.fromRGB(225, 140, 0), ChatColor.GOLD, (byte) 1),
    PINK(Color.fromRGB(230, 32, 144), ChatColor.LIGHT_PURPLE, (byte) 6),
    WHITE(Color.fromRGB(225,225,225), ChatColor.WHITE, (byte) 0);

    private final Color color;
    private final ChatColor chatColor;
    private final byte data;

    TeamType(Color color, ChatColor chatColor, byte data) {
        this.color = color;
        this.chatColor = chatColor;
        this.data = data;
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
}
