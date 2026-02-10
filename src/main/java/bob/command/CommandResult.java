package bob.command;

import java.time.LocalDate;
import java.util.ArrayList;

import bob.task.Task;

/**
 * Represents the result of processing a user command.
 * Used to unify CLI and GUI response handling.
 */
public class CommandResult {
    /** Indicates the user requested to exit. */
    public static final CommandResult EXIT = new CommandResult(ResultType.EXIT, null, null, null);

    /** Error message for unknown command. */
    public static final String UNKNOWN_COMMAND_ERROR =
            "WRONG!!! I'm sorry, but I don't know what that means :-(";

    /** Hint for invalid date/time format. */
    public static final String DATE_TIME_FORMAT_HINT =
            "WRONG!!! Invalid date/time.\nUse formats like:\n  2019-10-15\n  2019-10-15 1800\n  2/12/2019 1800";

    /** Hint for invalid date format (on command). */
    public static final String DATE_FORMAT_HINT =
            "WRONG!!! Invalid date.\nUse: on yyyy-mm-dd (e.g., on 2019-12-02)";

    /**
     * Type of result produced by processing a command.
     */
    public enum ResultType {
        EXIT,
        LIST,
        MATCHING_TASKS,
        TASKS_ON_DATE,
        MESSAGE,
        ERROR
    }

    private final ResultType type;
    private final String message;
    private final ArrayList<Task> taskList;
    private final LocalDate date;

    private CommandResult(ResultType type, String message, ArrayList<Task> taskList, LocalDate date) {
        this.type = type;
        this.message = message;
        this.taskList = taskList;
        this.date = date;
    }

    public static CommandResult list(ArrayList<Task> tasks) {
        return new CommandResult(ResultType.LIST, null, tasks, null);
    }

    public static CommandResult matchingTasks(ArrayList<Task> tasks) {
        return new CommandResult(ResultType.MATCHING_TASKS, null, tasks, null);
    }

    public static CommandResult tasksOnDate(LocalDate date, ArrayList<Task> tasks) {
        return new CommandResult(ResultType.TASKS_ON_DATE, null, tasks, date);
    }

    public static CommandResult message(String msg) {
        return new CommandResult(ResultType.MESSAGE, msg, null, null);
    }

    public static CommandResult error(String msg) {
        return new CommandResult(ResultType.ERROR, msg, null, null);
    }

    public ResultType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<Task> getTaskList() {
        return taskList;
    }

    public LocalDate getDate() {
        return date;
    }
}
