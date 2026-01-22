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

        @Override
        public String toString() {
            return "[" + statusIcon() + "] " + description;
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

            Task newTask = new Task(input);
            tasks.add(newTask);

            System.out.println(LINE);
            System.out.println("added: " + input);
            System.out.println(LINE);
        }
        sc.close();
    }

    // Helper
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
