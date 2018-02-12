package io.teamplayer.corewars.util.task;

import io.teamplayer.corewars.player.CorePlayer;
import io.teamplayer.corewars.util.StringColorUtil;
import org.bukkit.ChatColor;

import java.util.HashSet;
import java.util.Set;

/**
 * A task timer that allows will complete faster the more player that are helping in complete the
 * task. It will also display a message on all participating player's screens of the task's progress
 */
public abstract class MultiPlayerTaskTimer extends TaskTimer {

    private final Set<CorePlayer> playersDoingTask = new HashSet<>();

    private final String message;
    private final ChatColor messageColor;

    /** The amount additional players add to decrementRate */
    protected float help = .5F;

    /**
     * @param duration the amount of time it will take for the timer to finish in seconds
     */
    public MultiPlayerTaskTimer(int duration, String message, ChatColor messageColor) {
        super(duration);
        this.message = message;
        this.messageColor = messageColor;
    }

    public void addPlayer(CorePlayer player) {
        playersDoingTask.add(player);
        updateRate();
    }

    public void removePlayer(CorePlayer player) {
        playersDoingTask.remove(player);
        updateRate();
    }

    @Override
    protected void ticked() {
        sendMessages();
    }

    @Override
    public void cancel() {
        super.cancel();

        playersDoingTask.forEach(p -> p.sendActionBarMessage(""));
    }

    private void sendMessages() {
        final String message = StringColorUtil.colorFromFront(this.message, getPercentComplete(),
                messageColor, ChatColor.WHITE, true);

        playersDoingTask.forEach(p -> p.sendActionBarMessage(message));
    }

    private void updateRate() {
        final int size = playersDoingTask.size();

        decrementRate = (size > 0 ? 1 : 0) + ((size - 1) * help);
    }
}
