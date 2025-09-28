import java.util.*;

// ------------------- Task Class -------------------
class Task {
    private String description;
    private String startTime;
    private String endTime;
    private String priority;
    private boolean completed;

    public Task(String description, String startTime, String endTime, String priority) {
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.priority = priority;
        this.completed = false;
    }

    public String getDescription() { return description; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getPriority() { return priority; }
    public boolean isCompleted() { return completed; }

    public void markCompleted() { this.completed = true; }

    @Override
    public String toString() {
        String status = completed ? " (Completed)" : "";
        return startTime + " - " + endTime + ": " + description + " [" + priority + "]" + status;
    }
}

// ------------------- Factory Pattern -------------------
class TaskFactory {
    public static Task createTask(String description, String startTime, String endTime, String priority) {
        return new Task(description, startTime, endTime, priority);
    }
}

// ------------------- Observer Pattern -------------------
interface TaskObserver {
    void notifyConflict(String message);
}

class ConsoleObserver implements TaskObserver {
    @Override
    public void notifyConflict(String message) {
        System.out.println("Error: " + message);
    }
}

// ------------------- Singleton Pattern -------------------
class ScheduleManager {
    private static ScheduleManager instance;
    private List<Task> tasks;
    private List<TaskObserver> observers;

    private ScheduleManager() {
        tasks = new ArrayList<>();
        observers = new ArrayList<>();
    }

    public static ScheduleManager getInstance() {
        if (instance == null) {
            instance = new ScheduleManager();
        }
        return instance;
    }

    public void addObserver(TaskObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(String message) {
        for (TaskObserver observer : observers) {
            observer.notifyConflict(message);
        }
    }

    private boolean isValidTime(String time) {
        return time.matches("([01]\\d|2[0-3]):[0-5]\\d");
    }

    private boolean isOverlap(String start, String end) {
        int newStart = convertToMinutes(start);
        int newEnd = convertToMinutes(end);
        for (Task task : tasks) {
            int existingStart = convertToMinutes(task.getStartTime());
            int existingEnd = convertToMinutes(task.getEndTime());
            if (newStart < existingEnd && newEnd > existingStart) {
                notifyObservers("Task conflicts with existing task \"" + task.getDescription() + "\".");
                return true;
            }
        }
        return false;
    }

    private int convertToMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    public void addTask(String description, String startTime, String endTime, String priority) {
        if (!isValidTime(startTime) || !isValidTime(endTime)) {
            System.out.println("Error: Invalid time format.");
            return;
        }
        if (convertToMinutes(startTime) >= convertToMinutes(endTime)) {
            System.out.println("Error: Start time must be before end time.");
            return;
        }
        if (isOverlap(startTime, endTime)) return;

        Task task = TaskFactory.createTask(description, startTime, endTime, priority);
        tasks.add(task);
        System.out.println("Task added successfully. No conflicts.");
    }

    public void removeTask(String description) {
        Task toRemove = null;
        for (Task task : tasks) {
            if (task.getDescription().equalsIgnoreCase(description)) {
                toRemove = task;
                break;
            }
        }
        if (toRemove != null) {
            tasks.remove(toRemove);
            System.out.println("Task removed successfully.");
        } else {
            System.out.println("Error: Task not found.");
        }
    }

    public void viewTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks scheduled for the day.");
            return;
        }
        tasks.sort(Comparator.comparing(Task::getStartTime));
        for (Task task : tasks) {
            System.out.println(task);
        }
    }

    public void markTaskCompleted(String description) {
        for (Task task : tasks) {
            if (task.getDescription().equalsIgnoreCase(description)) {
                task.markCompleted();
                System.out.println("Task marked as completed.");
                return;
            }
        }
        System.out.println("Error: Task not found.");
    }

    public void viewByPriority(String priority) {
        boolean found = false;
        for (Task task : tasks) {
            if (task.getPriority().equalsIgnoreCase(priority)) {
                System.out.println(task);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No tasks found with priority: " + priority);
        }
    }
}

// ------------------- Main Application -------------------
public class AstronautScheduleApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ScheduleManager manager = ScheduleManager.getInstance();
        manager.addObserver(new ConsoleObserver());

        while (true) {
            System.out.println("\n--- Astronaut Daily Schedule ---");
            System.out.println("1. Add Task");
            System.out.println("2. Remove Task");
            System.out.println("3. View All Tasks");
            System.out.println("4. Mark Task as Completed");
            System.out.println("5. View Tasks by Priority");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");

            int choice = -1;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid input. Try again.");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter Description: ");
                    String desc = sc.nextLine();
                    System.out.print("Enter Start Time (HH:MM): ");
                    String start = sc.nextLine();
                    System.out.print("Enter End Time (HH:MM): ");
                    String end = sc.nextLine();
                    System.out.print("Enter Priority (High/Medium/Low): ");
                    String priority = sc.nextLine();
                    manager.addTask(desc, start, end, priority);
                    break;
                case 2:
                    System.out.print("Enter Task Description to Remove: ");
                    String removeDesc = sc.nextLine();
                    manager.removeTask(removeDesc);
                    break;
                case 3:
                    manager.viewTasks();
                    break;
                case 4:
                    System.out.print("Enter Task Description to Mark Completed: ");
                    String compDesc = sc.nextLine();
                    manager.markTaskCompleted(compDesc);
                    break;
                case 5:
                    System.out.print("Enter Priority to View: ");
                    String viewPriority = sc.nextLine();
                    manager.viewByPriority(viewPriority);
                    break;
                case 6:
                    System.out.println("Exiting... Goodbye Astronaut!");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
