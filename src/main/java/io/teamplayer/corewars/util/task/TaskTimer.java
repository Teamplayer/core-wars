package io.teamplayer.corewars.util.task;

import io.teamplayer.corewars.CoreWars;
import io.teamplayer.teamcore.util.TeamRunnable;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * A timer that runs every X ticks for a specified amount of times and calls 'ticked' every time
 * it ticks then calls finished when it's done
 */
public abstract class TaskTimer {

    /**
     * How many MC ticks a countdown tick is
     */
    private final int tickSize;
    private final BukkitRunnable runnable;

    private boolean scoreboardRelevant = false;

    private boolean running = true;
    final int maxTicks;
    double ticksRemaining;

    float decrementRate = 1;

    /**
     * @param duration the amount of time it will take for the timer to finish in seconds
     */
    public TaskTimer(int duration) {
        this(duration, 2);
    }

    /**
     * @param duration the amount of time it will take for the timer to finish in seconds
     *                 regardless of the tick size
     * @param tickSize how often this timer will 'tick' in minecraft ticks
     */
    public TaskTimer(int duration, int tickSize) {
        this.tickSize = tickSize;

        maxTicks = (20 / tickSize) * duration;
        ticksRemaining = maxTicks;

        runnable = ((TeamRunnable) () -> {
            ticksRemaining -= decrementRate;

            if (!shouldStillRun()) TaskTimer.this.cancel();

            if (ticksRemaining >= 0) {

                if (scoreboardRelevant) {
                    CoreWars.getInstance().getScoreboardManager()
                            .updateLine(TimeRemainingLine.NAME);
                }

                ticked();
            } else {
                TaskTimer.this.cancel();
                finish();
            }
        }).bukkit();

        runnable.runTaskTimer(CoreWars.getInstance(), tickSize, tickSize);
    }

    public void cancel() {
        runnable.cancel();
    }

    public boolean isRunning() {
        return running;
    }

    protected final float getPercentComplete() {
        return 1 - ((float) ticksRemaining / maxTicks);
    }

    public final int getSecondsRemaining() {
        return (int) Math.round(ticksRemaining / (20 / tickSize));
    }

    boolean isScoreboardRelevant() {
        return scoreboardRelevant;
    }

    void setScoreboardRelevant(boolean scoreboardRelevant) {
        this.scoreboardRelevant = scoreboardRelevant;
    }

    /**
     * Check if the timer should still run, if not the timer will cancel
     */
    protected boolean shouldStillRun() {
        return true;
    }

    /**
     * Called when the timer ticks down
     */
    protected void ticked() {
    }

    protected abstract void finish();
}
