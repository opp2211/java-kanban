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
                new Task(0, "Написать код", "Выполнить финалку 3 спринта", "IN_PROGRESS")
        );
        taskManager.addNewTask(
                new Task(0, "Лечь спать", "Выспаться перед сложным днем", "NEW")
        );

        // Создаем эпик с двумя подзадачами
        taskManager.addNewEpicTask(
                new EpicTask(0, "Переезд", "", "NEW")
        );
        taskManager.addNewSubTask(
                new SubTask(0, "Собрать коробки", "1", "DONE", 2)
        );
        taskManager.addNewSubTask(
                new SubTask(0, "Упаковать кошку", "2", "NEW", 2)
        );

        // Создаем эпик с 1 подзадачей
        taskManager.addNewEpicTask(
                new EpicTask(0, "Второй эпик", "", "NEW")
        );
        taskManager.addNewSubTask(
                new SubTask(0, "Подзадача второго эпика", "2222", "DONE", 5)
        );

        printTestResults(); // Выводим результаты

        // Меняем данные первой подзадачи первого эпика
        taskManager.updateSubTask(new SubTask(4, "Упаковать кошку", "22222", "DONE", 2));

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
