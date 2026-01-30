import java.time.LocalDateTime;

public class Event extends Task {
    public LocalDateTime from;
    public LocalDateTime to;

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

    @Override
    public String toString() {
        return "[E]" + "[" + statusIcon() + "] " + description
                + " (from: " + DateTimeUtil.formatForDisplay(from) + " to: " + DateTimeUtil.formatForDisplay(to) + ")";
    }
}
