package manager;

import historymanager.HistoryManager;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, EpicTask> epicTasks = new HashMap<>();
    protected final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistoryManager();
    protected int idGenerator = 0;

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<EpicTask> getEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<SubTask> getEpicSubTasks(int id) {
        ArrayList<SubTask> epicSubTasks = new ArrayList<>();
        EpicTask epic = epicTasks.get(id);
        if (epic != null)
            for (Integer subTaskId : epic.getSubTaskIds()) {
                epicSubTasks.add(subTasks.get(subTaskId));
            }
            return epicSubTasks;
        }
        else {
            System.out.println("getEpicSubTasks: ID переданного эпика не существует, " +
                    "вывод списка сабтасков определенного эпика не выполнен");
            return null;
        }
    }

    @Override
    public void clearTasks() {
        for (var task : tasks.values()) {
            historyManager.remove(task.getId());
        }
            tasks.clear();
    }

    @Override
    public void clearEpicTasks() {
        // При очистке эпиков для начала удаляем сабТаски связанные с этими эпиками
        for (var task : subTasks.values()) {
            historyManager.remove(task.getId());
        }
        subTasks.clear();
        for (var task : epicTasks.values()) {
            historyManager.remove(task.getId());
        }
        epicTasks.clear();
    }

    @Override
    public void clearSubTasks() {
        // При очистке сабТасков для начала удаляем ссылки на них в эпиках
        for (EpicTask epicTask : epicTasks.values()) {
            epicTask.getSubTaskIds().clear();
            // Обновляем статусы эпиков
            epicTask.updateStatus(subTasks);
        }
        // Удаляем сами сабТаски
        for (var task : subTasks.values()) {
            historyManager.remove(task.getId());
        }
        subTasks.clear();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null)
            historyManager.add(task);
        else
            System.out.println("getTask: Задача с переданным ID не существует");
        return task;
    }

    @Override
    public EpicTask getEpicTask(int id) {
        EpicTask epicTask = epicTasks.get(id);
        if (epicTask != null)
            historyManager.add(epicTask);
        else
            System.out.println("getEpicTask: Эпик с переданным ID не существует");
        return epicTask;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask != null)
            historyManager.add(subTask);
        else
            System.out.println("getSubTask: Подзадача с переданным ID не существует");
        return subTask;
    }

    @Override
    public int addNewTask(Task task) {
        if (task != null) {
            tasks.put(idGenerator, task);
            task.setId(idGenerator);
            return idGenerator++;
        } else {
            System.out.println("addNewTask: Передана пустая ссылка на объект задачи. Добавление задачи не выполнено");
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
            System.out.println("addNewEpicTask: Передана пустая ссылка на объект задачи. Добавление задачи не выполнено");
            return -1;
        }
    }

    @Override
    public int addNewSubTask(SubTask subTask) {
        if (subTask != null) {
            EpicTask epicTask = epicTasks.get(subTask.getEpicTaskId());
            if (epicTask != null) {
                subTasks.put(idGenerator, subTask);
                subTask.setId(idGenerator);
                // Привязываем сабТаск к эпику
                epicTask.addSubTaskId(subTask.getId());
                // Обновляем статусы эпиков
                epicTask.updateStatus(subTasks);

                return idGenerator++;
            }
            else {
                System.out.println("addNewSubTask: Передан неверный эпик ID. Добавление задачи не выполнено");
                return -1;
            }
        } else {
            System.out.println("addNewSubTask: Передана пустая ссылка на объект задачи. Добавление задачи не выполнено");
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
            //Перепривязываем сабстаски к новому эпику
            List<Integer> oldEpicSubTaskIds = epicTasks.get(epicTask.getId()).getSubTaskIds();
            for (Integer subTaskId : oldEpicSubTaskIds) {
                epicTask.addSubTaskId(subTaskId);
            }
            epicTask.updateStatus(subTasks);
            epicTasks.put(epicTask.getId(), epicTask);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTask != null && subTasks.containsKey(subTask.getId())) {
            subTasks.put(subTask.getId(), subTask);
            // Обновляем статус эпика
            EpicTask epicTask = epicTasks.get(subTask.getEpicTaskId());
            epicTask.updateStatus(subTasks);
        }
    }

    @Override
    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            historyManager.remove(id);
        } else System.out.println("deleteTask: Удаление не выполнено. Запрашиваемый ID не найден.");
    }

    @Override
    public void deleteEpicTask(int id) {
        if (epicTasks.containsKey(id)) {
            // При удалении эпика удаляем всё связанные саб-таски
            EpicTask epicTask = epicTasks.get(id);
            deleteSubTasksFromEpic(epicTask);
            // Удаляем сам эпик
            epicTasks.remove(id);
            historyManager.remove(id);

        } else System.out.println("deleteEpicTask: Удаление не выполнено. Запрашиваемый ID не найден.");

    }

    @Override
    public void deleteSubTask(int id) {
        if (subTasks.containsKey(id)) {
            // При удалении саб-таска надо удалить ссылки из его эпика
            int epicTaskId = subTasks.get(id).getEpicTaskId();
            EpicTask epicTask = epicTasks.get(epicTaskId);
            epicTask.getSubTaskIds().remove((Object)id);
            // Обновляем статус эпика
            epicTask.updateStatus(subTasks);
            //Удаляем саму таску
            subTasks.remove(id);
            historyManager.remove(id);
        } else System.out.println("deleteSubTask: Удаление не выполнено. Запрашиваемый ID не найден.");
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void deleteSubTasksFromEpic(EpicTask epicTask) {
        for (Integer subTaskId : epicTask.getSubTaskIds()) {
            subTasks.remove(subTaskId);
            historyManager.remove(subTaskId);
        }
    }
}
