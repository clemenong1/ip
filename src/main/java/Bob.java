import java.util.Scanner;

public class Bob {
    private static final String LINE = "____________________________________________________________";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Greeting
        System.out.println(LINE);
        System.out.println("Hello! I'm Bob");
        System.out.println("What can I do for you?");
        System.out.println(LINE);

        // Read until "bye"
        while (true) {
            String input = sc.nextLine();

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

            // Echo
            System.out.println(LINE);
            System.out.println(" " + input + ".GIVE ME MORE.");
            System.out.println(LINE);
        }

        sc.close();
    }
}
