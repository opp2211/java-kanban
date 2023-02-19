package practicum.manager.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import practicum.manager.file.FileBackedTaskManager;
import practicum.manager.Managers;
import practicum.tasks.EpicTask;
import practicum.tasks.SubTask;
import practicum.tasks.Task;
import practicum.tasks.TaskType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTaskManager {
    private final KVTaskClient kvTaskClient;
    private final Gson gson;

    public HttpTaskManager(String host) {
        kvTaskClient = new KVTaskClient(host, 8078);
        gson = Managers.getGson();
    }
    public static HttpTaskManager getLoaded(String host) {
        HttpTaskManager httpTaskManager = new HttpTaskManager(host);
        httpTaskManager.load();
        return httpTaskManager;
    }
    private void load() {
        addTasks(gson.fromJson(kvTaskClient.load("tasks"), new TypeToken<ArrayList<Task>>() {}.getType()));
        addTasks(gson.fromJson(kvTaskClient.load("subTasks"), new TypeToken<ArrayList<Task>>() {}.getType()));
        addTasks(gson.fromJson(kvTaskClient.load("epicTasks"), new TypeToken<ArrayList<Task>>() {}.getType()));

        List<Integer> history = gson.fromJson(kvTaskClient.load("epicTasks"),
                new TypeToken<ArrayList<Task>>() {}.getType());
        if (history != null)
            history.stream()
                    .map(this::getTask)
                    .forEach(historyManager::add);
    }
    @Override
    protected void save() {
        kvTaskClient.put("tasks", gson.toJson(new ArrayList<>(tasks.values())));
        kvTaskClient.put("subTasks", gson.toJson(new ArrayList<>(subTasks.values())));
        kvTaskClient.put("epicTasks", gson.toJson(new ArrayList<>(epicTasks.values())));

        kvTaskClient.put("history", gson.toJson(
                historyManager.getHistory().stream()
                        .map(Task::getId)
                        .collect(Collectors.toList())
        ));
    }
    private void addTasks(List<? extends Task> tasks) {
        if (tasks == null)
            return;
        for (Task task : tasks) {
            final int id = task.getId();
            TaskType taskType = task.getTaskType();
            switch (taskType) {
                case TASK:
                    addTask(task);
                    break;
                case SUB_TASK:
                    addSubTask((SubTask) task);
                    break;
                case EPIC_TASK:
                    addEpicTask((EpicTask) task);
                    break;
            }
            if (id + 1 > idGenerator)
                idGenerator = id + 1;
        }
    }
}
