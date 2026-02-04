package bob.task;

import java.util.ArrayList;
import java.util.Arrays;

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
        tasks.add(task);
    }

    /**
     * Adds multiple tasks to the list.
     *
     * @param tasks Tasks to add.
     */
    public void add(Task... tasks) {
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
        return tasks.remove(index);
    }

    /**
     * Returns the task at the specified index.
     *
     * @param index Index of the task to retrieve.
     * @return The task at the specified index.
     */
    public Task get(int index) {
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
        ArrayList<Task> matching = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        for (Task t : tasks) {
            if (t.getDescription().toLowerCase().contains(lowerKeyword)) {
                matching.add(t);
            }
        }
        return matching;
    }
}
