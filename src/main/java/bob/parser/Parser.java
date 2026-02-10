package bob.parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Parses user commands and extracts relevant information from them.
 */
public class Parser {
    /**
     * Parses a 0-based index from a command string that contains a 1-based task number.
     *
     * @param input Full user input.
     * @param prefix Command prefix (e.g., "mark ", "delete ").
     * @return 0-based index if parsing succeeds, otherwise -1.
     */
    public static int parseIndex(String input, String prefix) {
        assert input != null && prefix != null && !prefix.isEmpty() && input.startsWith(prefix)
                : "input must start with prefix";
        try {
            String numberPart = input.substring(prefix.length()).trim();
            int oneBased = Integer.parseInt(numberPart);
            return oneBased - 1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Parses the description from a todo command.
     *
     * @param input Full user input.
     * @return Description extracted from the command.
     */
    public static String parseTodoDescription(String input) {
        assert input != null && input.startsWith("todo ")
                : "input must start with 'todo '";
        return input.substring("todo ".length()).trim();
    }

    /**
     * Parses the description and deadline time from a deadline command.
     *
     * @param input Full user input.
     * @return Array containing description at index 0 and deadline time string at index 1.
     * @throws IllegalArgumentException If the command format is invalid.
     */
    public static String[] parseDeadlineArgs(String input) throws IllegalArgumentException {
        assert input != null && input.startsWith("deadline ")
                : "input must start with 'deadline '";
        String rest = input.substring("deadline ".length()).trim();
        int byPos = rest.indexOf("/by");

        if (byPos == -1) {
            throw new IllegalArgumentException("A deadline must have '/by <time>'");
        }

        String desc = rest.substring(0, byPos).trim();
        String byRaw = rest.substring(byPos + 3).trim();

        if (desc.isEmpty()) {
            throw new IllegalArgumentException("Add a description for your deadline task.");
        }
        if (byRaw.isEmpty()) {
            throw new IllegalArgumentException("The deadline time cannot be empty.");
        }

        String[] result = new String[]{desc, byRaw};
        assert result.length == 2 && !result[0].isEmpty() && !result[1].isEmpty()
                : "parseDeadlineArgs must return [desc, byRaw]";
        return result;
    }

    /**
     * Parses the description, start time, and end time from an event command.
     *
     * @param input Full user input.
     * @return Array containing description at index 0, start time string at index 1,
     *         and end time string at index 2.
     * @throws IllegalArgumentException If the command format is invalid.
     */
    public static String[] parseEventArgs(String input) throws IllegalArgumentException {
        assert input != null && input.startsWith("event ")
                : "input must start with 'event '";
        String rest = input.substring("event ".length()).trim();

        int fromPos = rest.indexOf("/from");
        int toPos = rest.indexOf("/to");

        if (fromPos == -1 || toPos == -1 || toPos < fromPos) {
            throw new IllegalArgumentException("An event must have '/from <start> /to <end>'");
        }

        String desc = rest.substring(0, fromPos).trim();
        String fromRaw = rest.substring(fromPos + 5, toPos).trim();
        String toRaw = rest.substring(toPos + 3).trim();

        if (desc.isEmpty()) {
            throw new IllegalArgumentException("Add a description for your event.");
        }
        if (fromRaw.isEmpty() || toRaw.isEmpty()) {
            throw new IllegalArgumentException("The event start/end time cannot be empty.");
        }

        String[] result = new String[]{desc, fromRaw, toRaw};
        assert result.length == 3 && !result[0].isEmpty() && !result[1].isEmpty() && !result[2].isEmpty()
                : "parseEventArgs must return [desc, fromRaw, toRaw]";
        return result;
    }

    /**
     * Parses the search keyword from a find command.
     *
     * @param input Full user input (e.g. "find book").
     * @return The trimmed keyword to search for.
     */
    public static String parseFindKeyword(String input) {
        assert input != null && input.startsWith("find ")
                : "input must start with 'find '";
        return input.substring("find ".length()).trim();
    }

    /**
     * Parses the date from an "on" command.
     *
     * @param input Full user input.
     * @return LocalDate parsed from the command.
     * @throws DateTimeParseException If the date format is invalid.
     */
    public static LocalDate parseOnDate(String input) throws DateTimeParseException {
        assert input != null && input.startsWith("on ")
                : "input must start with 'on '";
        String dateRaw = input.substring("on ".length()).trim();
        return LocalDate.parse(dateRaw, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
