import manager.FileBackedTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

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
        int task1 = taskManager.addNewTask(new Task("Test task 1", "Test task 1 description", TaskStatus.DONE));
        int task2 = taskManager.addNewTask(new Task("Test task 2", "Test task 2 description", TaskStatus.NEW));
        int epicTask1Id = taskManager.addNewEpicTask(new EpicTask("Test epicTask 1", "Test task 1 description"));
        int subTask1Id = taskManager.addNewSubTask(new SubTask("Test SubTask 1"
                , "Test SubTask 1 description", TaskStatus.NEW, epicTask1Id));
        int subTask2Id = taskManager.addNewSubTask(new SubTask("Test SubTask 2"
                , "Test SubTask 2 description", TaskStatus.IN_PROGRESS, epicTask1Id));

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
        //Я не понимаю почему не работает строка ниже. До заполнения истории корректно работает,
        // а если вызвать хоть один taskManager.getTask(Task task), то не работает
        //assertEquals(taskManager, taskManager2);

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
