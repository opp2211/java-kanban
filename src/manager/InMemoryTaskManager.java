package manager;

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager{
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, EpicTask> epicTasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private int idGenerator = 0;
    private final List<Task> viewsHistory = new ArrayList<>();

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<EpicTask> getEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public ArrayList<SubTask> getEpicSubTasks(int id) {
        ArrayList<SubTask> epicSubTasks = new ArrayList<>();
        EpicTask epic = epicTasks.get(id);
        for (Integer subTaskId : epic.getSubTaskIds()) {
            epicSubTasks.add(subTasks.get(subTaskId));
        }
        return epicSubTasks;
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
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

    @Override
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

    @Override
    public Task getTask(int id) {
        Task task = tasks.getOrDefault(id, null);
        if (task != null)
            addHistoryItem(task);
        return task;
    }

    @Override
    public EpicTask getEpicTask(int id) {
        EpicTask epicTask = epicTasks.getOrDefault(id, null);
        if (epicTask != null)
            addHistoryItem(epicTask);
        return epicTask;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.getOrDefault(id, null);
        if (subTask != null)
            addHistoryItem(subTask);
        return subTask;
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public void updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpicTask(EpicTask epicTask) {
        if (epicTask != null && epicTasks.containsKey(epicTask.getId())) {
            epicTasks.put(epicTask.getId(), epicTask);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTask != null && subTasks.containsKey(subTask.getId())) {
            subTasks.put(subTask.getId(), subTask);
            // Обновляем статус эпика
            updateEpicTaskStatus(
                    epicTasks.get(subTask.getEpicTaskId())
            );
        }
    }

    @Override
    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else System.out.println("Невозможно выполнить операцию");
    }

    @Override
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

    @Override
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

    @Override
    public List<Task> getHistory() {
        return null;
    }

    private void updateEpicTaskStatus(EpicTask epicTask) {
        if (epicTask == null)
            return;

        if (epicTask.getSubTaskIds().isEmpty()) {
            epicTask.setStatus(TaskStatus.NEW);
            return;
        }

        ArrayList<TaskStatus> subTaskStatuses = new ArrayList<>(); /* Я не разобрался с коллекцией Set еще, посмотрю
                                            попозже, но вроде бы она должна быть у нас в курсе через пару спринтов */
        for (Integer subTaskId : epicTask.getSubTaskIds()) {
            TaskStatus subTaskStatus = subTasks.get(subTaskId).getStatus();
            subTaskStatuses.add(subTaskStatus);
        }

        if (subTaskStatuses.contains(TaskStatus.NEW)
                && !subTaskStatuses.contains(TaskStatus.IN_PROGRESS) && !subTaskStatuses.contains(TaskStatus.DONE)) {
            epicTask.setStatus(TaskStatus.NEW);

        } else if (subTaskStatuses.contains(TaskStatus.DONE)
                && !subTaskStatuses.contains(TaskStatus.IN_PROGRESS) && !subTaskStatuses.contains(TaskStatus.NEW)) {
            epicTask.setStatus(TaskStatus.DONE);
        } else {
            epicTask.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
    private void addHistoryItem(Task task) {
        final int MAX_HISTORY_SIZE = 10;
        if (viewsHistory.size() == MAX_HISTORY_SIZE) {
            viewsHistory.remove(0);
        }
        viewsHistory.add(task);
    }
}
