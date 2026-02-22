# Bob - Task Management Chatbot

Bob is a task management chatbot that helps you track todos, deadlines, and events. It can be used via a **command-line interface (CLI)** or a **JavaFX GUI**. Tasks are saved automatically to `data/bob.txt`.

## Features

- **Todo** – Simple tasks with a description
- **Deadline** – Tasks with a due date/time
- **Event** – Tasks with a start and end time
- **Mark / Unmark** – Mark tasks as done or not done
- **Find** – Search tasks by keyword
- **On** – List tasks occurring on a specific date
- **Urgent tasks** – Automatically highlights deadlines due within 3 days

## Supported Commands

| Command | Format | Example |
|---------|--------|---------|
| List | `list` | `list` |
| Add todo | `todo <desc>` | `todo Buy milk` |
| Add deadline | `deadline <desc> /by <time>` | `deadline Submit report /by 2025-02-25 1800` |
| Add event | `event <desc> /from <start> /to <end>` | `event Meeting /from 2025-02-20 14:00 /to 2025-02-20 15:00` |
| Mark done | `mark <n>` | `mark 1` |
| Unmark | `unmark <n>` | `unmark 1` |
| Delete | `delete <n>` | `delete 1` |
| Find | `find <keyword>` | `find report` |
| On date | `on <yyyy-MM-dd>` | `on 2025-02-20` |
| Exit | `bye` | `bye` |

**Date/time formats:** `yyyy-MM-dd`, `yyyy-MM-dd HHmm`, `d/M/yyyy`, `d/M/yyyy HHmm`

## Architecture

- **Bob** – Main controller; processes commands and coordinates components
- **TaskList** – Manages tasks (add, remove, find, urgent, filter by date)
- **Task / Todo / Deadline / Event** – Task types with description and status
- **Parser** – Parses user input and extracts command arguments
- **Storage** – Loads and saves tasks to `data/bob.txt`
- **Ui** – CLI output; **MainWindow / DialogBox** – GUI components
- **CommandResult** – Result object used to unify CLI and GUI responses
- **DateTimeUtil** – Date/time parsing and formatting

## Prerequisites

- **JDK 17**
- IntelliJ IDEA (optional, for IDE setup)

## Setting up in IntelliJ

1. Open IntelliJ (if not on the welcome screen, click `File` > `Close Project`)
2. Click `Open`, select the project directory, and click `OK`
3. Configure the project to use **JDK 17** as explained [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk). Set **Project language level** to `SDK default`

**Note:** Keep the `src/main/java` folder as the root for Java files; tools like Gradle expect this structure.

## How to Run

### Run the GUI

```bash
./gradlew run
```

Or in IntelliJ: Right-click `src/main/java/bob/gui/Launcher.java` → `Run Launcher.main()`

### Run in the Terminal (CLI)

```bash
./gradlew runCli
```

Or in IntelliJ: Right-click `src/main/java/bob/Bob.java` → `Run Bob.main()`

### Run JAR (GUI)

```bash
./gradlew shadowJar
java -jar build/libs/bob.jar
```
