import manager.InMemoryTaskManager;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

public class Main {
    static InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

    public static void main(String[] args) {
        executeTest();

    }

    public static void executeTest() {

        // Создаем 2 задачи
        inMemoryTaskManager.addNewTask(
                new Task("Написать код", "Выполнить финалку 3 спринта", "IN_PROGRESS")
        );
        inMemoryTaskManager.addNewTask(
                new Task("Лечь спать", "Выспаться перед сложным днем", "NEW")
        );

        // Создаем эпик с двумя подзадачами
        int epicId1 = inMemoryTaskManager.addNewEpicTask(
                new EpicTask("Переезд", "")
        );
        inMemoryTaskManager.addNewSubTask(
                new SubTask("Собрать коробки", "1", "DONE", epicId1)
        );
        inMemoryTaskManager.addNewSubTask(
                new SubTask("Упаковать кошку", "2", "NEW", epicId1)
        );

        // Создаем эпик с 1 подзадачей
        int epicId2 = inMemoryTaskManager.addNewEpicTask(
                new EpicTask("Второй эпик", "")
        );
        inMemoryTaskManager.addNewSubTask(
                new SubTask("Подзадача второго эпика", "2222", "DONE", epicId2)
        );

        printTestResults(); // Выводим результаты

        // Меняем данные первой подзадачи первого эпика
        inMemoryTaskManager.updateSubTask(new SubTask(4, "Упаковать кошку", "22222", "DONE", epicId1));

        printTestResults(); // Наблюдаем за результатом

        inMemoryTaskManager.deleteTask(0);
        inMemoryTaskManager.deleteEpicTask(2);

        printTestResults(); // Наблюдаем за результатом
    }
    public static void printTestResults() {
        System.out.println();
        System.out.println(inMemoryTaskManager.getTasks());
        System.out.println(inMemoryTaskManager.getEpicTasks());
        System.out.println(inMemoryTaskManager.getSubTasks());
        System.out.println();
    }
}
