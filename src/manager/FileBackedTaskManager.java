package manager;

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public static void main(String[] args) {
        TaskManager taskManager1 = Managers.getDefault();

        // Создаем 2 задачи
        int task1Id = taskManager1.addNewTask(new Task("Написать код", "Выполнить финалку 3 спринта", TaskStatus.valueOf("IN_PROGRESS")));
        int task2Id = taskManager1.addNewTask(new Task("Лечь спать", "Выспаться перед сложным днем", TaskStatus.valueOf("NEW")));

        // Создаем эпик с тремя подзадачами
        int epic1Id = taskManager1.addNewEpicTask(new EpicTask("Переезд", " "));
        int subtask1Id = taskManager1.addNewSubTask(new SubTask("Собрать коробки", "1", TaskStatus.valueOf("DONE"), epic1Id));
        int subtask2Id = taskManager1.addNewSubTask(new SubTask("Упаковать кошку", "2", TaskStatus.valueOf("NEW"), epic1Id));
        int subtask3Id = taskManager1.addNewSubTask(new SubTask("3333", "3", TaskStatus.valueOf("NEW"), epic1Id));

        // Создаем эпик без подзадач
        int epic2Id = taskManager1.addNewEpicTask(new EpicTask("Второй эпик", " "));

        printAllTasks(taskManager1); // Выводим все задачи
        printHistory(taskManager1); // Выводим историю (пустую)

        //Вызываем некоторые задачи в случайном порядке и смотрим историю
        taskManager1.getSubTask(subtask3Id);
        taskManager1.getTask(task2Id);
        taskManager1.getEpicTask(epic2Id);
        taskManager1.getSubTask(subtask1Id);
        printHistory(taskManager1);

        TaskManager taskManager2 = Managers.getDefault();
        printAllTasks(taskManager2);
        printHistory(taskManager2);
    }
    private static void printAllTasks(TaskManager taskManager) {
        System.out.println("Все задачи: ");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpicTasks());
        System.out.println(taskManager.getSubTasks());
        System.out.println();
    }

    private static void printHistory(TaskManager taskManager) {
        System.out.println("История просмотров (" + taskManager.getHistory().size() + "): ");
        System.out.println(taskManager.getHistory());
        System.out.println();
    }

    public FileBackedTaskManager(File file) {
        this.file = file;
    }
    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write("id,type,name,status,description,epic\n");
            for (var task : super.tasks.values()) {
                writer.write(CSVTaskFormat.taskToString(task) + "\n");
            }
            for (var task : super.epicTasks.values()) {
                writer.write(CSVTaskFormat.taskToString(task) + "\n");
            }
            for (var task : super.subTasks.values()) {
                writer.write(CSVTaskFormat.taskToString(task) + "\n");
            }
            writer.write("\n");
            writer.write(CSVTaskFormat.historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения");
        }
    }
    public static FileBackedTaskManager loadFromFile(File file) {
        if (!file.exists())
            return new FileBackedTaskManager(file);
        int maxId = 0;
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            String line = reader.readLine();
            while (!line.isBlank()) {
                Task task = CSVTaskFormat.taskFromString(line);
                switch (task.getTaskType()) {
                    case TASK:
                        taskManager.addTask(task);
                        break;
                    case EPIC_TASK:
                        taskManager.addEpicTask((EpicTask) task);
                        break;
                    case SUB_TASK:
                        taskManager.addSubTask((SubTask) task);
                        break;
                }
                if (task.getId() > maxId)
                    maxId = task.getId();
                line = reader.readLine();
            }
            line = reader.readLine();
            List<Integer> historyList = CSVTaskFormat.historyFromString(line);
            for (Integer id : historyList) {
                Task task;
                if (taskManager.tasks.containsKey(id)) {
                    task = taskManager.tasks.get(id);
                } else if (taskManager.epicTasks.containsKey(id)) {
                    task = taskManager.epicTasks.get(id);
                } else
                    task = taskManager.subTasks.get(id);
                taskManager.historyManager.add(task);
            }
            taskManager.idGenerator = maxId + 1;
        } catch (IOException ex) {
            throw new ManagerSaveException("Ошибка загрузки");
        }
        return taskManager;
    }
    private void addTask (Task task) {
        tasks.put(task.getId(), task);
    }
    private void addEpicTask (EpicTask epicTask) {
        epicTasks.put(epicTask.getId(), epicTask);
    }
    private void addSubTask (SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
    }
    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }
    @Override
    public EpicTask getEpicTask(int id) {
        EpicTask epicTask = super.getEpicTask(id);
        save();
        return epicTask;
    }
    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = super.getSubTask(id);
        save();
        return subTask;
    }
    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        save();
        return id;
    }
    @Override
    public int addNewEpicTask(EpicTask epicTask) {
        int id = super.addNewEpicTask(epicTask);
        save();
        return id;
    }
    @Override
    public int addNewSubTask(SubTask subTask) {
        int id = super.addNewSubTask(subTask);
        save();
        return id;
    }
    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }
    @Override
    public void updateEpicTask(EpicTask epicTask) {
        super.updateEpicTask(epicTask);
        save();
    }
    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }
    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }
    @Override
    public void deleteEpicTask(int id) {
        super.deleteEpicTask(id);
        save();
    }
    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }
}
