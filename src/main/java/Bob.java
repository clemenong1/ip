import java.util.Scanner;
import java.util.ArrayList;

public class Bob {
    private static final String LINE = "____________________________________________________________";

    // Task class to store description + done status
    static class Task {
        String description;
        boolean isDone;

        Task(String description) {
            this.description = description;
            this.isDone = false;
        }

        String statusIcon() {
            return isDone ? "X" : " ";
        }

        // Each subclass will override this
        String typeIcon() {
            return "";
        }

        @Override
        public String toString() {
            // default: no type
            return "[" + statusIcon() + "] " + description;
        }
    }

    static class Todo extends Task {
        Todo(String description) {
            super(description);
        }

        @Override
        public String toString() {
            return "[T]" + "[" + statusIcon() + "] " + description;
        }
    }

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

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ArrayList<Task> tasks = new ArrayList<>();

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
                    t.isDone = true;

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
                    t.isDone = false;

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
                printAdded(t, tasks.size());
                continue;
            }

            // Unknown command
            printError("WRONG!!! I'm sorry, but I don't know what that means :-(");
        }
        sc.close();
    }

    // Helpers
    private static void printError(String message) {
        System.out.println(LINE);
        System.out.println(message);
        System.out.println(LINE);
    }

    private static void printAdded(Task t, int total) {
        System.out.println(LINE);
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + t);
        System.out.println("Now you have " + total + " tasks in the list.");
        System.out.println(LINE);
    }

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
