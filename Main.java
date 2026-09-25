import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Main {
    private static final String OUTPUT_FILE = "minutes.txt";

    static class Task {
        String description;
        String assignee;

        Task(String description, String assignee) {
            this.description = description;
            this.assignee = assignee;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        ArrayList<String> attendance = new ArrayList<>();
        ArrayList<String> notes = new ArrayList<>();
        ArrayList<Task> tasks = new ArrayList<>();
        ArrayList<String> unresolvedDiscussion = new ArrayList<>();

        System.out.print("What time is it (start time): ");
        String startTime = in.readLine().strip();

        showCommands();
        String command = readCommand(in);

        while (true) {
            switch (command) {
                case "fja":
                    command = attendance(attendance, in);
                    break;

                case "fjt":
                    command = addTask(tasks, in);
                    break;

                case "fjn":
                    command = note(notes, in);
                    break;

                case "fjsn":
                    showCurrentNotes(notes);
                    command = readCommand(in);
                    break;

                case "fjst":
                    showCurrentTasks(tasks);
                    command = readCommand(in);
                    break;

                case "fjat":
                    command = assignTask(tasks, in);
                    break;

                case "fju":
                    command = unresolvedDiscussion(unresolvedDiscussion, in);
                    break;

                case "fjsu":
                    showUnresolvedDiscussion(unresolvedDiscussion);
                    command = readCommand(in);
                    break;

                case "fjau":
                    command = markUnresolvedDiscussion(unresolvedDiscussion, in);
                    break;

                case "fjc":
                    showCommands();
                    command = readCommand(in);
                    break;

                case "fjend":
                    if (confirmEnd(in)) {
                        end(in, startTime, attendance, notes,
                                tasks, unresolvedDiscussion);

                        System.out.println("Minutes saved to " + OUTPUT_FILE);
                        return;
                    }

                    command = readCommand(in);
                    break;

                default:
                    System.out.println(
                            "Unknown command. Type fjc to see the command list.");
                    command = readCommand(in);
                    break;
            }
        }
    }

    static String readCommand(BufferedReader in) throws IOException {
        System.out.print("Enter a command: ");
        return in.readLine().strip().toLowerCase();
    }

    static boolean isCommand(String input) {
        if (input == null) {
            return false;
        }

        switch (input.strip().toLowerCase()) {
            case "fja":
            case "fjt":
            case "fjn":
            case "fjsn":
            case "fjst":
            case "fjat":
            case "fju":
            case "fjsu":
            case "fjau":
            case "fjc":
            case "fjend":
                return true;

            default:
                return false;
        }
    }

    static void showCommands() {
        System.out.println("\nAVAILABLE COMMANDS");
        System.out.println("fja   - add attendance");
        System.out.println("fjt   - add a task");
        System.out.println("fjn   - add notes");
        System.out.println("fjsn  - show current notes");
        System.out.println("fjst  - show current tasks");
        System.out.println("fjat  - assign a person to a task");
        System.out.println("fju   - add an unresolved discussion");
        System.out.println("fjsu  - show unresolved discussions");
        System.out.println("fjau  - mark an unresolved discussion as resolved");
        System.out.println("fjc   - show available commands");
        System.out.println("fjend - end the meeting\n");
    }

    static String attendance(
            ArrayList<String> attendance,
            BufferedReader in) throws IOException {

        System.out.println(
                "ATTENDANCE: enter one name per line. "
                        + "Enter any command when finished.");

        while (true) {
            String input = in.readLine().strip();

            if (isCommand(input)) {
                return input.toLowerCase();
            }

            if (input.isEmpty()) {
                continue;
            }

            if (attendance.contains(input)) {
                System.out.println("Name already exists.");
            } else {
                attendance.add(input);
            }
        }
    }

    static String addTask(
            ArrayList<Task> tasks,
            BufferedReader in) throws IOException {

        System.out.println(
                "ADD TASK: enter a task, or use task = person "
                        + "to assign it immediately.");

        System.out.println("Enter any command when finished.");

        while (true) {
            String input = in.readLine().strip();

            if (isCommand(input)) {
                return input.toLowerCase();
            }

            if (input.isEmpty()) {
                continue;
            }

            int equals = input.indexOf('=');

            if (equals >= 0) {
                String description =
                        input.substring(0, equals).strip();

                String assignee =
                        input.substring(equals + 1).strip();

                if (description.isEmpty()) {
                    System.out.println(
                            "Task description cannot be empty.");
                    continue;
                }

                if (assignee.isEmpty()) {
                    assignee = "unassigned";
                }

                tasks.add(new Task(description, assignee));

            } else {
                tasks.add(new Task(input, "unassigned"));
            }
        }
    }

    static String note(
            ArrayList<String> notes,
            BufferedReader in) throws IOException {

        System.out.println(
                "NOTES: enter one note per line. "
                        + "Enter any command when finished.");

        while (true) {
            String input = in.readLine();

            if (isCommand(input)) {
                return input.strip().toLowerCase();
            }

            notes.add(input);
        }
    }

    static void showCurrentNotes(ArrayList<String> notes) {
        System.out.println("CURRENT NOTES:");

        if (notes.isEmpty()) {
            System.out.println("No notes recorded.");
        } else {
            for (String note : notes) {
                System.out.println(note);
            }
        }

        System.out.println("/* end of notes */");
    }

    static void showCurrentTasks(ArrayList<Task> tasks) {
        System.out.println("CURRENT TASKS:");

        if (tasks.isEmpty()) {
            System.out.println("No tasks recorded.");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);

                System.out.println(
                        (i + 1) + ". "
                                + task.description
                                + " - "
                                + task.assignee);
            }
        }

        System.out.println("/* end of tasks */");
    }

    static String assignTask(
            ArrayList<Task> tasks,
            BufferedReader in) throws IOException {

        if (tasks.isEmpty()) {
            System.out.println("There are no tasks to assign.");
            return readCommand(in);
        }

        System.out.println(
                "ASSIGN TASK: enter task number - name, "
                        + "for example: 1 - John Smith");

        System.out.println("Enter any command when finished.");

        while (true) {
            showCurrentTasks(tasks);

            String input = in.readLine().strip();

            if (isCommand(input)) {
                return input.toLowerCase();
            }

            int dash = input.indexOf('-');

            if (dash < 0) {
                System.out.println(
                        "Use the format: 1 - John Smith");
                continue;
            }

            try {
                int taskNumber = Integer.parseInt(
                        input.substring(0, dash).strip());

                String assignee =
                        input.substring(dash + 1).strip();

                int index = taskNumber - 1;

                if (index < 0 || index >= tasks.size()) {
                    System.out.println("Invalid task number.");

                } else if (assignee.isEmpty()) {
                    System.out.println(
                            "Assignee cannot be empty.");

                } else {
                    tasks.get(index).assignee = assignee;

                    System.out.println(
                            "Task " + taskNumber
                                    + " assigned to "
                                    + assignee + ".");
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid task number.");
            }
        }
    }

    static String unresolvedDiscussion(
            ArrayList<String> unresolvedDiscussion,
            BufferedReader in) throws IOException {

        System.out.println(
                "UNRESOLVED DISCUSSION: enter one item per line. "
                        + "Enter any command when finished.");

        while (true) {
            String input = in.readLine().strip();

            if (isCommand(input)) {
                return input.toLowerCase();
            }

            if (!input.isEmpty()) {
                unresolvedDiscussion.add(input);
            }
        }
    }

    static void showUnresolvedDiscussion(
            ArrayList<String> unresolvedDiscussion) {

        System.out.println(
                "CURRENT UNRESOLVED DISCUSSIONS:");

        if (unresolvedDiscussion.isEmpty()) {
            System.out.println(
                    "No unresolved discussions.");
        } else {
            for (int i = 0;
                 i < unresolvedDiscussion.size();
                 i++) {

                System.out.println(
                        (i + 1) + ". "
                                + unresolvedDiscussion.get(i));
            }
        }

        System.out.println(
                "/* end of unresolved discussions */");
    }

    static String markUnresolvedDiscussion(
            ArrayList<String> unresolvedDiscussion,
            BufferedReader in) throws IOException {

        if (unresolvedDiscussion.isEmpty()) {
            System.out.println(
                    "There are no unresolved discussions.");

            return readCommand(in);
        }

        System.out.println(
                "MARK RESOLVED: enter the discussion number. "
                        + "Enter any command when finished.");

        while (true) {
            showUnresolvedDiscussion(
                    unresolvedDiscussion);

            String input = in.readLine().strip();

            if (isCommand(input)) {
                return input.toLowerCase();
            }

            try {
                int discussionNumber =
                        Integer.parseInt(input);

                int index = discussionNumber - 1;

                if (index < 0
                        || index >= unresolvedDiscussion.size()) {

                    System.out.println(
                            "Invalid discussion number.");

                } else {
                    String resolved =
                            unresolvedDiscussion.remove(index);

                    System.out.println(
                            "Resolved: " + resolved);

                    if (unresolvedDiscussion.isEmpty()) {
                        System.out.println(
                                "No unresolved discussions remain.");
                    }
                }

            } catch (NumberFormatException e) {
                System.out.println(
                        "Enter a valid discussion number.");
            }
        }
    }

    static boolean confirmEnd(
            BufferedReader in) throws IOException {

        System.out.print(
                "Are you sure you want to end? (Y/N): ");

        String answer = in.readLine().strip();

        return !answer.isEmpty()
                && Character.toLowerCase(answer.charAt(0)) == 'y';
    }

    static void end(
            BufferedReader in,
            String startTime,
            ArrayList<String> attendance,
            ArrayList<String> notes,
            ArrayList<Task> tasks,
            ArrayList<String> unresolvedDiscussion)
            throws IOException {

        System.out.print(
                "When is the next meeting? "
                        + "(Month date, year, at time): ");
        String nextMeetingDate = in.readLine();

        System.out.print(
                "What time did the meeting end?: ");
        String endTime = in.readLine();

        System.out.print("Who presided?: ");
        String presided = in.readLine();

        System.out.print(
                "What is your name as secretary?: ");
        String secretary = in.readLine();

        System.out.print(
                "What is the date? (Month day, year): ");
        String date = in.readLine();

        System.out.print(
                "Where did this meeting take place?: ");
        String place = in.readLine();

        System.out.print(
                "What was this meeting for? "
                        + "(e.g. Weekly Meeting of Board of Directors): ");
        String why = in.readLine();

        System.out.print(
                "What is the organization?: ");
        String org = in.readLine();

        try (PrintWriter out =
                     new PrintWriter(new File(OUTPUT_FILE))) {

            out.println(org.toUpperCase());
            out.println();
            out.println("Minutes");
            out.println();
            out.println(why);
            out.println(date);
            out.println();

            out.println(
                    "The " + why
                            + " was called to order at "
                            + place
                            + " at "
                            + startTime
                            + ".");

            out.println();

            if (attendance.isEmpty()) {
                out.println(
                        "Attendees: None recorded.");
            } else {
                out.println(
                        "Attendees: "
                                + String.join(", ", attendance));
            }

            out.println(
                    presided
                            + " presided and "
                            + secretary
                            + " recorded the proceedings "
                            + "of the meeting.");

            out.println();

            out.println("Notes:");

            if (notes.isEmpty()) {
                out.println("- None recorded.");
            } else {
                for (String note : notes) {
                    out.println("- " + note);
                }
            }

            out.println();

            out.println("Tasks:");

            if (tasks.isEmpty()) {
                out.println("- None recorded.");
            } else {
                for (Task task : tasks) {
                    out.println(
                            "- "
                                    + task.description
                                    + " - "
                                    + task.assignee);
                }
            }

            out.println();

            out.println("Unresolved Discussions:");

            if (unresolvedDiscussion.isEmpty()) {
                out.println("- None.");
            } else {
                for (String discussion :
                        unresolvedDiscussion) {

                    out.println("- " + discussion);
                }
            }

            out.println();

            out.println(
                    "The next meeting will be held on "
                            + nextMeetingDate
                            + ".");

            out.println();

            out.println(
                    "There being no further business, "
                            + "the meeting was adjourned at "
                            + endTime
                            + ".");
        }
    }
}
