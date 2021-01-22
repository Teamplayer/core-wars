package io.teamplayer.corewars.util;

import org.bukkit.ChatColor;

/**
 * A utility class to color messages for chat
 */
public final class StringColorUtil {

    private StringColorUtil() {
        throw new UnsupportedOperationException("This is a utility class");
    }

    /**
     * Color part of a message with minecraft chat colors starting from the beginning of the string
     *
     * @param message   message to make color
     * @param percent   percent the message should be color
     * @param color     the color to use to color the message
     * @param uncolored the color the uncolored part of the message should be
     * @param bold      whether or not the message should be bold
     * @return colored message
     */
    public static String colorFromFront(String message, float percent, ChatColor color, ChatColor uncolored,
                                        boolean bold) {

        final StringBuilder coloredMessage = new StringBuilder(message);
        final int coloredAmount = (int) Math.floor(message.length() * percent);

        insertColor(coloredMessage, coloredAmount, uncolored, bold);
        insertColor(coloredMessage, 0, color, bold);

        return coloredMessage.toString();
    }

    private static void insertColor(StringBuilder builder, int index, ChatColor color, boolean bold) {
        if (bold) {
            builder.insert(index, ChatColor.BOLD);
        }

        builder.insert(index, color);
    }
}
