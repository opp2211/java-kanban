package manager;

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, EpicTask> epicTasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private int idGenerator = 0;

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<EpicTask> getEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public ArrayList<SubTask> getEpicSubTasks(int id) {
        ArrayList<SubTask> epicSubTasks = new ArrayList<>();
        EpicTask epic = epicTasks.get(id);
        for (Integer subTaskId : epic.getSubTaskIds()) {
            epicSubTasks.add(subTasks.get(subTaskId));
        }
        return epicSubTasks;
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearEpicTasks() {
        // При очистке эпиков для начала удаляем сабТаски связанные с этими эпиками
        // Возникает сложная логика, тк мы храним не ссылки на сабТаски, а их айдишники
        for (EpicTask epicTask : epicTasks.values()) {
            for (Integer subTaskId : epicTask.getSubTaskIds()) {
                subTasks.remove(subTaskId);
            }
        }
        // Удаляем сами эпики
        epicTasks.clear();
    }

    public void clearSubTasks() {
        // При очистке сабТасков для начала удаляем ссылки на них в эпиках
        for (SubTask subTask : subTasks.values()) {
            int epicId = subTask.getEpicTaskId();
            int subTaskId = subTask.getId();
            EpicTask epicTask = epicTasks.get(epicId);

            epicTask.getSubTaskIds().remove(subTaskId);
            // Обновляем статусы эпиков
            updateEpicTaskStatus(epicTask);
        }
        // Удаляем сами сабТаски
        subTasks.clear();
    }

    public Task getTask(int id) {
        if (tasks.containsKey(id))
            return tasks.get(id);
        else
            return null;
    }

    public EpicTask getEpicTask(int id) {
        if (epicTasks.containsKey(id))
            return epicTasks.get(id);
        else
            return null;
    }

    public SubTask getSubTask(int id) {
        if (subTasks.containsKey(id))
            return subTasks.get(id);
        else
            return null;
    }

    public void addNewTask(Task task) {
        if (task != null) {
            int newId = idGenerator++;
            tasks.put(newId, task);
            task.setId(newId);
        } else System.out.println("Невозможно выполнить операцию");
    }

    public void addNewEpicTask(EpicTask epicTask) {
        if (epicTask != null) {
            int newId = idGenerator++;
            epicTasks.put(newId, epicTask);
            epicTask.setId(newId);
        } else System.out.println("Невозможно выполнить операцию");
    }

    public void addNewSubTask(SubTask subTask) {
        if (subTask != null) {
            int newId = idGenerator++;
            subTasks.put(newId, subTask);

            // Привязываем сабТаск к эпику
            EpicTask epicTask = epicTasks.get(subTask.getEpicTaskId());
            epicTask.addSubTaskId(subTask.getEpicTaskId());
            updateEpicTaskStatus(epicTask);
            // Обновляем статусы эпиков
            updateEpicTaskStatus(epicTask);
        } else System.out.println("Невозможно выполнить операцию");
    }

    public void updateTask(Task task) {
        if (task != null) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateEpicTask(EpicTask epicTask) {
        if (epicTask != null) {
            epicTasks.put(epicTask.getId(), epicTask);
        }
    }

    public void updateSubTask(SubTask subTask) {
        if (subTask != null) {
            subTasks.put(subTask.getId(), subTask);
            // Обновляем статус эпика
            updateEpicTaskStatus(
                    epicTasks.get(subTask.getEpicTaskId())
            );
        }
    }

    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else System.out.println("Невозможно выполнить операцию");
    }

    public void deleteEpicTask(int id) {
        if (epicTasks.containsKey(id)) {
            // При удалении эпика удаляем всё связанные саб-таски
            EpicTask epicTask = epicTasks.get(id);
            for (Integer subTaskId : epicTask.getSubTaskIds()) {
                subTasks.remove(subTaskId);
            }
            // Удаляем сам эпик
            epicTasks.remove(id);
        } else System.out.println("Невозможно выполнить операцию");

    }

    public void deleteSubTask(int id) {
        if (subTasks.containsKey(id)) {
            // При удалении саб-таска надо удалить ссылки из его эпика
            int epicTaskId = subTasks.get(id).getEpicTaskId();
            EpicTask epicTask = epicTasks.get(epicTaskId);
            epicTask.getSubTaskIds().remove(id);
            // Обновляем статус эпика
            updateEpicTaskStatus(epicTask);
            //Удаляем саму таску
            subTasks.remove(id);
        } else System.out.println("Невозможно выполнить операцию");
    }

    private void updateEpicTaskStatus(EpicTask epicTask) {
        if (epicTask == null)
            return;

        if (epicTask.getSubTaskIds().isEmpty()) {
            epicTask.setStatus("NEW");
            return;
        }

        ArrayList<String> subTaskStatuses = new ArrayList<>();
        for (Integer subTaskId : epicTask.getSubTaskIds()) {
            String subTaskStatus = subTasks.get(subTaskId).getStatus();
            subTaskStatuses.add(subTaskStatus);
        }

        if (subTaskStatuses.contains("NEW") && !subTaskStatuses.contains("IN_PROGRESS") && !subTaskStatuses.contains("DONE")) {
            epicTask.setStatus("NEW");

        } else if (subTaskStatuses.contains("DONE") && !subTaskStatuses.contains("IN_PROGRESS") && !subTaskStatuses.contains("NEW")) {
            epicTask.setStatus("DONE");
        } else {
            epicTask.setStatus("IN_PROGRESS");
        }
    }
}
