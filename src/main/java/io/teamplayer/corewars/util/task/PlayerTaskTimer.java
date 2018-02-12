package io.teamplayer.corewars.util.task;

import io.teamplayer.corewars.player.CorePlayer;
import io.teamplayer.corewars.util.StringColorUtil;
import org.bukkit.ChatColor;

/**
 * A TaskTimer that give the player a message on their screen displaying how long it will be
 * until the task is completed
 */
public abstract class PlayerTaskTimer extends TaskTimer {

    protected final CorePlayer player;
    private final String message;
    private final ChatColor messageColor;
    private final boolean mustBeAlive;

    /**
     * @param player the player the task is for
     * @param duration the amount of time it will take the time to complete in seconds
     * @param message the message to display in the action bar
     * @param messageColor the color the message will be filled with
     * @param mustBeAlive whether or not the timer will cancel if the player dies
     */
    public PlayerTaskTimer(CorePlayer player, int duration, String message,
                           ChatColor messageColor, boolean mustBeAlive) {
        super(duration);
        this.player = player;
        this.message = message.toUpperCase();
        this.messageColor = messageColor;
        this.mustBeAlive = mustBeAlive;
    }

    /**
     * @param player the player the task is for
     * @param duration the amount of time it will take the time to complete in seconds
     * @param message the message to display in the action bar
     * @param messageColor the color the message will be filled with
     */
    public PlayerTaskTimer(CorePlayer player, int duration, String message,
                           ChatColor messageColor) {
        this(player, duration, message, messageColor, true);
    }

    @Override
    public void cancel() {
        super.cancel();
        player.sendActionBarMessage(""); //Clears their current message
    }

    @Override
    protected void ticked() {
        sendActionMessage();
    }

    @Override
    protected boolean shouldStillRun() {
        return !(mustBeAlive && !player.getState().equals(CorePlayer.PlayerState.ALIVE));
    }

    private void sendActionMessage() {
        player.sendActionBarMessage(StringColorUtil.colorFromFront(message, getPercentComplete(),
                messageColor, ChatColor.WHITE, true));
    }

    protected abstract void finish();

}
