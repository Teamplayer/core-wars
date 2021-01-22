package io.teamplayer.corewars.util.task;

import io.teamplayer.teamcore.scoreboard.ScoreboardLine;
import org.bukkit.ChatColor;

/**
 * A line that will display the amount of time left in the associated TaskTimer
 */
public class TimeRemainingLine extends ScoreboardLine {

    static final String NAME = "Remaining";

    private TaskTimer linkedTask;

    public TimeRemainingLine() {
        super(NAME);
    }

    public TimeRemainingLine(TaskTimer linkedTask) {
        this();
        setLinkedTask(linkedTask);
    }

    @Override
    protected String getContent(long animationCycle) {
        if (linkedTask == null) return "";

        final StringBuilder message = new StringBuilder(ChatColor.WHITE.toString());

        if (linkedTask.getSecondsRemaining() > 60) {
            message.append(((int) Math.ceil(
                    linkedTask.getSecondsRemaining() / 60))).append("m ");
        }

        message.append(linkedTask.getSecondsRemaining() % 60)
                .append('s');

        return message.toString();
    }

    public void setLinkedTask(TaskTimer linkedTask) {
        this.linkedTask = linkedTask;
        linkedTask.setScoreboardRelevant(true);
    }

    public TaskTimer getLinkedTask() {
        return linkedTask;
    }
}
