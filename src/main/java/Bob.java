import java.io.IOException;

import java.util.ArrayList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Runs the Bob chatbot application that manages a list of tasks.
 */
public class Bob {
    private static final String DATA_FILE_PATH = "data/duke.txt";

    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage(DATA_FILE_PATH);
        TaskList tasks;

        try {
            tasks = new TaskList(storage.load());
        } catch (IOException e) {
            ui.showError("Could not load tasks: " + e.getMessage());
            tasks = new TaskList();
        }

        // Greeting
        ui.showWelcome();

        while (true) {
            String input = ui.readCommand();

            // Exit
            if (input.equalsIgnoreCase("bye")) {
                ui.showGoodbye();
                break;
            }

            // LIST
            if (input.equals("list")) {
                ui.showTaskList(tasks.getAllTasks());
                continue;
            }

            // MARK
            if (input.startsWith("mark ")) {
                int idx = parseIndex(input, "mark ");
                if (tasks.isValidIndex(idx)) {
                    Task t = tasks.get(idx);
                    t.status = Task.Status.DONE;
                    try {
                        storage.save(tasks.getAllTasks());
                    } catch (IOException e) {
                        ui.showError("Could not save tasks: " + e.getMessage());
                    }
                    ui.showMarkedTask(t);
                } else {
                    ui.showError("WRONG!!! That task number does not exist.");
                }
                continue;
            }

            // UNMARK
            if (input.startsWith("unmark ")) {
                int idx = parseIndex(input, "unmark ");
                if (tasks.isValidIndex(idx)) {
                    Task t = tasks.get(idx);
                    t.status = Task.Status.NOT_DONE;
                    try {
                        storage.save(tasks.getAllTasks());
                    } catch (IOException e) {
                        ui.showError("Could not save tasks: " + e.getMessage());
                    }
                    ui.showUnmarkedTask(t);
                } else {
                    ui.showError("WRONG!!! That task number does not exist.");
                }
                continue;
            }

            // DELETE
            if (input.equals("delete")) {
                ui.showError("WRONG!!! Please specify a task number to delete.");
                continue;
            }

            if (input.startsWith("delete ")) {
                int idx = parseIndex(input, "delete ");
                if (tasks.isValidIndex(idx)) {
                    Task removed = tasks.remove(idx);
                    try {
                        storage.save(tasks.getAllTasks());
                    } catch (IOException e) {
                        ui.showError("Could not save tasks: " + e.getMessage());
                    }
                    ui.showDeletedTask(removed, tasks.size());
                } else {
                    ui.showError("WRONG!!! That task number does not exist.");
                }
                continue;
            }

            // TODO (handles empty todo)
            if (input.equals("todo")) {
                ui.showError("WRONG!!! Add a description for your todo.");
                continue;
            }

            if (input.startsWith("todo ")) {
                String desc = input.substring("todo ".length()).trim();
                if (desc.isEmpty()) {
                    ui.showError("WRONG!!! Add a description for your todo.");
                    continue;
                }

                Task t = new Todo(desc);
                tasks.add(t);
                try {
                    storage.save(tasks.getAllTasks());
                } catch (IOException e) {
                    ui.showError("Could not save tasks: " + e.getMessage());
                }
                ui.showAddedTask(t, tasks.size());
                continue;
            }

            // DEADLINE (handles missing /by)
            if (input.equals("deadline")) {
                ui.showError("WRONG!!! Add a description for your deadline task.");
                continue;
            }

            if (input.startsWith("deadline ")) {
                String rest = input.substring("deadline ".length()).trim();
                int byPos = rest.indexOf("/by");

                if (byPos == -1) {
                    ui.showError("WRONG!!! A deadline must have '/by <time>'");
                    continue;
                }

                String desc = rest.substring(0, byPos).trim();
                String byRaw = rest.substring(byPos + 3).trim();

                if (desc.isEmpty()) {
                    ui.showError("WRONG!!! Add a description for your deadline task.");
                    continue;
                }
                if (byRaw.isEmpty()) {
                    ui.showError("WRONG!!! The deadline time cannot be empty.");
                    continue;
                }

                try {
                    LocalDateTime by = DateTimeUtil.parseUserDateTime(byRaw);
                    Task t = new Deadline(desc, by);
                    tasks.add(t);
                    try {
                        storage.save(tasks.getAllTasks());
                    } catch (IOException e) {
                        ui.showError("Could not save tasks: " + e.getMessage());
                    }
                    ui.showAddedTask(t, tasks.size());
                } catch (DateTimeParseException e) {
                    ui.showError("WRONG!!! Invalid date/time.\n"
                            + "Use formats like:\n"
                            + "  2019-10-15\n"
                            + "  2019-10-15 1800\n"
                            + "  2/12/2019 1800");
                }
                continue;
            }

            // EVENT (handles missing /from or /to)
            if (input.equals("event")) {
                ui.showError("WRONG!!! Add a description for your event.");
                continue;
            }

            if (input.startsWith("event ")) {
                String rest = input.substring("event ".length()).trim();

                int fromPos = rest.indexOf("/from");
                int toPos = rest.indexOf("/to");

                if (fromPos == -1 || toPos == -1 || toPos < fromPos) {
                    ui.showError("WRONG!!! An event must have '/from <start> /to <end>'");
                    continue;
                }

                String desc = rest.substring(0, fromPos).trim();
                String fromRaw = rest.substring(fromPos + 5, toPos).trim();
                String toRaw = rest.substring(toPos + 3).trim();

                if (desc.isEmpty()) {
                    ui.showError("WRONG!!! Add a description for your event.");
                    continue;
                }
                if (fromRaw.isEmpty() || toRaw.isEmpty()) {
                    ui.showError("WRONG!!! The event start/end time cannot be empty.");
                    continue;
                }

                try {
                    LocalDateTime from = DateTimeUtil.parseUserDateTime(fromRaw);
                    LocalDateTime to = DateTimeUtil.parseUserDateTime(toRaw);

                    if (to.isBefore(from)) {
                        ui.showError("WRONG!!! Event end time must be after start time.");
                        continue;
                    }

                    Task t = new Event(desc, from, to);
                    tasks.add(t);
                    try {
                        storage.save(tasks.getAllTasks());
                    } catch (IOException e) {
                        ui.showError("Could not save tasks: " + e.getMessage());
                    }
                    ui.showAddedTask(t, tasks.size());
                } catch (DateTimeParseException e) {
                    ui.showError("WRONG!!! Invalid date/time.\n"
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

                    LocalDateTime start = date.atStartOfDay();
                    LocalDateTime end = date.plusDays(1).atStartOfDay().minusNanos(1);

                    ArrayList<Task> matchingTasks = new ArrayList<>();
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
                            matchingTasks.add(t);
                        }
                    }

                    ui.showTasksOnDate(date, matchingTasks);
                } catch (DateTimeParseException e) {
                    ui.showError("WRONG!!! Invalid date.\nUse: on yyyy-mm-dd (e.g., on 2019-12-02)");
                }
                continue;
            }
            // Unknown command
            ui.showError("WRONG!!! I'm sorry, but I don't know what that means :-(");
        }
        ui.close();
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
