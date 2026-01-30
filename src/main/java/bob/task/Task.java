package bob.task;

/**
 * Represents a task with a description and completion status.
 * Subclasses (e.g. Todo, Deadline, Event) add type-specific details.
 */
public class Task {

    /**
     * Completion status of a task.
     */
    public enum Status {
        DONE("X"),
        NOT_DONE(" ");

        public final String icon;

        Status(String icon) {
            this.icon = icon;
        }
    }

    protected String description;
    protected Status status;

    /**
     * Creates a new task with the given description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.status = Status.NOT_DONE;
    }

    /**
     * Marks this task as done.
     */
    public void markDone() {
        this.status = Status.DONE;
    }

    /**
     * Marks this task as not done.
     */
    public void markNotDone() {
        this.status = Status.NOT_DONE;
    }

    /**
     * Returns the completion status of this task.
     *
     * @return Current status (DONE or NOT_DONE).
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Sets the completion status of this task.
     *
     * @param status New status to set.
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Returns the description of this task.
     *
     * @return Task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the icon string for the current status (for display).
     *
     * @return Status icon (e.g. "X" or " ").
     */
    protected String statusIcon() {
        return status.icon;
    }

    @Override
    public String toString() {
        return "[" + statusIcon() + "] " + description;
    }
}
