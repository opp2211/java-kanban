package practicum.historymanager;

import org.junit.jupiter.api.Test;
import practicum.tasks.Task;
import practicum.tasks.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

public abstract class HistoryManagerTest<T extends HistoryManager> {
    private static final int NON_EXIST_TASK_ID = -1;
    protected T historyManager;

    @Test
    public void addTask() {
        historyManager.add(new Task(0,"Test task 1", "Test task 1 description", TaskStatus.NEW, 0, null));
        assertEquals(1, historyManager.getHistory().size());
    }
    @Test
    public void addSameTaskTwice() {
        Task task = new Task(0,"Test task 1", "Test task 1 description", TaskStatus.NEW, 0, null);
        historyManager.add(task);
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size());
    }
    @Test
    public void removeFirst() {
        Task task1 = new Task(0,"Test task 1", "Test task 1 description", TaskStatus.NEW, 0, null);
        Task task2 = new Task(1,"Test task 2", "Test task 2 description", TaskStatus.NEW, 0, null);
        Task task3 = new Task(2,"Test task 3", "Test task 3 description", TaskStatus.NEW, 0, null);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task1.getId());

        assertFalse(historyManager.getHistory().contains(task1));
        assertEquals(2, historyManager.getHistory().size());
        assertEquals(task2, historyManager.getHistory().get(0));
        assertEquals(task3, historyManager.getHistory().get(1));
    }
    @Test
    public void removeMiddle() {
        Task task1 = new Task(0,"Test task 1", "Test task 1 description", TaskStatus.NEW, 0, null);
        Task task2 = new Task(1,"Test task 2", "Test task 2 description", TaskStatus.NEW, 0, null);
        Task task3 = new Task(2,"Test task 3", "Test task 3 description", TaskStatus.NEW, 0, null);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task2.getId());

        assertFalse(historyManager.getHistory().contains(task2));
        assertEquals(2, historyManager.getHistory().size());
        assertEquals(task1, historyManager.getHistory().get(0));
        assertEquals(task3, historyManager.getHistory().get(1));
    }
    @Test
    public void removeLast() {
        Task task1 = new Task(0,"Test task 1", "Test task 1 description", TaskStatus.NEW, 0, null);
        Task task2 = new Task(1,"Test task 2", "Test task 2 description", TaskStatus.NEW, 0, null);
        Task task3 = new Task(2,"Test task 3", "Test task 3 description", TaskStatus.NEW, 0, null);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task3.getId());

        assertFalse(historyManager.getHistory().contains(task3));
        assertEquals(2, historyManager.getHistory().size());
        assertEquals(task1, historyManager.getHistory().get(0));
        assertEquals(task2, historyManager.getHistory().get(1));
    }
    @Test
    public void removeWrongTaskId() {
        Task task = new Task(0,"Test task 1", "Test task 1 description", TaskStatus.NEW, 0, null);
        historyManager.add(task);
        historyManager.remove(NON_EXIST_TASK_ID);

        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    public void getHistory() {
        historyManager.add(new Task(0,"Test task 1", "Test task 1 description", TaskStatus.NEW, 0, null));
        historyManager.add(new Task(1,"Test task 2", "Test task 2 description", TaskStatus.NEW, 0, null));

        assertEquals(2, historyManager.getHistory().size());
    }
    @Test
    public void clear() {
        historyManager.add(new Task(0,"Test task 1", "Test task 1 description", TaskStatus.NEW, 0, null));
        historyManager.add(new Task(1,"Test task 2", "Test task 2 description", TaskStatus.NEW, 0, null));
        historyManager.clear();

        assertEquals(0, historyManager.getHistory().size());
    }
    @Test
    public void clearEmpty() {
        historyManager.clear();
        assertEquals(0, historyManager.getHistory().size());
    }
}
