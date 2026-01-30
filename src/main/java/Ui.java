import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles user interface interactions for the Bob chatbot.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private Scanner scanner;

    /**
     * Creates a new Ui instance.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Shows the welcome message.
     */
    public void showWelcome() {
        showLine();
        System.out.println("Hello! I'm Bob");
        System.out.println("What can I do for you?");
        showLine();
    }

    /**
     * Shows a divider line.
     */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Reads a command from the user.
     *
     * @return User input as a trimmed string.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Shows an error message.
     *
     * @param message Error message to display.
     */
    public void showError(String message) {
        showLine();
        System.out.println(message);
        showLine();
    }

    /**
     * Shows the list of tasks.
     *
     * @param tasks List of tasks to display.
     */
    public void showTaskList(ArrayList<Task> tasks) {
        showLine();
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
        showLine();
    }

    /**
     * Shows the goodbye message.
     */
    public void showGoodbye() {
        showLine();
        System.out.println("Bye. Hope to see you again soon!");
        showLine();
    }

    /**
     * Shows a message when a task is marked as done.
     *
     * @param task Task that was marked.
     */
    public void showMarkedTask(Task task) {
        showLine();
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
        showLine();
    }

    /**
     * Shows a message when a task is marked as not done.
     *
     * @param task Task that was unmarked.
     */
    public void showUnmarkedTask(Task task) {
        showLine();
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
        showLine();
    }

    /**
     * Shows a message when a task is deleted.
     *
     * @param task Task that was deleted.
     * @param total Total number of tasks remaining.
     */
    public void showDeletedTask(Task task, int total) {
        showLine();
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + total + " tasks in the list.");
        showLine();
    }

    /**
     * Shows a message when a task is added.
     *
     * @param task Task that was added.
     * @param total Total number of tasks after adding.
     */
    public void showAddedTask(Task task, int total) {
        showLine();
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + total + " tasks in the list.");
        showLine();
    }

    /**
     * Shows tasks occurring on a specific date.
     *
     * @param date Date to display in the header.
     * @param tasks List of tasks to display (already filtered).
     */
    public void showTasksOnDate(java.time.LocalDate date, ArrayList<Task> tasks) {
        showLine();
        System.out.println("Here are the tasks occurring on " + DateTimeUtil.formatDateForDisplay(date) + ":");

        if (tasks.isEmpty()) {
            System.out.println("No matching tasks.");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + "." + tasks.get(i));
            }
        }
        showLine();
    }

    /**
     * Closes the scanner.
     */
    public void close() {
        scanner.close();
    }
}
