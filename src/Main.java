import manager.Managers;
import manager.TaskManager;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

public class Main {
    static TaskManager taskManager = Managers.getDefault();

    public static void main(String[] args) {
        System.out.println();
        executeTest();
    }

    public static void executeTest() {

        // Создаем 2 задачи
        taskManager.addNewTask(
                new Task("Написать код", "Выполнить финалку 3 спринта", TaskStatus.valueOf("IN_PROGRESS"))
        );
        taskManager.addNewTask(
                new Task("Лечь спать", "Выспаться перед сложным днем", TaskStatus.valueOf("NEW"))
        );

        // Создаем эпик с тремя подзадачами
        int epicId1 = taskManager.addNewEpicTask(
                new EpicTask("Переезд", "")
        );
        taskManager.addNewSubTask(
                new SubTask("Собрать коробки", "1", TaskStatus.valueOf("DONE"), epicId1)
        );
        taskManager.addNewSubTask(
                new SubTask("Упаковать кошку", "2", TaskStatus.valueOf("NEW"), epicId1)
        );
        taskManager.addNewSubTask(
                new SubTask("3333", "3", TaskStatus.valueOf("NEW"), epicId1)
        );

        // Создаем эпик без подзадач
        taskManager.addNewEpicTask(
                new EpicTask("Второй эпик", "")
        );

        printAllTasks(); // Выводим все задачи
        printHistory(); // Выводим историю (пустую)

        //Вызываем все задачи и смотрим историю
        taskManager.getTask(0);
        taskManager.getTask(1);
        taskManager.getEpicTask(2);
        taskManager.getSubTask(3);
        taskManager.getSubTask(4);
        taskManager.getSubTask(5);
        taskManager.getEpicTask(6);
        printHistory();

        //Вызываем задачи и смотрим историю (на порядок и наличие повторов)
        taskManager.getEpicTask(2);
        taskManager.getTask(0);
        taskManager.getSubTask(4);
        printHistory();

        //Удяляем задачи и смотрим историю
        taskManager.deleteTask(1);
        taskManager.deleteSubTask(5);
        printHistory();

        //Удяляем эпик и смотрим историю
        taskManager.deleteEpicTask(2);
        printHistory();
    }

    static void printAllTasks() {
        System.out.println("Все задачи: ");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpicTasks());
        System.out.println(taskManager.getSubTasks());
        System.out.println();
    }

    static void printHistory() {
        System.out.println("История просмотров (" + taskManager.getHistory().size() + "): ");
        System.out.println(taskManager.getHistory());
        System.out.println();
    }
}
