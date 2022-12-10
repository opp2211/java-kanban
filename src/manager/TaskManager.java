package manager;

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    ArrayList<Task> getTasks();

    ArrayList<EpicTask> getEpicTasks();

    ArrayList<SubTask> getSubTasks();

    ArrayList<SubTask> getEpicSubTasks(int id);

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
