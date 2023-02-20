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

    public HttpTaskManager() {
        kvTaskClient = new KVTaskClient(8078);
        gson = Managers.getGson();
    }
    public static HttpTaskManager getLoaded() {
        HttpTaskManager httpTaskManager = new HttpTaskManager();
        httpTaskManager.load();
        return httpTaskManager;
    }
    private void load() {
        addTasks(gson.fromJson(kvTaskClient.load("tasks"), new TypeToken<ArrayList<Task>>() {}.getType()));
        addTasks(gson.fromJson(kvTaskClient.load("epicTasks"), new TypeToken<ArrayList<EpicTask>>() {}.getType()));
        addTasks(gson.fromJson(kvTaskClient.load("subTasks"), new TypeToken<ArrayList<SubTask>>() {}.getType()));

        List<Integer> history = gson.fromJson(kvTaskClient.load("history"),
                new TypeToken<ArrayList<Integer>>() {}.getType());
        if (history != null)
            history.stream()
                    .map(id -> {
                        if (tasks.containsKey(id)) {
                            return tasks.get(id);
                        } else if (epicTasks.containsKey(id)) {
                            return epicTasks.get(id);
                        } else
                            return subTasks.get(id);
                    })
                    .forEach(historyManager::add);
    }
    @Override
    protected void save() {
        kvTaskClient.put("tasks", gson.toJson(new ArrayList<>(tasks.values())));
        kvTaskClient.put("epicTasks", gson.toJson(new ArrayList<>(epicTasks.values())));
        kvTaskClient.put("subTasks", gson.toJson(new ArrayList<>(subTasks.values())));

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
                case EPIC_TASK:
                    EpicTask epicTask = (EpicTask) task;
                    epicTask.getSubTaskIds().clear();
                    addEpicTask(epicTask);
                    break;
                case SUB_TASK:
                    addSubTask((SubTask) task);
                    break;

            }
            if (id + 1 > idGenerator)
                idGenerator = id + 1;
        }
    }
}
