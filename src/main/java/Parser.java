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
        try {
            String numberPart = input.substring(prefix.length()).trim();
            int oneBased = Integer.parseInt(numberPart);
            return oneBased - 1;
        } catch (Exception e) {
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

        return new String[]{desc, byRaw};
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

        return new String[]{desc, fromRaw, toRaw};
    }

    /**
     * Parses the date from an "on" command.
     *
     * @param input Full user input.
     * @return LocalDate parsed from the command.
     * @throws DateTimeParseException If the date format is invalid.
     */
    public static LocalDate parseOnDate(String input) throws DateTimeParseException {
        String dateRaw = input.substring("on ".length()).trim();
        return LocalDate.parse(dateRaw, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
