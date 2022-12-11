package manager;

import historyManager.HistoryManager;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, EpicTask> epicTasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistoryManager();
    private int idGenerator = 0;

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
        subTasks.clear();
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
        if (task != null) historyManager.add(task);
        return task;
    }

    @Override
    public EpicTask getEpicTask(int id) {
        EpicTask epicTask = epicTasks.getOrDefault(id, null);
        if (epicTask != null) historyManager.add(epicTask);
        return epicTask;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.getOrDefault(id, null);
        if (subTask != null) historyManager.add(subTask);
        return subTask;
    }

    @Override
    public int addNewTask(Task task) {
        if (task != null) {
            tasks.put(idGenerator, task);
            task.setId(idGenerator);
            return idGenerator++;
        } else {
            System.out.println("Передана пустая ссылка на объект задачи. Добавление задачи не выполнено");
            return -1;
        }
    }

    @Override
    public int addNewEpicTask(EpicTask epicTask) {
        if (epicTask != null) {
            epicTasks.put(idGenerator, epicTask);
            epicTask.setId(idGenerator);
            return idGenerator++;
        } else {
            System.out.println("Передана пустая ссылка на объект задачи. Добавление задачи не выполнено");
            return -1;
        }
    }

    @Override
    public int addNewSubTask(SubTask subTask) {
        if (subTask != null) {
            subTasks.put(idGenerator, subTask);
            subTask.setId(idGenerator);

            // Привязываем сабТаск к эпику
            EpicTask epicTask = epicTasks.get(subTask.getEpicTaskId());
            epicTask.addSubTaskId(subTask.getId());
            // Обновляем статусы эпиков
            updateEpicTaskStatus(epicTask);

            return idGenerator++;
        } else {
            System.out.println("Передана пустая ссылка на объект задачи. Добавление задачи не выполнено");
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
            updateEpicTaskStatus(epicTasks.get(subTask.getEpicTaskId()));
        }
    }

    @Override
    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else System.out.println("Удаление не выполнено. Запрашиваемый ID не найден.");
    }

    @Override
    public void deleteEpicTask(int id) {
        if (epicTasks.containsKey(id)) {
            // При удалении эпика удаляем всё связанные саб-таски
            EpicTask epicTask = epicTasks.get(id);
            deleteSubTasksFromEpic(epicTask);
            // Удаляем сам эпик
            epicTasks.remove(id);
        } else System.out.println("Удаление не выполнено. Запрашиваемый ID не найден.");

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
        } else System.out.println("Удаление не выполнено. Запрашиваемый ID не найден.");
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicTaskStatus(EpicTask epicTask) {
        if (epicTask == null) return;

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

    private void deleteSubTasksFromEpic(EpicTask epicTask) {
        for (Integer subTaskId : epicTask.getSubTaskIds()) {
            subTasks.remove(subTaskId);
        }
    }
}
