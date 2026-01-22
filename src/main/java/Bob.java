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

            if (input.equals("bye") || input.equals("Bye")) {
                System.out.println(LINE);
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(LINE);
                break;
            }
            if (input.equals("BYE")) {
                System.out.println(LINE);
                System.out.println("BYE. HOPE TO SEE YOU AGAIN SOON!");
                System.out.println(LINE);
                break;
            }

            // LIST
            if (input.equals("list")) {
                System.out.println(LINE);
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < tasks.size(); i++) {
                    System.out.println((i + 1) + "." + tasks.get(i)); // e.g. 1.[ ] read book
                }
                System.out.println(LINE);
                continue;
            }

            // MARK
            if (input.startsWith("mark ")) {
                int idx = parseIndex(input, "mark "); // 0-based
                if (idx >= 0 && idx < tasks.size()) {
                    Task t = tasks.get(idx);
                    t.isDone = true;

                    System.out.println(LINE);
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + t);
                    System.out.println(LINE);
                }
                continue;
            }

            // UNMARK
            if (input.startsWith("unmark ")) {
                int idx = parseIndex(input, "unmark "); // 0-based
                if (idx >= 0 && idx < tasks.size()) {
                    Task t = tasks.get(idx);
                    t.isDone = false;

                    System.out.println(LINE);
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + t);
                    System.out.println(LINE);
                }
                continue;
            }

            if (input.startsWith("todo ")) {
                String desc = input.substring("todo ".length()).trim();
                Task t = new Todo(desc);
                tasks.add(t);
                printAdded(t, tasks.size());
                continue;
            }

            if (input.startsWith("deadline ")) {
                String rest = input.substring("deadline ".length()).trim();

                int byPos = rest.indexOf("/by");
                if (byPos != -1) {
                    String desc = rest.substring(0, byPos).trim();
                    String by = rest.substring(byPos + 3).trim(); // 3 = length of "/by"
                    Task t = new Deadline(desc, by);
                    tasks.add(t);
                    printAdded(t, tasks.size());
                }
                continue;
            }

            if (input.startsWith("event ")) {
                String rest = input.substring("event ".length()).trim();

                int fromPos = rest.indexOf("/from");
                int toPos = rest.indexOf("/to");

                if (fromPos != -1 && toPos != -1 && toPos > fromPos) {
                    String desc = rest.substring(0, fromPos).trim();
                    String from = rest.substring(fromPos + 5, toPos).trim(); // 5 = "/from"
                    String to = rest.substring(toPos + 3).trim();            // 3 = "/to"

                    Task t = new Event(desc, from, to);
                    tasks.add(t);
                    printAdded(t, tasks.size());
                }
                continue;
            }

            // if it is not a command
            System.out.println(LINE);
            System.out.println(input);
            System.out.println(LINE);
        }
        sc.close();
    }

    // Helpers
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
