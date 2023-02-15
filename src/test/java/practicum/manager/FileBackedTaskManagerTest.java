package practicum.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import practicum.tasks.EpicTask;
import practicum.tasks.SubTask;
import practicum.tasks.Task;
import practicum.tasks.TaskStatus;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager>{

    private File file;
    @BeforeEach
    public void setUp() {
        file = new File("test_"+ System.nanoTime() +".csv");
        taskManager = new FileBackedTaskManager(file);
    }
    @AfterEach
    public void cleanUp() {

        file.delete();
    }

    @Test
    public void LoadManagerWithOneEpic() {
        taskManager.addNewEpicTask(new EpicTask("Test epicTask 1", "Test task 1 description"));
        TaskManager taskManager2 = FileBackedTaskManager.loadFromFile(file);

        assertNotNull(taskManager2);
        assertEquals(taskManager, taskManager2);

    }
    @Test
    public void LoadManagerWithSomeTasksAndHistory() {
        int task1 = taskManager.addNewTask(new Task("Test task 1", "Test task 1 description", TaskStatus.DONE, 0, null));
        int task2 = taskManager.addNewTask(new Task("Test task 2", "Test task 2 description", TaskStatus.NEW, 0, null));
        int epicTask1Id = taskManager.addNewEpicTask(new EpicTask("Test epicTask 1", "Test task 1 description"));
        int subTask1Id = taskManager.addNewSubTask(new SubTask("Test SubTask 1"
                , "Test SubTask 1 description", TaskStatus.NEW, epicTask1Id, 0, null));
        int subTask2Id = taskManager.addNewSubTask(new SubTask("Test SubTask 2"
                , "Test SubTask 2 description", TaskStatus.IN_PROGRESS, epicTask1Id, 0, null));

        //Вызываем задачи для заполнения истории
        taskManager.getTask(task2);
        taskManager.getSubTask(subTask2Id);
        taskManager.getEpicTask(epicTask1Id);


        TaskManager taskManager2 = FileBackedTaskManager.loadFromFile(file);

        assertNotNull(taskManager2);
        assertEquals(taskManager.getTasks(), taskManager2.getTasks());
        assertEquals(taskManager.getEpicTasks(), taskManager2.getEpicTasks());
        assertEquals(taskManager.getSubTasks(), taskManager2.getSubTasks());
        assertEquals(taskManager.getHistory(), taskManager2.getHistory());

    }
    @Test
    public void LoadEmptyManager() {
        TaskManager taskManager2 = FileBackedTaskManager.loadFromFile(file);

        assertNotNull(taskManager2);
        assertEquals(taskManager, taskManager2);

        assertTrue(taskManager2.getTasks().isEmpty());
        assertTrue(taskManager2.getEpicTasks().isEmpty());
        assertTrue(taskManager2.getSubTasks().isEmpty());
        assertTrue(taskManager2.getHistory().isEmpty());
    }

}
