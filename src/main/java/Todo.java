public class Todo extends Task {
    /**
     * Creates a new todo task with the given description.
     *
     * @param description Description of the todo task.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + "[" + statusIcon() + "] " + description;
    }
}
