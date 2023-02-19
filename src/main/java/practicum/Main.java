package practicum;

import practicum.manager.Managers;
import practicum.manager.TaskManager;
import practicum.tasks.EpicTask;
import practicum.tasks.SubTask;
import practicum.tasks.Task;
import practicum.tasks.TaskStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    public static final DateTimeFormatter DT_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy--HH:mm");
    static TaskManager taskManager;

    public static void main(String[] args) throws IOException {
        KVServer kvServer = new KVServer();
        kvServer.start();
        taskManager = Managers.getDefault();

        System.out.println();
        executeTest();

        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }

    public static void executeTest() {

        // Создаем 2 задачи
        int task1Id = taskManager.addNewTask(new Task("Написать код", "Выполнить финалку 3 спринта", TaskStatus.valueOf("IN_PROGRESS"), 59, LocalDateTime.parse("13-02-2023--03:00", DT_FORMATTER)));
        int task2Id = taskManager.addNewTask(new Task("Лечь спать", "Выспаться перед сложным днем", TaskStatus.valueOf("NEW"), 59, LocalDateTime.parse("13-02-2023--04:00", DT_FORMATTER)));

        // Создаем эпик с тремя подзадачами
        int epic1Id = taskManager.addNewEpicTask(new EpicTask("Переезд", " "));
        int subtask1Id = taskManager.addNewSubTask(new SubTask("Собрать коробки", "1", TaskStatus.valueOf("DONE"), epic1Id, 29, LocalDateTime.parse("13-02-2023--12:00", DT_FORMATTER)));
        int subtask2Id = taskManager.addNewSubTask(new SubTask("Упаковать кошку", "2", TaskStatus.valueOf("NEW"), epic1Id, 50, LocalDateTime.parse("13-02-2023--12:10", DT_FORMATTER)));
        int subtask3Id = taskManager.addNewSubTask(new SubTask("3333", "3", TaskStatus.valueOf("NEW"), epic1Id, 90, LocalDateTime.parse("13-02-2023--12:30", DT_FORMATTER)));

        // Создаем эпик без подзадач
        int epic2Id = taskManager.addNewEpicTask(new EpicTask("Второй эпик", " "));

        printAllTasks(); // Выводим все задачи
        printHistory(); // Выводим историю (пустую)

        //Вызываем все задачи и смотрим историю
        taskManager.getTask(task1Id);
        taskManager.getTask(task2Id);
        taskManager.getEpicTask(epic1Id);
        taskManager.getSubTask(subtask1Id);
        taskManager.getSubTask(subtask2Id);
        taskManager.getSubTask(subtask3Id);
        taskManager.getEpicTask(epic2Id);
        printHistory();

        //Вызываем задачи и смотрим историю (на порядок и наличие повторов)
        taskManager.getEpicTask(epic1Id);
        taskManager.getTask(task1Id);
        taskManager.getSubTask(subtask2Id);
        printHistory();

        //Удяляем задачи и смотрим историю
        taskManager.deleteTask(task2Id);
        taskManager.deleteSubTask(subtask3Id);
        printHistory();

        //Удяляем эпик и смотрим историю
        taskManager.deleteEpicTask(epic1Id);
        printHistory();
        printAllTasks();
    }

    static void printAllTasks() {
        System.out.println("Все задачи: ");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpicTasks());
        System.out.println(taskManager.getSubTasks());
        System.out.println("Приоритизированный список: ");
        System.out.println(taskManager.getPrioritizedTasks());
        System.out.println();
    }

    static void printHistory() {
        System.out.println("История просмотров (" + taskManager.getHistory().size() + "): ");
        System.out.println(taskManager.getHistory());
        System.out.println();
    }
}
