package practicum.manager;

import org.junit.jupiter.api.Test;
import practicum.tasks.EpicTask;
import practicum.tasks.SubTask;
import practicum.tasks.Task;
import practicum.tasks.TaskStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    private static final int NON_EXIST_TASK_ID = -1;
    public static final DateTimeFormatter DT_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy--HH:mm");
    protected T taskManager;
    @Test
    public void getTasks() {
        Task task = new Task("Test task 1", "Test task 1 description", TaskStatus.NEW, 0, null);
        taskManager.addNewTask(task);
        List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks);
        assertEquals(task, tasks.get(0));
        assertEquals(1, tasks.size());
    }
    @Test
    public void getTasksWhenEmpty() {
        List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks);
        assertEquals(0, tasks.size());
    }
    @Test
    public void getEpicTasks() {
        EpicTask epicTask = new EpicTask("Test epicTask 1", "Test task 1 description");
        taskManager.addNewEpicTask(epicTask);
        List<EpicTask> epicTasks = taskManager.getEpicTasks();

        assertNotNull(epicTasks);
        assertEquals(epicTask, epicTasks.get(0));
        assertEquals(1, epicTasks.size());
    }
    @Test
    public void getEpicTasksWhenEmpty() {
        List<EpicTask> epicTasks = taskManager.getEpicTasks();

        assertNotNull(epicTasks);
        assertEquals(0, epicTasks.size());
    }
    @Test
    public void getSubTasks() {
        int epicId = taskManager.addNewEpicTask(new EpicTask("Test epicTask 1", "Test task 1 description"));
        SubTask subTask = new SubTask("Test SubTask 1", "Test SubTask 1 description", TaskStatus.NEW, epicId, 0, null);
        taskManager.addNewSubTask(subTask);
        List<SubTask> subTasks = taskManager.getSubTasks();

        assertNotNull(subTasks);
        assertEquals(subTask, subTasks.get(0));
        assertEquals(1, subTasks.size());
    }
    @Test
    public void getSubTasksWhenEmpty() {
        List<SubTask> subTasks = taskManager.getSubTasks();

        assertNotNull(subTasks);
        assertEquals(0, subTasks.size());
    }

    @Test
    public void getEpicSubTasks() {
        int epicId = taskManager.addNewEpicTask(new EpicTask("Test epicTask 1", "Test task 1 description"));
        SubTask subTask = new SubTask("Test SubTask 1", "Test SubTask 1 description", TaskStatus.NEW, epicId, 0, null);
        taskManager.addNewSubTask(subTask);
        List<SubTask> subTasks = taskManager.getEpicSubTasks(epicId);

        assertNotNull(subTasks);
        assertEquals(1, subTasks.size());
    }
    @Test
    public void getEpicSubTasksWhenEmpty() {
        int epicId = taskManager.addNewEpicTask(new EpicTask("Test epicTask 1", "Test task 1 description"));
        List<SubTask> subTasks = taskManager.getEpicSubTasks(epicId);

        assertNotNull(subTasks);
        assertEquals(0, subTasks.size());
    }
    @Test
    public void getEpicSubTasksWithWrongEpicId() {
        List<SubTask> subTasks = taskManager.getEpicSubTasks(NON_EXIST_TASK_ID);

        assertNull(subTasks);
        assertEquals(0, taskManager.getSubTasks().size());
    }

    @Test
    public void clearTasks() {
        taskManager.addNewTask(new Task("Test task 1", "Test task 1 description", TaskStatus.NEW, 0, null));
        taskManager.clearTasks();

        assertNotNull(taskManager.getTasks());
        assertTrue(taskManager.getTasks().isEmpty());
    }
    @Test
    public void clearTasksWhenEmpty() {
        taskManager.clearTasks();

        assertNotNull(taskManager.getTasks());
        assertTrue(taskManager.getTasks().isEmpty());
    }
    @Test
    public void clearEpicTasks() {
        taskManager.addNewEpicTask(new EpicTask("Test epicTask 1", "Test epicTask 1 description"));
        taskManager.clearEpicTasks();

        assertNotNull(taskManager.getEpicTasks());
        assertTrue(taskManager.getEpicTasks().isEmpty());
        assertTrue(taskManager.getSubTasks().isEmpty());
    }
    @Test
    public void clearEpicTasksWhenEmpty() {
        taskManager.clearEpicTasks();

        assertNotNull(taskManager.getEpicTasks());
        assertTrue(taskManager.getEpicTasks().isEmpty());
        assertTrue(taskManager.getSubTasks().isEmpty());
    }
    @Test
    public void clearSubTasks() {
        int epicId = taskManager.addNewEpicTask(new EpicTask("Test epicTask 1", "Test epicTask 1 description"));
        taskManager.addNewSubTask(new SubTask("Test SubTask 1", "Test SubTask 1 description", TaskStatus.NEW, epicId, 0, null));
        taskManager.clearSubTasks();

        assertNotNull(taskManager.getSubTasks());
        assertTrue(taskManager.getSubTasks().isEmpty());
        assertTrue(taskManager.getEpicSubTasks(epicId).isEmpty());

    }
    @Test
    public void clearSubTasksWhenEmpty() {
        int epicId = taskManager.addNewEpicTask(new EpicTask("Test epicTask 1", "Test epicTask 1 description"));
        taskManager.clearSubTasks();

        assertNotNull(taskManager.getSubTasks());
        assertTrue(taskManager.getSubTasks().isEmpty());
        assertTrue(taskManager.getEpicSubTasks(epicId).isEmpty());
    }
    @Test
    public void getTask() {
        Task task = new Task("Test task 1", "Test task 1 description", TaskStatus.NEW, 0, null);
        int taskId = taskManager.addNewTask(task);

        assertEquals(task, taskManager.getTask(taskId));
        assertEquals(taskId, taskManager.getTask(taskId).getId());
    }
    @Test
    public void getTaskWithWrongId() {
        Task task = new Task("Test task 1", "Test task 1 description", TaskStatus.NEW, 0, null);
        taskManager.addNewTask(task);

        assertNull(taskManager.getTask(NON_EXIST_TASK_ID));
    }
    @Test
    public void getEpicTask() {
        EpicTask epicTask = new EpicTask("Test epicTask 1", "Test epicTask 1 description");
        int epicTaskId = taskManager.addNewEpicTask(epicTask);

        assertEquals(epicTask, taskManager.getEpicTask(epicTaskId));
        assertEquals(epicTaskId, taskManager.getEpicTask(epicTaskId).getId());
    }
    @Test
    public void getEpicTaskWithWrongId() {
        EpicTask epicTask = new EpicTask("Test epicTask 1", "Test epicTask 1 description");
        taskManager.addNewTask(epicTask);

        assertNull(taskManager.getEpicTask(NON_EXIST_TASK_ID));
    }
    @Test
    public void getSubTask() {
        int epicTaskId = taskManager.addNewEpicTask(
                new EpicTask("Test epicTask 1", "Test epicTask 1 description")
        );
        SubTask subTask = new SubTask("Test SubTask 1"
                , "Test SubTask 1 description", TaskStatus.NEW, epicTaskId, 0, null);
        int subTaskId = taskManager.addNewSubTask(subTask);

        assertEquals(subTask, taskManager.getSubTask(subTaskId));
        assertEquals(subTaskId, taskManager.getSubTask(subTaskId).getId());
    }
    @Test
    public void getSubTaskWithWrongId() {
        int epicTaskId = taskManager.addNewEpicTask(
                new EpicTask("Test epicTask 1", "Test epicTask 1 description")
        );
        SubTask subTask = new SubTask("Test SubTask 1"
                , "Test SubTask 1 description", TaskStatus.NEW, epicTaskId, 0, null);
        taskManager.addNewSubTask(subTask);

        assertNull(taskManager.getSubTask(NON_EXIST_TASK_ID));
    }

    @Test
    public void addNewTask() {
        Task task = new Task("Test task 1", "Test task 1 description", TaskStatus.NEW, 0, null);
        int taskId = taskManager.addNewTask(task);

        assertNotNull(taskManager.getTask(taskId));
        assertEquals(task, taskManager.getTask(taskId));
        assertEquals(1, taskManager.getTasks().size());
    }
    @Test
    public void addNewNullTask() {
        int taskId = taskManager.addNewTask(null);

        assertEquals(-1, taskId);
        assertNull(taskManager.getTask(taskId));
        assertEquals(0, taskManager.getTasks().size());
    }
    @Test
    public void addNewEpicTask() {
        EpicTask epicTask = new EpicTask("Test epicTask 1", "Test epicTask 1 description");
        int epicTaskId = taskManager.addNewEpicTask(epicTask);

        assertNotNull(taskManager.getEpicTask(epicTaskId));
        assertEquals(epicTask, taskManager.getEpicTask(epicTaskId));
        assertEquals(1, taskManager.getEpicTasks().size());
    }
    @Test
    public void addNewNullEpicTask() {
        int epicTaskId = taskManager.addNewEpicTask(null);

        assertEquals(-1, epicTaskId);
        assertNull(taskManager.getTask(epicTaskId));
        assertEquals(0, taskManager.getEpicTasks().size());
    }
    @Test
    public void addNewSubTask() {
        int epicTaskId = taskManager.addNewEpicTask(
                new EpicTask("Test epicTask 1", "Test epicTask 1 description")
        );
        SubTask subTask = new SubTask("Test SubTask 1"
                , "Test SubTask 1 description", TaskStatus.NEW, epicTaskId, 0, null);
        int subTaskId = taskManager.addNewSubTask(subTask);

        assertNotNull(taskManager.getSubTask(subTaskId));
        assertEquals(subTask, taskManager.getSubTask(subTaskId));
        assertEquals(1, taskManager.getSubTasks().size());
    }
    @Test
    public void addNewNullSubTask() {
        int subTaskId = taskManager.addNewSubTask(null);

        assertEquals(-1, subTaskId);
        assertNull(taskManager.getTask(subTaskId));
        assertEquals(0, taskManager.getSubTasks().size());
    }
    @Test
    public void addNewSubTaskWithWrongEpicId() {
        SubTask subTask = new SubTask("Test SubTask 1"
                , "Test SubTask 1 description", TaskStatus.NEW, NON_EXIST_TASK_ID, 0, null);
        int subTaskId = taskManager.addNewSubTask(subTask);

        assertNull(taskManager.getSubTask(subTaskId));
        assertEquals(-1, subTaskId);
        assertEquals(0, taskManager.getSubTasks().size());
    }

    @Test
    public void updateTask() {
        int taskId = taskManager.addNewTask(
                new Task("Test task 1", "Test task 1 description", TaskStatus.NEW, 0, null)
        );
        Task updatedTask = new Task(taskId, "Updated test task 1", "Updated test task 1 description"
                , TaskStatus.IN_PROGRESS, 0, null);
        taskManager.updateTask(updatedTask);

        assertEquals(updatedTask, taskManager.getTask(taskId));
        assertEquals(1, taskManager.getTasks().size());
        assertEquals(updatedTask.getTaskType(), taskManager.getTask(taskId).getTaskType());
        assertEquals(updatedTask.getName(), taskManager.getTask(taskId).getName());
        assertEquals(updatedTask.getDescription(), taskManager.getTask(taskId).getDescription());
    }
    @Test
    public void updateEpicTask() {
        int epicTaskId = taskManager.addNewEpicTask(
                new EpicTask("Test epicTask 1", "Test epicTask 1 description")
        );
        EpicTask updatedEpicTask = new EpicTask(epicTaskId, "Updated test epicTask 1", "Updated test epicTask 1 description");
        taskManager.updateEpicTask(updatedEpicTask);

        assertEquals(updatedEpicTask, taskManager.getEpicTask(epicTaskId));
        assertEquals(1, taskManager.getEpicTasks().size());
        assertEquals(updatedEpicTask.getTaskType(), taskManager.getEpicTask(epicTaskId).getTaskType());
        assertEquals(updatedEpicTask.getName(), taskManager.getEpicTask(epicTaskId).getName());
        assertEquals(updatedEpicTask.getDescription(), taskManager.getEpicTask(epicTaskId).getDescription());
    }
    @Test
    public void updateSubTask() {
        int epicTaskId = taskManager.addNewEpicTask(
                new EpicTask("Test epicTask 1", "Test epicTask 1 description")
        );
        int subTaskId = taskManager.addNewSubTask(new SubTask("Test SubTask 1"
                , "Test SubTask 1 description", TaskStatus.NEW, epicTaskId, 0, null));
        SubTask updatedSubTask = new SubTask(subTaskId,"Updated test SubTask 1"
                , "Updated test SubTask 1 description", TaskStatus.IN_PROGRESS, epicTaskId, 0, null);
        taskManager.updateSubTask(updatedSubTask);

        assertEquals(updatedSubTask, taskManager.getSubTask(subTaskId));
        assertEquals(1, taskManager.getSubTasks().size());
        assertEquals(updatedSubTask.getTaskType(), taskManager.getSubTask(subTaskId).getTaskType());
        assertEquals(updatedSubTask.getName(), taskManager.getSubTask(subTaskId).getName());
        assertEquals(updatedSubTask.getDescription(), taskManager.getSubTask(subTaskId).getDescription());
    }

    @Test
    public void deleteTask() {
        Task task = new Task("Test task 1", "Test task 1 description", TaskStatus.NEW, 0, null);
        int taskId = taskManager.addNewTask(task);

        taskManager.deleteTask(taskId);

        assertEquals(0, taskManager.getTasks().size());
    }
    @Test
    public void deleteTaskWithWrongId() {
        taskManager.deleteTask(NON_EXIST_TASK_ID);

        assertEquals(0, taskManager.getTasks().size());
    }
    @Test
    public void deleteEpicTask() {
        int epicTaskId = taskManager.addNewEpicTask(
                new EpicTask("Test epicTask 1", "Test epicTask 1 description")
        );
        taskManager.addNewSubTask(new SubTask("Test SubTask 1"
                , "Test SubTask 1 description", TaskStatus.NEW, epicTaskId, 0, null));

        taskManager.deleteEpicTask(epicTaskId);

        assertEquals(0, taskManager.getEpicTasks().size());
        assertEquals(0, taskManager.getSubTasks().size());
    }
    @Test
    public void deleteEpicTaskWithWrongId() {
        taskManager.deleteEpicTask(NON_EXIST_TASK_ID);

        assertEquals(0, taskManager.getEpicTasks().size());
    }
    @Test
    public void deleteSubTask() {
        int epicTaskId = taskManager.addNewEpicTask(
                new EpicTask("Test epicTask 1", "Test epicTask 1 description")
        );
        int subTaskId = taskManager.addNewSubTask(new SubTask("Test SubTask 1"
                , "Test SubTask 1 description", TaskStatus.NEW, epicTaskId, 0, null));

        taskManager.deleteSubTask(subTaskId);

        assertEquals(0, taskManager.getSubTasks().size());
        assertEquals(1, taskManager.getEpicTasks().size());
    }
    @Test
    public void deleteSubTaskWithWrongId() {
        taskManager.deleteSubTask(NON_EXIST_TASK_ID);

        assertEquals(0, taskManager.getSubTasks().size());
    }
    @Test
    public void updatingEpicStatusWithEmptySubtasks() {
        int epicTaskId = taskManager.addNewEpicTask(
                new EpicTask("Test epicTask 1", "Test epicTask 1 description")
        );

        assertFalse(taskManager.getEpicTasks().isEmpty());
        assertEquals(TaskStatus.NEW, taskManager.getEpicTask(epicTaskId).getStatus());
    }
    @Test
    public void updatingEpicStatusWithSubtasksAllNew() {
        int epicTaskId = taskManager.addNewEpicTask(
                new EpicTask("Test epicTask 1", "Test epicTask 1 description")
        );
        taskManager.addNewSubTask(new SubTask("Test SubTask 1"
                , "Test SubTask 1 description", TaskStatus.NEW, epicTaskId, 0, null));
        taskManager.addNewSubTask(new SubTask("Test SubTask 2"
                , "Test SubTask 2 description", TaskStatus.NEW, epicTaskId, 0, null));

        assertFalse(taskManager.getEpicTasks().isEmpty());
        assertTrue(taskManager.getSubTasks().size() > 0);
        assertEquals(TaskStatus.NEW, taskManager.getEpicTask(epicTaskId).getStatus());
    }
    @Test
    public void updatingEpicStatusWithSubtasksAllDone() {
        int epicTaskId = taskManager.addNewEpicTask(
                new EpicTask("Test epicTask 1", "Test epicTask 1 description")
        );
        taskManager.addNewSubTask(new SubTask("Test SubTask 1"
                , "Test SubTask 1 description", TaskStatus.DONE, epicTaskId, 0, null));
        taskManager.addNewSubTask(new SubTask("Test SubTask 2"
                , "Test SubTask 2 description", TaskStatus.DONE, epicTaskId, 0, null));

        assertFalse(taskManager.getEpicTasks().isEmpty());
        assertTrue(taskManager.getSubTasks().size() > 0);
        assertEquals(TaskStatus.DONE, taskManager.getEpicTask(epicTaskId).getStatus());
    }
    @Test
    public void updatingEpicStatusWithSubtaskInProgress() {
        int epicTaskId = taskManager.addNewEpicTask(
                new EpicTask("Test epicTask 1", "Test epicTask 1 description")
        );
        taskManager.addNewSubTask(new SubTask("Test SubTask 1"
                , "Test SubTask 1 description", TaskStatus.NEW, epicTaskId, 0, null));
        taskManager.addNewSubTask(new SubTask("Test SubTask 2"
                , "Test SubTask 2 description", TaskStatus.IN_PROGRESS, epicTaskId, 0, null));


        assertFalse(taskManager.getEpicTasks().isEmpty());
        assertTrue(taskManager.getSubTasks().size() > 0);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicTask(epicTaskId).getStatus());
    }
    @Test
    public void updatingEpicStatusWithSubtasksNewAndDone() {
        int epicTaskId = taskManager.addNewEpicTask(
                new EpicTask("Test epicTask 1", "Test epicTask 1 description")
        );
        taskManager.addNewSubTask(new SubTask("Test SubTask 1"
                , "Test SubTask 1 description", TaskStatus.NEW, epicTaskId, 0, null));
        taskManager.addNewSubTask(new SubTask("Test SubTask 2"
                , "Test SubTask 2 description", TaskStatus.DONE, epicTaskId, 0, null));


        assertFalse(taskManager.getEpicTasks().isEmpty());
        assertTrue(taskManager.getSubTasks().size() > 0);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicTask(epicTaskId).getStatus());
    }

    @Test
    public void addNewCrossedTasks() {
        taskManager.addNewTask(new Task("Test task 1", "Test task 1 description", TaskStatus.NEW, 59, LocalDateTime.parse("15-02-2023--15:00", DT_FORMATTER)));

        taskManager.addNewTask(new Task("Test task 2", "Test task 2 description", TaskStatus.NEW, 29, LocalDateTime.parse("15-02-2023--14:40", DT_FORMATTER)));
        taskManager.addNewTask(new Task("Test task 2", "Test task 3 description", TaskStatus.NEW, 29, LocalDateTime.parse("15-02-2023--15:50", DT_FORMATTER)));

        assertEquals(1, taskManager.getTasks().size());
    }
    @Test
    public void updateTaskToBeCrossed() {
        taskManager.addNewTask(new Task("Test task 1", "Test task 1 description", TaskStatus.NEW, 59, LocalDateTime.parse("15-02-2023--15:00", DT_FORMATTER)));
        Task task2 = new Task("Test task 2", "Test task 2 description", TaskStatus.NEW, 29, LocalDateTime.parse("15-02-2023--16:00", DT_FORMATTER));
        int task2Id = taskManager.addNewTask(task2);

        taskManager.updateTask(new Task(task2Id, "Updated1 test task 2", "Updated1 test task 2 description", TaskStatus.NEW, 29, LocalDateTime.parse("15-02-2023--15:20", DT_FORMATTER)));
        taskManager.updateTask(new Task(task2Id, "Updated2 test task 2", "Updated2 test task 2 description", TaskStatus.NEW, 29, LocalDateTime.parse("15-02-2023--14:40", DT_FORMATTER)));
        taskManager.updateTask(new Task(task2Id, "Updated2 test task 3", "Updated3 test task 2 description", TaskStatus.NEW, 29, LocalDateTime.parse("15-02-2023--15:50", DT_FORMATTER)));

        assertEquals(task2, taskManager.getTask(task2Id));
    }
}
