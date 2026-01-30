import java.time.LocalDateTime;

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
        return "[D]" + "[" + statusIcon() + "] " + description
                + " (by: " + DateTimeUtil.formatForDisplay(by) + ")";
    }
}
