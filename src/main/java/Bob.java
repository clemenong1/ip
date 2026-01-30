import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Scanner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Runs the Bob chatbot application that manages a list of tasks.
 */
public class Bob {
    private static final String LINE = "____________________________________________________________";
    private static final Path DATA_DIRECTORY = Paths.get("data");
    private static final Path DATA_FILE_PATH = DATA_DIRECTORY.resolve("duke.txt");

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
        String isDone = (task.status == Task.Status.DONE) ? "1" : "0";

        if (task instanceof Todo) {
            return "T | " + isDone + " | " + task.description;
        }

        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return "D | " + isDone + " | " + deadline.description + " | " + deadline.by.format(DateTimeUtil.STORAGE_DATE_TIME);
        }

        if (task instanceof Event) {
            Event event = (Event) task;
            return "E | " + isDone + " | " + event.description
                    + " | " + event.from.format(DateTimeUtil.STORAGE_DATE_TIME)
                    + " | " + event.to.format(DateTimeUtil.STORAGE_DATE_TIME);
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
                LocalDateTime by = LocalDateTime.parse(parts[3], DateTimeUtil.STORAGE_DATE_TIME);
                task = new Deadline(description, by);
            } else if ("E".equals(type)) {
                if (parts.length < 5) {
                    return null;
                }
                LocalDateTime from = LocalDateTime.parse(parts[3], DateTimeUtil.STORAGE_DATE_TIME);
                LocalDateTime to = LocalDateTime.parse(parts[4], DateTimeUtil.STORAGE_DATE_TIME);
                task = new Event(description, from, to);
            } else {
                return null;
            }

            task.status = "1".equals(isDone) ? Task.Status.DONE : Task.Status.NOT_DONE;
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
                    t.status = Task.Status.DONE;
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
                    t.status = Task.Status.NOT_DONE;
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
                String byRaw = rest.substring(byPos + 3).trim();

                if (desc.isEmpty()) {
                    printError("WRONG!!! Add a description for your deadline task.");
                    continue;
                }
                if (byRaw.isEmpty()) {
                    printError("WRONG!!! The deadline time cannot be empty.");
                    continue;
                }

                try {
                    LocalDateTime by = DateTimeUtil.parseUserDateTime(byRaw);
                    Task t = new Deadline(desc, by);
                    tasks.add(t);
                    saveTasksToDisk(tasks);
                    printAdded(t, tasks.size());
                } catch (DateTimeParseException e) {
                    printError("WRONG!!! Invalid date/time.\n"
                            + "Use formats like:\n"
                            + "  2019-10-15\n"
                            + "  2019-10-15 1800\n"
                            + "  2/12/2019 1800");
                }
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
                String fromRaw = rest.substring(fromPos + 5, toPos).trim();
                String toRaw = rest.substring(toPos + 3).trim();

                if (desc.isEmpty()) {
                    printError("WRONG!!! Add a description for your event.");
                    continue;
                }
                if (fromRaw.isEmpty() || toRaw.isEmpty()) {
                    printError("WRONG!!! The event start/end time cannot be empty.");
                    continue;
                }

                try {
                    LocalDateTime from = DateTimeUtil.parseUserDateTime(fromRaw);
                    LocalDateTime to = DateTimeUtil.parseUserDateTime(toRaw);

                    if (to.isBefore(from)) {
                        printError("WRONG!!! Event end time must be after start time.");
                        continue;
                    }

                    Task t = new Event(desc, from, to);
                    tasks.add(t);
                    saveTasksToDisk(tasks);
                    printAdded(t, tasks.size());
                } catch (DateTimeParseException e) {
                    printError("WRONG!!! Invalid date/time.\n"
                            + "Use formats like:\n"
                            + "  2019-10-15\n"
                            + "  2019-10-15 1800\n"
                            + "  2/12/2019 1800");
                }
                continue;
            }

            // ON (lists deadlines/events on a specific date)
            if (input.startsWith("on ")) {
                String dateRaw = input.substring("on ".length()).trim();
                try {
                    LocalDate date = LocalDate.parse(dateRaw, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                    System.out.println(LINE);
                    System.out.println("Here are the tasks occurring on " + DateTimeUtil.formatDateForDisplay(date) + ":");

                    LocalDateTime start = date.atStartOfDay();
                    LocalDateTime end = date.plusDays(1).atStartOfDay().minusNanos(1);

                    int count = 0;
                    for (int i = 0; i < tasks.size(); i++) {
                        Task t = tasks.get(i);

                        boolean matches = false;

                        if (t instanceof Deadline) {
                            Deadline d = (Deadline) t;
                            matches = d.by.toLocalDate().equals(date);
                        } else if (t instanceof Event) {
                            Event e = (Event) t;
                            // overlaps the day
                            matches = !e.to.isBefore(start) && !e.from.isAfter(end);
                        }

                        if (matches) {
                            System.out.println((i + 1) + "." + t);
                            count++;
                        }
                    }

                    if (count == 0) {
                        System.out.println("No matching tasks.");
                    }

                    System.out.println(LINE);
                } catch (DateTimeParseException e) {
                    printError("WRONG!!! Invalid date.\nUse: on yyyy-mm-dd (e.g., on 2019-12-02)");
                }
                continue;
            }
            // Unknown command
            printError("WRONG!!! I'm sorry, but I don't know what that means :-(");
        }
        sc.close();
    }


    /**
     * Prints an error message wrapped with divider lines.
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
     * Parses a 0-based index from a command string that contains a 1-based task number.
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
