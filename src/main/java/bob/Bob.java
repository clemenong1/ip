package bob;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import bob.command.CommandResult;
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
    private static final String ERROR_TASK_NOT_FOUND = "WRONG!!! That task number does not exist.";
    private static final String ERROR_DELETE_SPECIFY = "WRONG!!! Please specify a task number to delete.";
    private static final String ERROR_TODO_DESC = "WRONG!!! Add a description for your todo.";
    private static final String ERROR_DEADLINE_DESC = "WRONG!!! Add a description for your deadline task.";
    private static final String ERROR_EVENT_DESC = "WRONG!!! Add a description for your event.";
    private static final String ERROR_FIND_KEYWORD = "WRONG!!! Please specify a keyword to search for.";
    private static final String ERROR_EVENT_END_AFTER_START = "WRONG!!! Event end time must be after start time.";

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
        ui.showWelcome();

        while (true) {
            String input = ui.readCommand();
            CommandResult result = processCommand(input);

            if (result.getType() == CommandResult.ResultType.EXIT) {
                ui.showGoodbye();
                break;
            }

            showUrgentTasksIfAny();
            dispatchToUi(result);
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
        CommandResult result = processCommand(input);

        if (result.getType() == CommandResult.ResultType.EXIT) {
            return "Bye. Hope to see you again soon!";
        }
        String response = formatResult(result);
        String urgentPrefix = formatUrgentTasks();
        if (!urgentPrefix.isEmpty()) {
            response = urgentPrefix + "\n\n" + response;
        }
        return response;
    }

    /**
     * Processes the user input and returns a CommandResult.
     */
    private CommandResult processCommand(String input) {
        if (input.equalsIgnoreCase("bye")) {
            return CommandResult.EXIT;
        }
        if (input.equals("list")) {
            return CommandResult.list(tasks.getAllTasks());
        }
        if (input.startsWith(Parser.PREFIX_MARK)) {
            return handleMark(input);
        }
        if (input.startsWith(Parser.PREFIX_UNMARK)) {
            return handleUnmark(input);
        }
        if (input.equals("delete")) {
            return CommandResult.error(ERROR_DELETE_SPECIFY);
        }
        if (input.startsWith(Parser.PREFIX_DELETE)) {
            return handleDelete(input);
        }
        if (input.equals("todo")) {
            return CommandResult.error(ERROR_TODO_DESC);
        }
        if (input.startsWith(Parser.PREFIX_TODO)) {
            return handleTodo(input);
        }
        if (input.equals("deadline")) {
            return CommandResult.error(ERROR_DEADLINE_DESC);
        }
        if (input.startsWith(Parser.PREFIX_DEADLINE)) {
            return handleDeadline(input);
        }
        if (input.equals("event")) {
            return CommandResult.error(ERROR_EVENT_DESC);
        }
        if (input.startsWith(Parser.PREFIX_EVENT)) {
            return handleEvent(input);
        }
        if (input.equals("find")) {
            return CommandResult.error(ERROR_FIND_KEYWORD);
        }
        if (input.startsWith(Parser.PREFIX_FIND)) {
            return handleFind(input);
        }
        if (input.startsWith(Parser.PREFIX_ON)) {
            return handleOn(input);
        }
        return CommandResult.error(CommandResult.UNKNOWN_COMMAND_ERROR);
    }

    private void dispatchToUi(CommandResult result) {
        switch (result.getType()) {
        case LIST:
            ui.showTaskList(result.getTaskList());
            break;
        case MATCHING_TASKS:
            ui.showMatchingTasks(result.getTaskList());
            break;
        case TASKS_ON_DATE:
            ui.showTasksOnDate(result.getDate(), result.getTaskList());
            break;
        case MESSAGE:
            ui.showMessage(result.getMessage());
            break;
        case ERROR:
            ui.showError(result.getMessage());
            break;
        default:
            break;
        }
    }

    private String formatResult(CommandResult result) {
        switch (result.getType()) {
        case LIST:
            return formatTaskList(result.getTaskList());
        case MATCHING_TASKS:
            return formatMatchingTasks(result.getTaskList());
        case TASKS_ON_DATE:
            return formatTasksOnDate(result.getDate(), result.getTaskList());
        case MESSAGE:
        case ERROR:
            return result.getMessage();
        default:
            return "";
        }
    }

    private CommandResult handleMark(String input) {
        int idx = Parser.parseIndex(input, Parser.PREFIX_MARK);
        if (!tasks.isValidIndex(idx)) {
            return CommandResult.error(ERROR_TASK_NOT_FOUND);
        }
        Task task = tasks.get(idx);
        task.setStatus(Task.Status.DONE);
        String saveError = saveTasks();
        if (saveError != null) {
            return CommandResult.error(saveError);
        }
        return CommandResult.message("Nice! I've marked this task as done:\n  " + task);
    }

    private CommandResult handleUnmark(String input) {
        int idx = Parser.parseIndex(input, Parser.PREFIX_UNMARK);
        if (!tasks.isValidIndex(idx)) {
            return CommandResult.error(ERROR_TASK_NOT_FOUND);
        }
        Task task = tasks.get(idx);
        task.setStatus(Task.Status.NOT_DONE);
        String saveError = saveTasks();
        if (saveError != null) {
            return CommandResult.error(saveError);
        }
        return CommandResult.message("OK, I've marked this task as not done yet:\n  " + task);
    }

    private CommandResult handleDelete(String input) {
        int idx = Parser.parseIndex(input, Parser.PREFIX_DELETE);
        if (!tasks.isValidIndex(idx)) {
            return CommandResult.error(ERROR_TASK_NOT_FOUND);
        }
        Task removed = tasks.remove(idx);
        String saveError = saveTasks();
        if (saveError != null) {
            return CommandResult.error(saveError);
        }
        return CommandResult.message("Noted. I've removed this task:\n  " + removed
                + "\nNow you have " + tasks.size() + " tasks in the list.");
    }

    private CommandResult handleTodo(String input) {
        String desc = Parser.parseTodoDescription(input);
        if (desc.isEmpty()) {
            return CommandResult.error(ERROR_TODO_DESC);
        }
        Task task = new Todo(desc);
        tasks.add(task);
        String saveError = saveTasks();
        if (saveError != null) {
            return CommandResult.error(saveError);
        }
        return CommandResult.message("Got it. I've added this task:\n  " + task
                + "\nNow you have " + tasks.size() + " tasks in the list.");
    }

    private CommandResult handleDeadline(String input) {
        String[] args;
        try {
            args = Parser.parseDeadlineArgs(input);
        } catch (IllegalArgumentException e) {
            return CommandResult.error("WRONG!!! " + e.getMessage());
        }

        try {
            LocalDateTime by = DateTimeUtil.parseUserDateTime(args[1]);
            Task task = new Deadline(args[0], by);
            tasks.add(task);
            String saveError = saveTasks();
            if (saveError != null) {
                return CommandResult.error(saveError);
            }
            return CommandResult.message("Got it. I've added this task:\n  " + task
                    + "\nNow you have " + tasks.size() + " tasks in the list.");
        } catch (DateTimeParseException e) {
            return CommandResult.error(CommandResult.DATE_TIME_FORMAT_HINT);
        }
    }

    private CommandResult handleEvent(String input) {
        String[] args;
        try {
            args = Parser.parseEventArgs(input);
        } catch (IllegalArgumentException e) {
            return CommandResult.error("WRONG!!! " + e.getMessage());
        }

        try {
            LocalDateTime from = DateTimeUtil.parseUserDateTime(args[1]);
            LocalDateTime to = DateTimeUtil.parseUserDateTime(args[2]);
            if (to.isBefore(from)) {
                return CommandResult.error(ERROR_EVENT_END_AFTER_START);
            }
            Task task = new Event(args[0], from, to);
            tasks.add(task);
            String saveError = saveTasks();
            if (saveError != null) {
                return CommandResult.error(saveError);
            }
            return CommandResult.message("Got it. I've added this task:\n  " + task
                    + "\nNow you have " + tasks.size() + " tasks in the list.");
        } catch (DateTimeParseException e) {
            return CommandResult.error(CommandResult.DATE_TIME_FORMAT_HINT);
        }
    }

    private CommandResult handleFind(String input) {
        String keyword = Parser.parseFindKeyword(input);
        ArrayList<Task> matching = tasks.findTasksByKeyword(keyword);
        return CommandResult.matchingTasks(matching);
    }

    private CommandResult handleOn(String input) {
        try {
            LocalDate date = Parser.parseOnDate(input);
            ArrayList<Task> matching = tasks.getTasksOnDate(date);
            return CommandResult.tasksOnDate(date, matching);
        } catch (DateTimeParseException e) {
            return CommandResult.error(CommandResult.DATE_FORMAT_HINT);
        }
    }

    /**
     * Saves tasks to storage.
     * @return Error message if save fails, null on success.
     */
    private String saveTasks() {
        try {
            storage.save(tasks.getAllTasks());
            return null;
        } catch (IOException e) {
            return "Could not save tasks: " + e.getMessage();
        }
    }

    private String formatTaskList(ArrayList<Task> taskList) {
        assert taskList != null : "taskList must not be null";
        return formatNumberedList("Here are the tasks in your list:", taskList);
    }

    private String formatMatchingTasks(ArrayList<Task> matchingTasks) {
        assert matchingTasks != null : "matchingTasks must not be null";
        return formatNumberedList("Here are the matching tasks in your list:", matchingTasks);
    }

    private String formatNumberedList(String header, ArrayList<Task> taskList) {
        StringBuilder sb = new StringBuilder(header).append("\n");
        for (int i = 0; i < taskList.size(); i++) {
            sb.append(i + 1).append(".").append(taskList.get(i)).append("\n");
        }
        return sb.toString().trim();
    }

    private String formatTasksOnDate(LocalDate date, ArrayList<Task> taskList) {
        assert date != null && taskList != null : "date and taskList must not be null";
        String header = "Here are the tasks occurring on "
                + DateTimeUtil.formatDateForDisplay(date) + ":";
        if (taskList.isEmpty()) {
            return header + "\nNo matching tasks.";
        }
        return formatNumberedList(header, taskList);
    }

    /**
     * Returns formatted string of urgent tasks (deadlines within 3 days).
     * Returns empty string if no urgent tasks.
     */
    private String formatUrgentTasks() {
        ArrayList<Task> urgent = tasks.getUrgentTasks();
        if (urgent.isEmpty()) {
            return "";
        }
        return formatNumberedList("URGENT TASKS:", urgent);
    }

    /**
     * Shows urgent tasks in the CLI if any exist.
     */
    private void showUrgentTasksIfAny() {
        String urgent = formatUrgentTasks();
        if (!urgent.isEmpty()) {
            ui.showMessage(urgent);
        }
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
