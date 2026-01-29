import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Runs the Bob chatbot application that manages a list of tasks.
 */
public class Bob {
    private static final String LINE = "____________________________________________________________";
    private static final Path DATA_DIRECTORY = Paths.get("data");
    private static final Path DATA_FILE_PATH = DATA_DIRECTORY.resolve("duke.txt");

    /**
     * Represents the completion status of a task.
     */
    enum Status {
        DONE("X"),
        NOT_DONE(" ");

        final String icon;

        /**
         * Creates a status with the icon used in the UI.
         *
         * @param icon Icon representing the status.
         */
        Status(String icon) {
            this.icon = icon;
        }
    }

    /**
     * Represents a task with a description and completion status.
     */
    static class Task {
        String description;
        Status status;

        /**
         * Creates a task with the given description and an initial NOT_DONE status.
         *
         * @param description Task description.
         */
        Task(String description) {
            this.description = description;
            this.status = Status.NOT_DONE;
        }

        /**
         * Returns the status icon for this task.
         *
         * @return Status icon.
         */
        String statusIcon() {
            return status.icon;
        }

        /**
         * Returns the type icon for this task.
         *
         * @return Type icon (empty by default).
         */
        String typeIcon() {
            return "";
        }

        @Override
        public String toString() {
            // default: no type
            return "[" + statusIcon() + "] " + description;
        }
    }

    /**
     * Represents a Todo task.
     */
    static class Todo extends Task {
        Todo(String description) {
            super(description);
        }

        @Override
        public String toString() {
            return "[T]" + "[" + statusIcon() + "] " + description;
        }
    }

    /**
     * Represents a Deadline task with a deadline time.
     */
    static class Deadline extends Task {
        String by;

        Deadline(String description, String by) {
            super(description);
            this.by = by;
        }

        @Override
        public String toString() {
            return "[D]" + "[" + statusIcon() + "] " + description + " (by: " + by + ")";
        }
    }

    /**
     * Represents an Event task with a start and end time.
     */
    static class Event extends Task {
        String from;
        String to;

        Event(String description, String from, String to) {
            super(description);
            this.from = from;
            this.to = to;
        }

        @Override
        public String toString() {
            return "[E]" + "[" + statusIcon() + "] " + description
                    + " (from: " + from + " to: " + to + ")";
        }
    }

    /**
     * Loads tasks from disk into an in-memory list.
     *
     * @return List of tasks loaded from disk. Returns an empty list if the file does not exist.
     */
    private static ArrayList<Task> loadTasksFromDisk() {
        ArrayList<Task> tasks = new ArrayList<>();

        if (!Files.exists(DATA_FILE_PATH)) {
            return tasks;
        }

        try (BufferedReader reader = Files.newBufferedReader(DATA_FILE_PATH)) {
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = parseTaskLine(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not load tasks: " + e.getMessage());
        }

        return tasks;
    }

    /**
     * Saves the given list of tasks to disk.
     *
     * @param tasks List of tasks to save.
     */
    private static void saveTasksToDisk(ArrayList<Task> tasks) {
        try {
            Files.createDirectories(DATA_DIRECTORY);

            try (BufferedWriter writer = Files.newBufferedWriter(DATA_FILE_PATH)) {
                for (Task task : tasks) {
                    writer.write(formatTaskLine(task));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Could not save tasks: " + e.getMessage());
        }
    }

    /**
     * Returns the storage format line for a given task.
     *
     * @param task Task to format.
     * @return Storage line representing the task.
     */
    private static String formatTaskLine(Task task) {
        String isDone = (task.status == Status.DONE) ? "1" : "0";

        if (task instanceof Todo) {
            return "T | " + isDone + " | " + task.description;
        }

        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return "D | " + isDone + " | " + deadline.description + " | " + deadline.by;
        }

        if (task instanceof Event) {
            Event event = (Event) task;
            return "E | " + isDone + " | " + event.description + " | " + event.from + " | " + event.to;
        }

        return "";
    }

    /**
     * Parses a storage format line into a Task instance.
     *
     * @param line Storage line.
     * @return Parsed task, or null if the line is invalid/corrupted.
     */
    private static Task parseTaskLine(String line) {
        try {
            String[] parts = line.split("\\s*\\|\\s*");
            if (parts.length < 3) {
                return null;
            }

            String type = parts[0];
            String isDone = parts[1];
            String description = parts[2];

            Task task;
            if ("T".equals(type)) {
                task = new Todo(description);
            } else if ("D".equals(type)) {
                if (parts.length < 4) {
                    return null;
                }
                task = new Deadline(description, parts[3]);
            } else if ("E".equals(type)) {
                if (parts.length < 5) {
                    return null;
                }
                task = new Event(description, parts[3], parts[4]);
            } else {
                return null;
            }

            task.status = "1".equals(isDone) ? Status.DONE : Status.NOT_DONE;
            return task;
        } catch (Exception e) {
            return null; // corrupted line -> skip
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ArrayList<Task> tasks = loadTasksFromDisk();

        // Greeting
        System.out.println(LINE);
        System.out.println("Hello! I'm Bob");
        System.out.println("What can I do for you?");
        System.out.println(LINE);

        while (true) {
            String input = sc.nextLine().trim();

            // Exit
            if (input.equalsIgnoreCase("bye")) {
                System.out.println(LINE);
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(LINE);
                break;
            }

            // LIST
            if (input.equals("list")) {
                System.out.println(LINE);
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < tasks.size(); i++) {
                    System.out.println((i + 1) + "." + tasks.get(i));
                }
                System.out.println(LINE);
                continue;
            }

            // MARK
            if (input.startsWith("mark ")) {
                int idx = parseIndex(input, "mark ");
                if (idx >= 0 && idx < tasks.size()) {
                    Task t = tasks.get(idx);
                    t.status = Status.DONE;
                    saveTasksToDisk(tasks);

                    System.out.println(LINE);
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + t);
                    System.out.println(LINE);
                } else {
                    printError("WRONG!!! That task number does not exist.");
                }
                continue;
            }

            // UNMARK
            if (input.startsWith("unmark ")) {
                int idx = parseIndex(input, "unmark ");
                if (idx >= 0 && idx < tasks.size()) {
                    Task t = tasks.get(idx);
                    t.status = Status.NOT_DONE;
                    saveTasksToDisk(tasks);

                    System.out.println(LINE);
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + t);
                    System.out.println(LINE);
                } else {
                    printError("WRONG!!! That task number does not exist.");
                }
                continue;
            }

            // DELETE
            if (input.equals("delete")) {
                printError("WRONG!!! Please specify a task number to delete.");
                continue;
            }

            if (input.startsWith("delete ")) {
                int idx = parseIndex(input, "delete ");
                if (idx >= 0 && idx < tasks.size()) {
                    Task removed = tasks.remove(idx);
                    saveTasksToDisk(tasks);

                    System.out.println(LINE);
                    System.out.println("Noted. I've removed this task:");
                    System.out.println("  " + removed);
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                    System.out.println(LINE);
                } else {
                    printError("WRONG!!! That task number does not exist.");
                }
                continue;
            }

            // TODO (handles empty todo)
            if (input.equals("todo")) {
                printError("WRONG!!! Add a description for your todo.");
                continue;
            }

            if (input.startsWith("todo ")) {
                String desc = input.substring("todo ".length()).trim();
                if (desc.isEmpty()) {
                    printError("WRONG!!! Add a description for your todo.");
                    continue;
                }

                Task t = new Todo(desc);
                tasks.add(t);
                saveTasksToDisk(tasks);
                printAdded(t, tasks.size());
                continue;
            }

            // DEADLINE (handles missing /by)
            if (input.equals("deadline")) {
                printError("WRONG!!! Add a description for your deadline task.");
                continue;
            }

            if (input.startsWith("deadline ")) {
                String rest = input.substring("deadline ".length()).trim();
                int byPos = rest.indexOf("/by");

                if (byPos == -1) {
                    printError("WRONG!!! A deadline must have '/by <time>'");
                    continue;
                }

                String desc = rest.substring(0, byPos).trim();
                String by = rest.substring(byPos + 3).trim();

                if (desc.isEmpty()) {
                    printError("WRONG!!! Add a description for your deadline task.");
                    continue;
                }
                if (by.isEmpty()) {
                    printError("WRONG!!! The deadline time cannot be empty.");
                    continue;
                }

                Task t = new Deadline(desc, by);
                tasks.add(t);
                saveTasksToDisk(tasks);
                printAdded(t, tasks.size());
                continue;
            }

            // EVENT (handles missing /from or /to)
            if (input.equals("event")) {
                printError("WRONG!!! Add a description for your event.");
                continue;
            }

            if (input.startsWith("event ")) {
                String rest = input.substring("event ".length()).trim();

                int fromPos = rest.indexOf("/from");
                int toPos = rest.indexOf("/to");

                if (fromPos == -1 || toPos == -1 || toPos < fromPos) {
                    printError("WRONG!!! An event must have '/from <start> /to <end>'");
                    continue;
                }

                String desc = rest.substring(0, fromPos).trim();
                String from = rest.substring(fromPos + 5, toPos).trim();
                String to = rest.substring(toPos + 3).trim();

                if (desc.isEmpty()) {
                    printError("WRONG!!! Add a description for your event.");
                    continue;
                }
                if (from.isEmpty() || to.isEmpty()) {
                    printError("WRONG!!! The event start/end time cannot be empty.");
                    continue;
                }

                Task t = new Event(desc, from, to);
                tasks.add(t);
                saveTasksToDisk(tasks);
                printAdded(t, tasks.size());
                continue;
            }

            // Unknown command
            printError("WRONG!!! I'm sorry, but I don't know what that means :-(");
        }
        sc.close();
    }


    /**
     * Prints an error message wrapped with divider lines, following format.
     *
     * @param message Error message to print.
     */
    private static void printError(String message) {
        System.out.println(LINE);
        System.out.println(message);
        System.out.println(LINE);
    }

    /**
     * Prints the confirmation message after a task is added.
     *
     * @param t Task that was added.
     * @param total Total number of tasks after adding.
     */
    private static void printAdded(Task t, int total) {
        System.out.println(LINE);
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + t);
        System.out.println("Now you have " + total + " tasks in the list.");
        System.out.println(LINE);
    }

    /**
     * Returns the 0-based index parsed from a command string that contains a 1-based task number.
     *
     * @param input Full user input.
     * @param prefix Command prefix (e.g., "mark ", "delete ").
     * @return 0-based index if parsing succeeds, otherwise -1.
     */
    private static int parseIndex(String input, String prefix) {
        try {
            String numberPart = input.substring(prefix.length()).trim();
            int oneBased = Integer.parseInt(numberPart);
            return oneBased - 1;
        } catch (Exception e) {
            return -1;
        }
    }
}
