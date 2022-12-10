package manager;

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager{
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
        return tasks.getOrDefault(id, null);
    }

    public EpicTask getEpicTask(int id) {
        return epicTasks.getOrDefault(id, null);
    }

    public SubTask getSubTask(int id) {
        return subTasks.getOrDefault(id, null);
    }

    public int addNewTask(Task task) {
        if (task != null) {
            int newId = idGenerator++;
            tasks.put(newId, task);
            task.setId(newId);
            return newId;
        } else {
            System.out.println("Невозможно выполнить операцию");
            return -1;
        }
    }

    public int addNewEpicTask(EpicTask epicTask) {
        if (epicTask != null) {
            int newId = idGenerator++;
            epicTasks.put(newId, epicTask);
            epicTask.setId(newId);
            return newId;
        } else {
            System.out.println("Невозможно выполнить операцию");
            return -1;
        }
    }

    public int addNewSubTask(SubTask subTask) {
        if (subTask != null) {
            int newId = idGenerator++;
            subTasks.put(newId, subTask);
            subTask.setId(newId);

            // Привязываем сабТаск к эпику
            EpicTask epicTask = epicTasks.get(subTask.getEpicTaskId());
            epicTask.addSubTaskId(subTask.getId());
            // Обновляем статусы эпиков
            updateEpicTaskStatus(epicTask);

            return newId;
        } else {
            System.out.println("Невозможно выполнить операцию");
            return -1;
        }
    }

    public void updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateEpicTask(EpicTask epicTask) {
        if (epicTask != null && epicTasks.containsKey(epicTask.getId())) {
            epicTasks.put(epicTask.getId(), epicTask);
        }
    }

    public void updateSubTask(SubTask subTask) {
        if (subTask != null && subTasks.containsKey(subTask.getId())) {
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

        ArrayList<String> subTaskStatuses = new ArrayList<>(); /* Я не разобрался с этой коллекцией еще, посмотрю
                                            попозже, но вроде бы она должна быть у нас в курсе через пару спринтов */
        for (Integer subTaskId : epicTask.getSubTaskIds()) {
            String subTaskStatus = subTasks.get(subTaskId).getStatus();
            subTaskStatuses.add(subTaskStatus);
        }

        if (subTaskStatuses.contains("NEW")
                && !subTaskStatuses.contains("IN_PROGRESS") && !subTaskStatuses.contains("DONE")) {
            epicTask.setStatus("NEW");

        } else if (subTaskStatuses.contains("DONE")
                && !subTaskStatuses.contains("IN_PROGRESS") && !subTaskStatuses.contains("NEW")) {
            epicTask.setStatus("DONE");
        } else {
            epicTask.setStatus("IN_PROGRESS");
        }
    }
}
