import manager.TaskManager;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

public class Main {
    static TaskManager taskManager = new TaskManager();

    public static void main(String[] args) {
        executeTest();
    }

    public static void executeTest() {

        // Создаем 2 задачи
        taskManager.addNewTask(
                new Task("Написать код", "Выполнить финалку 3 спринта", "IN_PROGRESS")
        );
        taskManager.addNewTask(
                new Task("Лечь спать", "Выспаться перед сложным днем", "NEW")
        );

        // Создаем эпик с двумя подзадачами
        int epicId1 = taskManager.addNewEpicTask(
                new EpicTask("Переезд", "")
        );
        taskManager.addNewSubTask(
                new SubTask("Собрать коробки", "1", "DONE", epicId1)
        );
        taskManager.addNewSubTask(
                new SubTask("Упаковать кошку", "2", "NEW", epicId1)
        );

        // Создаем эпик с 1 подзадачей
        int epicId2 = taskManager.addNewEpicTask(
                new EpicTask("Второй эпик", "")
        );
        taskManager.addNewSubTask(
                new SubTask("Подзадача второго эпика", "2222", "DONE", epicId2)
        );

        printTestResults(); // Выводим результаты

        // Меняем данные первой подзадачи первого эпика
        taskManager.updateSubTask(new SubTask(4, "Упаковать кошку", "22222", "DONE", epicId1));

        printTestResults(); // Наблюдаем за результатом

        taskManager.deleteTask(0);
        taskManager.deleteEpicTask(2);

        printTestResults(); // Наблюдаем за результатом
    }
    public static void printTestResults() {
        System.out.println();
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpicTasks());
        System.out.println(taskManager.getSubTasks());
        System.out.println();
    }
}
