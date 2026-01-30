package bob.tasktype;

import java.time.LocalDateTime;

import bob.task.Task;
import bob.util.DateTimeUtil;

/**
 * A task with a description and a start and end time.
 */
public class Event extends Task {
    private LocalDateTime from;
    private LocalDateTime to;

    /**
     * Creates a new event task with the given description, start time, and end time.
     *
     * @param description Description of the event task.
     * @param from Start time of the event.
     * @param to End time of the event.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the start time of this event.
     *
     * @return Start time.
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the end time of this event.
     *
     * @return End time.
     */
    public LocalDateTime getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "[E]" + "[" + statusIcon() + "] " + getDescription()
                + " (from: " + DateTimeUtil.formatForDisplay(getFrom()) + " to: " + DateTimeUtil.formatForDisplay(getTo()) + ")";
    }
}
