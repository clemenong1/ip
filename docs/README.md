# Bob User Guide

**Bob** is a task management chatbot that helps you track todos, deadlines, and events. Chat with Bob via the GUI or CLI—your tasks are saved automatically.

![Bob Chatbot](images/Ui.png)

---

## Quick Start

1. Run `./gradlew run` (or `java -jar build/libs/bob.jar` after building)
2. Type your command in the input box and click **Send**, or press Enter
3. Try `list` to see your tasks, or `todo Buy milk` to add one

---

## Notes about the command format

> :bulb: **Tips**
>
> - Words in `UPPER_CASE` are parameters you must provide (e.g. in `todo DESCRIPTION`, `DESCRIPTION` is the task text).
> - Items in square brackets `[ ]` are optional.
> - **Date/time formats:** `yyyy-MM-dd`, `yyyy-MM-dd HHmm`, `d/M/yyyy`, `d/M/yyyy HHmm` (e.g. `2025-02-20`, `25/2/2025 1800`).
> - Extraneous text after commands like `list` or `bye` is ignored (e.g. `list extra` is treated as `list`).
> - Commands are **case-sensitive** (use lowercase: `list`, not `List`).

---

## Features

### Viewing tasks: `list`

Shows all tasks in your list.

**Example:** `list`

```
Here are the tasks in your list:
1.[T][] Buy milk
2.[D][] Submit report (by: Feb 25 2026)
3.[E][] Meeting (from: Feb 20 2026 to: Feb 20 2026)
```

---

### Adding a todo: `todo`

Adds a simple task with a description.

**Format:** `todo DESCRIPTION`

**Example:** `todo Buy milk`

```
Got it. I've added this task:
[T][] Buy milk
Now you have 4 tasks in the list.
```

---

### Adding a deadline: `deadline`

Adds a task with a due date/time.

**Format:** `deadline DESCRIPTION /by DATE [TIME]`

**Examples:**
- `deadline Submit report /by 2025-02-25`
- `deadline Submit report /by 25/2/2025 1800`

```
Got it. I've added this task:
[D][] Submit report (by: Feb 25 2026)
Now you have 5 tasks in the list.
```

---

### Adding an event: `event`

Adds a task with a start and end time.

**Format:** `event DESCRIPTION /from START /to END`

**Example:** `event Team meeting /from 2025-02-20 14:00 /to 2025-02-20 15:00`

```
Got it. I've added this task:
[E][] Team meeting (from: Feb 20 2026 to: Feb 20 2026)
Now you have 6 tasks in the list.
```

---

### Marking a task done: `mark`

Marks the task at the given index as done.

**Format:** `mark INDEX`

**Example:** `mark 1`

```
Nice! I've marked this task as done:
[T][X] Buy milk
```

---

### Unmarking a task: `unmark`

Marks the task at the given index as not done.

**Format:** `unmark INDEX`

**Example:** `unmark 1`

```
OK, I've marked this task as not done yet:
[T][] Buy milk
```

---

### Deleting a task: `delete`

Removes the task at the given index.

**Format:** `delete INDEX`

**Example:** `delete 2`

```
Noted. I've removed this task:
[D][] Submit report (by: Feb 25 2026)
Now you have 5 tasks in the list.
```

---

### Finding tasks: `find`

Lists tasks whose description contains the keyword.

**Format:** `find KEYWORD`

**Example:** `find report`

```
Here are the matching tasks in your list:
1.[D][] Submit report (by: Feb 25 2026)
```

---

### Viewing tasks on a date: `on`

Lists tasks occurring on a specific date (deadlines due that day, events spanning that day).

**Format:** `on DATE`

**Example:** `on 2025-02-20`

```
Here are the tasks occurring on 20 Feb 2026:
1.[E][] Team meeting (from: Feb 20 2026 to: Feb 20 2026)
```

---

### Exiting: `bye`

Exits Bob. Your tasks are saved automatically.

**Example:** `bye`

```
Bye. Hope to see you again soon!
```

---

## Tips

- **Urgent deadlines:** Bob highlights deadlines due within 3 days in the list.
- **Task symbols:** `[T]` = Todo, `[D]` = Deadline, `[E]` = Event. `[X]` means done, `[]` means not done.
- Tasks are stored in `data/bob.txt` in the project folder.
