import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;

import java.time.LocalDateTime;

/**
 * Handles loading tasks from file and saving tasks to file.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates a new Storage instance with the given file path.
     *
     * @param filePath Path to the file for storing tasks.
     */
    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Loads tasks from the file.
     *
     * @return List of tasks loaded from file. Returns an empty list if the file does not exist.
     * @throws IOException If there is an error reading from the file.
     */
    public ArrayList<Task> load() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return tasks;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = parseTaskLine(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
        }

        return tasks;
    }

    /**
     * Saves the given list of tasks to the file.
     *
     * @param tasks List of tasks to save.
     * @throws IOException If there is an error writing to the file.
     */
    public void save(ArrayList<Task> tasks) throws IOException {
        Path directory = filePath.getParent();
        if (directory != null) {
            Files.createDirectories(directory);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (Task task : tasks) {
                writer.write(formatTaskLine(task));
                writer.newLine();
            }
        }
    }

    /**
     * Returns the storage format line for a given task.
     *
     * @param task Task to format.
     * @return Storage line representing the task.
     */
    private String formatTaskLine(Task task) {
        String isDone = (task.status == Task.Status.DONE) ? "1" : "0";

        if (task instanceof Todo) {
            return "T | " + isDone + " | " + task.description;
        }

        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return "D | " + isDone + " | " + deadline.description + " | " + deadline.by.format(DateTimeUtil.STORAGE_DATE_TIME);
        }

        if (task instanceof Event) {
            Event event = (Event) task;
            return "E | " + isDone + " | " + event.description
                    + " | " + event.from.format(DateTimeUtil.STORAGE_DATE_TIME)
                    + " | " + event.to.format(DateTimeUtil.STORAGE_DATE_TIME);
        }
        return "";
    }

    /**
     * Parses a storage format line into a Task instance.
     *
     * @param line Storage line.
     * @return Parsed task, or null if the line is invalid/corrupted.
     */
    private Task parseTaskLine(String line) {
        try {
            String[] parts = line.split("\\s*\\|\\s*");
            if (parts.length < 3) {
                return null;
            }

            String type = parts[0];
            String isDone = parts[1];
            String description = parts[2];

            Task task;
            if ("T".equals(type)) {
                task = new Todo(description);
            } else if ("D".equals(type)) {
                if (parts.length < 4) {
                    return null;
                }
                LocalDateTime by = LocalDateTime.parse(parts[3], DateTimeUtil.STORAGE_DATE_TIME);
                task = new Deadline(description, by);
            } else if ("E".equals(type)) {
                if (parts.length < 5) {
                    return null;
                }
                LocalDateTime from = LocalDateTime.parse(parts[3], DateTimeUtil.STORAGE_DATE_TIME);
                LocalDateTime to = LocalDateTime.parse(parts[4], DateTimeUtil.STORAGE_DATE_TIME);
                task = new Event(description, from, to);
            } else {
                return null;
            }

            task.status = "1".equals(isDone) ? Task.Status.DONE : Task.Status.NOT_DONE;
            return task;
        } catch (Exception e) {
            return null; // corrupted line -> skip
        }
    }
}
