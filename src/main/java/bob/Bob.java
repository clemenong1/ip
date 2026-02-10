package bob;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import bob.parser.Parser;
import bob.storage.Storage;
import bob.task.Task;
import bob.task.TaskList;
import bob.tasktype.Deadline;
import bob.tasktype.Event;
import bob.tasktype.Todo;
import bob.ui.Ui;
import bob.util.DateTimeUtil;

/**
 * Runs the Bob chatbot application that manages a list of tasks.
 */
public class Bob {
    private Storage storage;
    private TaskList tasks;
    private Ui ui;

    /**
     * Creates a new Bob instance with the given file path.
     *
     * @param filePath Path to the file for storing tasks.
     */
    public Bob(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (IOException e) {
            ui.showError("Could not load tasks: " + e.getMessage());
            tasks = new TaskList();
        }
    }

    /**
     * Runs the Bob chatbot application.
     */
    public void run() {
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
                int idx = Parser.parseIndex(input, "mark ");
                if (tasks.isValidIndex(idx)) {
                    Task t = tasks.get(idx);
                    t.setStatus(Task.Status.DONE);
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
                int idx = Parser.parseIndex(input, "unmark ");
                if (tasks.isValidIndex(idx)) {
                    Task t = tasks.get(idx);
                    t.setStatus(Task.Status.NOT_DONE);
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
                int idx = Parser.parseIndex(input, "delete ");
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
                String desc = Parser.parseTodoDescription(input);
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
                String[] deadlineArgs;
                try {
                    deadlineArgs = Parser.parseDeadlineArgs(input);
                } catch (IllegalArgumentException e) {
                    ui.showError("WRONG!!! " + e.getMessage());
                    continue;
                }

                String desc = deadlineArgs[0];
                String byRaw = deadlineArgs[1];
                assert deadlineArgs.length == 2 : "parseDeadlineArgs returns [desc, byRaw]";

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
                String[] eventArgs;
                try {
                    eventArgs = Parser.parseEventArgs(input);
                } catch (IllegalArgumentException e) {
                    ui.showError("WRONG!!! " + e.getMessage());
                    continue;
                }

                String desc = eventArgs[0];
                String fromRaw = eventArgs[1];
                String toRaw = eventArgs[2];
                assert eventArgs.length == 3 : "parseEventArgs returns [desc, fromRaw, toRaw]";

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

            // FIND (search tasks by keyword in description)
            if (input.equals("find")) {
                ui.showError("WRONG!!! Please specify a keyword to search for.");
                continue;
            }

            if (input.startsWith("find ")) {
                String keyword = Parser.parseFindKeyword(input);
                ArrayList<Task> matchingTasks = tasks.findTasksByKeyword(keyword);
                ui.showMatchingTasks(matchingTasks);
                continue;
            }

            // ON (lists deadlines/events on a specific date)
            if (input.startsWith("on ")) {
                LocalDate date;
                try {
                    date = Parser.parseOnDate(input);

                    LocalDateTime start = date.atStartOfDay();
                    LocalDateTime end = date.plusDays(1).atStartOfDay().minusNanos(1);

                    ArrayList<Task> matchingTasks = new ArrayList<>();
                    for (int i = 0; i < tasks.size(); i++) {
                        Task t = tasks.get(i);

                        boolean matches = false;

                        if (t instanceof Deadline) {
                            Deadline d = (Deadline) t;
                            matches = d.getBy().toLocalDate().equals(date);
                        } else if (t instanceof Event) {
                            Event e = (Event) t;
                            // overlaps the day
                            matches = !e.getTo().isBefore(start) && !e.getFrom().isAfter(end);
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
     * Gets a response from Bob for the given user input.
     * Used by the GUI to process commands and return responses as strings.
     *
     * @param input User's command input.
     * @return Bob's response as a String.
     */
    public String getResponse(String input) {
        input = input.trim();

        // Handle "bye" command
        if (input.equalsIgnoreCase("bye")) {
            return "Bye. Hope to see you again soon!";
        }

        // LIST
        if (input.equals("list")) {
            return formatTaskList(tasks.getAllTasks());
        }

        // MARK
        if (input.startsWith("mark ")) {
            int idx = Parser.parseIndex(input, "mark ");
            if (tasks.isValidIndex(idx)) {
                Task t = tasks.get(idx);
                t.setStatus(Task.Status.DONE);
                try {
                    storage.save(tasks.getAllTasks());
                } catch (IOException e) {
                    return "Could not save tasks: " + e.getMessage();
                }
                return "Nice! I've marked this task as done:\n  " + t;
            }
            return "WRONG!!! That task number does not exist.";
        }

        // UNMARK
        if (input.startsWith("unmark ")) {
            int idx = Parser.parseIndex(input, "unmark ");
            if (tasks.isValidIndex(idx)) {
                Task t = tasks.get(idx);
                t.setStatus(Task.Status.NOT_DONE);
                try {
                    storage.save(tasks.getAllTasks());
                } catch (IOException e) {
                    return "Could not save tasks: " + e.getMessage();
                }
                return "OK, I've marked this task as not done yet:\n  " + t;
            }
            return "WRONG!!! That task number does not exist.";
        }

        // DELETE
        if (input.equals("delete")) {
            return "WRONG!!! Please specify a task number to delete.";
        }

        if (input.startsWith("delete ")) {
            int idx = Parser.parseIndex(input, "delete ");
            if (tasks.isValidIndex(idx)) {
                Task removed = tasks.remove(idx);
                try {
                    storage.save(tasks.getAllTasks());
                } catch (IOException e) {
                    return "Could not save tasks: " + e.getMessage();
                }
                return "Noted. I've removed this task:\n  " + removed + "\nNow you have "
                        + tasks.size() + " tasks in the list.";
            }
            return "WRONG!!! That task number does not exist.";
        }

        // TODO
        if (input.equals("todo")) {
            return "WRONG!!! Add a description for your todo.";
        }

        if (input.startsWith("todo ")) {
            String desc = Parser.parseTodoDescription(input);
            if (desc.isEmpty()) {
                return "WRONG!!! Add a description for your todo.";
            }
            Task t = new Todo(desc);
            tasks.add(t);
            try {
                storage.save(tasks.getAllTasks());
            } catch (IOException e) {
                return "Could not save tasks: " + e.getMessage();
            }
            return "Got it. I've added this task:\n  " + t + "\nNow you have "
                    + tasks.size() + " tasks in the list.";
        }

        // DEADLINE
        if (input.equals("deadline")) {
            return "WRONG!!! Add a description for your deadline task.";
        }

        if (input.startsWith("deadline ")) {
            String[] deadlineArgs;
            try {
                deadlineArgs = Parser.parseDeadlineArgs(input);
            } catch (IllegalArgumentException e) {
                return "WRONG!!! " + e.getMessage();
            }

            String desc = deadlineArgs[0];
            String byRaw = deadlineArgs[1];
            assert deadlineArgs.length == 2 : "parseDeadlineArgs returns [desc, byRaw]";

            try {
                LocalDateTime by = DateTimeUtil.parseUserDateTime(byRaw);
                Task t = new Deadline(desc, by);
                tasks.add(t);
                try {
                    storage.save(tasks.getAllTasks());
                } catch (IOException e) {
                    return "Could not save tasks: " + e.getMessage();
                }
                return "Got it. I've added this task:\n  " + t + "\nNow you have "
                        + tasks.size() + " tasks in the list.";
            } catch (DateTimeParseException e) {
                return "WRONG!!! Invalid date/time.\nUse formats like:\n  2019-10-15\n  "
                        + "2019-10-15 1800\n  2/12/2019 1800";
            }
        }

        // EVENT
        if (input.equals("event")) {
            return "WRONG!!! Add a description for your event.";
        }

        if (input.startsWith("event ")) {
            String[] eventArgs;
            try {
                eventArgs = Parser.parseEventArgs(input);
            } catch (IllegalArgumentException e) {
                return "WRONG!!! " + e.getMessage();
            }

            String desc = eventArgs[0];
            String fromRaw = eventArgs[1];
            String toRaw = eventArgs[2];
            assert eventArgs.length == 3 : "parseEventArgs returns [desc, fromRaw, toRaw]";

            try {
                LocalDateTime from = DateTimeUtil.parseUserDateTime(fromRaw);
                LocalDateTime to = DateTimeUtil.parseUserDateTime(toRaw);

                if (to.isBefore(from)) {
                    return "WRONG!!! Event end time must be after start time.";
                }

                Task t = new Event(desc, from, to);
                tasks.add(t);
                try {
                    storage.save(tasks.getAllTasks());
                } catch (IOException e) {
                    return "Could not save tasks: " + e.getMessage();
                }
                return "Got it. I've added this task:\n  " + t + "\nNow you have "
                        + tasks.size() + " tasks in the list.";
            } catch (DateTimeParseException e) {
                return "WRONG!!! Invalid date/time.\nUse formats like:\n  2019-10-15\n  "
                        + "2019-10-15 1800\n  2/12/2019 1800";
            }
        }

        // FIND
        if (input.equals("find")) {
            return "WRONG!!! Please specify a keyword to search for.";
        }

        if (input.startsWith("find ")) {
            String keyword = Parser.parseFindKeyword(input);
            ArrayList<Task> matchingTasks = tasks.findTasksByKeyword(keyword);
            return formatMatchingTasks(matchingTasks);
        }

        // ON (lists deadlines/events on a specific date)
        if (input.startsWith("on ")) {
            try {
                LocalDate date = Parser.parseOnDate(input);
                LocalDateTime start = date.atStartOfDay();
                LocalDateTime end = date.plusDays(1).atStartOfDay().minusNanos(1);

                ArrayList<Task> matchingTasks = new ArrayList<>();
                for (int i = 0; i < tasks.size(); i++) {
                    Task t = tasks.get(i);
                    boolean matches = false;

                    if (t instanceof Deadline) {
                        Deadline d = (Deadline) t;
                        matches = d.getBy().toLocalDate().equals(date);
                    } else if (t instanceof Event) {
                        Event e = (Event) t;
                        matches = !e.getTo().isBefore(start) && !e.getFrom().isAfter(end);
                    }

                    if (matches) {
                        matchingTasks.add(t);
                    }
                }

                return formatTasksOnDate(date, matchingTasks);
            } catch (DateTimeParseException e) {
                return "WRONG!!! Invalid date.\nUse: on yyyy-mm-dd (e.g., on 2019-12-02)";
            }
        }

        // Unknown command
        return "WRONG!!! I'm sorry, but I don't know what that means :-(";
    }

    /**
     * Formats the task list for display.
     */
    private String formatTaskList(ArrayList<Task> taskList) {
        assert taskList != null : "taskList must not be null";
        StringBuilder sb = new StringBuilder("Here are the tasks in your list:\n");
        for (int i = 0; i < taskList.size(); i++) {
            sb.append((i + 1)).append(".").append(taskList.get(i)).append("\n");
        }
        return sb.toString().trim();
    }

    /**
     * Formats matching tasks from a find search.
     */
    private String formatMatchingTasks(ArrayList<Task> matchingTasks) {
        assert matchingTasks != null : "matchingTasks must not be null";
        StringBuilder sb = new StringBuilder("Here are the matching tasks in your list:\n");
        for (int i = 0; i < matchingTasks.size(); i++) {
            sb.append((i + 1)).append(".").append(matchingTasks.get(i)).append("\n");
        }
        return sb.toString().trim();
    }

    /**
     * Formats tasks occurring on a specific date.
     */
    private String formatTasksOnDate(LocalDate date, ArrayList<Task> taskList) {
        assert date != null && taskList != null : "date and taskList must not be null";
        StringBuilder sb = new StringBuilder("Here are the tasks occurring on ");
        sb.append(DateTimeUtil.formatDateForDisplay(date)).append(":\n");

        if (taskList.isEmpty()) {
            sb.append("No matching tasks.");
        } else {
            for (int i = 0; i < taskList.size(); i++) {
                sb.append((i + 1)).append(".").append(taskList.get(i)).append("\n");
            }
        }
        return sb.toString().trim();
    }

    /**
     * Entry point for the Bob chatbot application.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        new Bob("data/bob.txt").run();
    }
}
