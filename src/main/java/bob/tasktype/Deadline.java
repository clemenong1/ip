package bob.tasktype;

import java.time.LocalDateTime;

import bob.task.Task;
import bob.util.DateTimeUtil;

/**
 * A task with a description and a single deadline time.
 */
public class Deadline extends Task {
    public LocalDateTime by;

    /**
     * Creates a new deadline task with the given description and deadline time.
     *
     * @param description Description of the deadline task.
     * @param by Deadline time for the task.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D]" + "[" + statusIcon() + "] " + getDescription()
                + " (by: " + DateTimeUtil.formatForDisplay(by) + ")";
    }
}
