public class Task {
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

    public Status getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    protected String statusIcon() {
        return status.icon;
    }

    @Override
    public String toString() {
        return "[" + statusIcon() + "] " + description;
    }
}
