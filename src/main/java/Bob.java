import java.util.Scanner;
import java.util.ArrayList;

public class Bob {
    private static final String LINE = "____________________________________________________________";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ArrayList<String> items = new ArrayList<>();

        // Greeting
        System.out.println(LINE);
        System.out.println("Hello! I'm Bob");
        System.out.println("What can I do for you?");
        System.out.println(LINE);

        // Read until "bye"
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

            // List ability
            if (input.equals("list")) {
                System.out.println(LINE);
                for (int i = 0; i < items.size(); i++) {
                    System.out.println((i + 1) + ". " + items.get(i));
                }
                System.out.println(LINE);
                continue;
            }

            // Echo
            items.add(input);
            System.out.println(LINE);
            System.out.println("added: " + input);
            System.out.println(LINE);
        }

        sc.close();
    }
}
