import manager.Managers;
import manager.TaskManager;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

public class Main {
    static TaskManager taskManager = Managers.getDefault();

    public static void main(String[] args) {
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

        // Создаем эпик с двумя подзадачами
        int epicId1 = taskManager.addNewEpicTask(
                new EpicTask("Переезд", "")
        );
        taskManager.addNewSubTask(
                new SubTask("Собрать коробки", "1", TaskStatus.valueOf("DONE"), epicId1)
        );
        taskManager.addNewSubTask(
                new SubTask("Упаковать кошку", "2", TaskStatus.valueOf("NEW"), epicId1)
        );

        // Создаем эпик с 1 подзадачей
        int epicId2 = taskManager.addNewEpicTask(
                new EpicTask("Второй эпик", "")
        );
        taskManager.addNewSubTask(
                new SubTask("Подзадача второго эпика", "2222", TaskStatus.valueOf("DONE"), epicId2)
        );

        printAllTasks(); // Выводим результаты

        // Меняем данные первой подзадачи первого эпика

        taskManager.updateSubTask(new SubTask(4, "Упаковать кошку", "22222", TaskStatus.valueOf("DONE"), epicId1));


        printAllTasks(); // Наблюдаем за результатом

        taskManager.deleteTask(0);
        taskManager.deleteEpicTask(2);

        printAllTasks(); // Наблюдаем за результатом
    }

    public static void printAllTasks() {
        System.out.println();
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpicTasks());
        System.out.println(taskManager.getSubTasks());
        System.out.println();
    }
}
