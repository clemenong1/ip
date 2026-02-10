package bob.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import bob.tasktype.Deadline;
import bob.tasktype.Event;

/**
 * Encapsulates a list of tasks and provides operations to manage them.
 */
public class TaskList {
    private ArrayList<Task> tasks;

    /**
     * Creates an empty TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a TaskList with the given list of tasks.
     *
     * @param tasks List of tasks to initialize with.
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "tasks list must not be null";
        this.tasks = tasks;
    }

    /**
     * Creates a TaskList with the given tasks.
     *
     * @param initialTasks Tasks to initialize with.
     */
    public TaskList(Task... initialTasks) {
        this.tasks = new ArrayList<>(Arrays.asList(initialTasks));
    }

    /**
     * Adds a task to the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        assert task != null : "task must not be null";
        tasks.add(task);
    }

    /**
     * Adds multiple tasks to the list.
     *
     * @param tasks Tasks to add.
     */
    public void add(Task... tasks) {
        assert tasks != null : "tasks array must not be null";
        for (Task t : tasks) {
            this.tasks.add(t);
        }
    }

    /**
     * Removes and returns the task at the specified index.
     *
     * @param index Index of the task to remove.
     * @return The removed task.
     */
    public Task remove(int index) {
        assert isValidIndex(index) : "index must be valid (0 to size-1)";
        return tasks.remove(index);
    }

    /**
     * Returns the task at the specified index.
     *
     * @param index Index of the task to retrieve.
     * @return The task at the specified index.
     */
    public Task get(int index) {
        assert isValidIndex(index) : "index must be valid (0 to size-1)";
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Checks if the given index is valid for this list.
     *
     * @param index Index to check.
     * @return True if the index is valid, false otherwise.
     */
    public boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    /**
     * Returns all tasks as an ArrayList.
     * This method is provided for compatibility with Storage and Ui classes.
     *
     * @return ArrayList containing all tasks.
     */
    public ArrayList<Task> getAllTasks() {
        return tasks;
    }

    /**
     * Returns tasks whose description contains the given keyword (case-insensitive).
     *
     * @param keyword Keyword to search for in task descriptions.
     * @return ArrayList of matching tasks.
     */
    public ArrayList<Task> findTasksByKeyword(String keyword) {
        assert keyword != null : "keyword must not be null";
        String lowerKeyword = keyword.toLowerCase();
        return new ArrayList<>(tasks.stream()
                .filter(t -> t.getDescription().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList()));
    }

    /**
     * Returns tasks with deadlines due within the next 3 days.
     *
     * @return List of urgent deadline tasks.
     */
    public ArrayList<Task> getUrgentTasks() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeDaysLater = now.plusDays(3);
        ArrayList<Task> urgent = new ArrayList<>();
        for (Task t : tasks) {
            if (t instanceof Deadline) {
                Deadline d = (Deadline) t;
                LocalDateTime by = d.getBy();
                if (!by.isBefore(now) && by.isBefore(threeDaysLater)) {
                    urgent.add(t);
                }
            }
        }
        return urgent;
    }

    /**
     * Returns tasks that occur on the given date.
     * Includes deadlines due on that date and events overlapping that day.
     *
     * @param date The date to filter by.
     * @return List of matching tasks.
     */
    public ArrayList<Task> getTasksOnDate(LocalDate date) {
        assert date != null : "date must not be null";
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay().minusNanos(1);

        ArrayList<Task> matching = new ArrayList<>();
        for (Task t : tasks) {
            if (isTaskOnDate(t, date, startOfDay, endOfDay)) {
                matching.add(t);
            }
        }
        return matching;
    }

    private boolean isTaskOnDate(Task task, LocalDate date,
            LocalDateTime startOfDay, LocalDateTime endOfDay) {
        if (task instanceof Deadline) {
            Deadline d = (Deadline) task;
            return d.getBy().toLocalDate().equals(date);
        }
        if (task instanceof Event) {
            Event e = (Event) task;
            boolean endsOnOrAfterStart = !e.getTo().isBefore(startOfDay);
            boolean startsOnOrBeforeEnd = !e.getFrom().isAfter(endOfDay);
            return endsOnOrAfterStart && startsOnOrBeforeEnd;
        }
        return false;
    }
}
