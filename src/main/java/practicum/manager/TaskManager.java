package practicum.manager;

import practicum.tasks.EpicTask;
import practicum.tasks.SubTask;
import practicum.tasks.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getTasks();

    List<EpicTask> getEpicTasks();

    List<SubTask> getSubTasks();
    List<Task> getPrioritizedTasks();

    List<SubTask> getEpicSubTasks(int id);

    void clearTasks();

    void clearEpicTasks();

    void clearSubTasks();

    Task getTask(int id);

    EpicTask getEpicTask(int id);

    SubTask getSubTask(int id);

    int addNewTask(Task task);

    int addNewEpicTask(EpicTask epicTask);

    int addNewSubTask(SubTask subTask);

    void updateTask(Task task);

    void updateEpicTask(EpicTask epicTask);

    void updateSubTask(SubTask subTask);

    void deleteTask(int id);

    void deleteEpicTask(int id);

    void deleteSubTask(int id);

    List<Task> getHistory();
}
