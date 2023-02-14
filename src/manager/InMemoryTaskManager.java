package manager;

import historymanager.HistoryManager;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private static final Comparator<Task> START_TIME_TASK_COMPARATOR = Comparator.comparing(Task::getStartTime,
            Comparator.nullsFirst(Comparator.naturalOrder())).thenComparing(Task::getId);

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, EpicTask> epicTasks = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(START_TIME_TASK_COMPARATOR);
    protected final HistoryManager historyManager = Managers.getDefaultHistoryManager();
    protected int idGenerator = 0;

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

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
        if (epic != null) {
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
            epicTask.updateEpicData(subTasks);
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
            if (isNotCrossOtherTasks(task)) {
                task.setId(idGenerator);
                tasks.put(idGenerator, task);
                prioritizedTasks.add(task);
                return idGenerator++;
            } else {
                System.out.println("addNewTask: Обнаружено пересечение задач. Добавление задачи не выполнено");
                return -1;
            }
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
                if (isNotCrossOtherTasks(subTask)) {
                    subTask.setId(idGenerator);
                    subTasks.put(idGenerator, subTask);
                    prioritizedTasks.add(subTask);
                    // Привязываем сабТаск к эпику
                    epicTask.addSubTaskId(subTask.getId());
                    // Обновляем статусы эпиков
                    epicTask.updateEpicData(subTasks);
                    return idGenerator++;
                } else {
                    System.out.println("addNewSubTask: Обнаружено пересечение задач. Добавление задачи не выполнено");
                    return -1;
                }
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
            if (isNotCrossOtherTasks(task)) {
                Task oldTask = tasks.get(task.getId());
                tasks.put(task.getId(), task);
                prioritizedTasks.remove(oldTask);
                prioritizedTasks.add(task);
            } else
                System.out.println("updateTask: Обнаружено пересечение задач. Изменение задачи не выполнено");
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
            epicTask.updateEpicData(subTasks);
            epicTasks.put(epicTask.getId(), epicTask);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTask != null && subTasks.containsKey(subTask.getId())) {
            if (isNotCrossOtherTasks(subTask)) {
                SubTask oldSubTask = subTasks.get(subTask.getId());
                subTasks.put(subTask.getId(), subTask);
                prioritizedTasks.remove(oldSubTask);
                prioritizedTasks.add(subTask);
                // Обновляем статус эпика
                EpicTask epicTask = epicTasks.get(subTask.getEpicTaskId());
                epicTask.updateEpicData(subTasks);

                // При смене эпика
                if (subTask.getEpicTaskId() != oldSubTask.getEpicTaskId()) {
                    EpicTask oldEpicTask = epicTasks.get(oldSubTask.getEpicTaskId());
                    oldEpicTask.updateEpicData(subTasks);
                }
            } else
                System.out.println("updateTask: Обнаружено пересечение задач. Изменение задачи не выполнено");
        }
    }

    @Override
    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            tasks.remove(id);
            prioritizedTasks.remove(task);
            historyManager.remove(id);
        } else System.out.println("deleteTask: Удаление не выполнено. Запрашиваемый ID не найден.");
    }

    @Override
    public void deleteEpicTask(int id) {
        if (epicTasks.containsKey(id)) {
            EpicTask epicTask = epicTasks.get(id);
            // При удалении эпика удаляем всё связанные саб-таски
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
            epicTask.updateEpicData(subTasks);
            //Удаляем саму таску
            SubTask subTask = subTasks.get(id);
            subTasks.remove(id);
            prioritizedTasks.remove(subTask);
            historyManager.remove(id);
        } else System.out.println("deleteSubTask: Удаление не выполнено. Запрашиваемый ID не найден.");
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void deleteSubTasksFromEpic(EpicTask epicTask) {
        for (Integer subTaskId : epicTask.getSubTaskIds()) {
            SubTask subTask = subTasks.get(subTaskId);
            subTasks.remove(subTaskId);
            prioritizedTasks.remove(subTask);
            historyManager.remove(subTaskId);
        }
    }
    private boolean isNotCrossOtherTasks(Task newTask) {
        if (newTask.getStartTime() == null || prioritizedTasks.isEmpty())
            return true;
        for (Task task : prioritizedTasks) {
            if (newTask.getStartTime().isBefore(task.getStartTime())) {
                return newTask.getEndTime().isBefore(task.getStartTime());
            } else if (newTask.getStartTime().isBefore(task.getEndTime()))
                return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryTaskManager that = (InMemoryTaskManager) o;
        return idGenerator == that.idGenerator && getTasks().equals(that.getTasks()) && getEpicTasks().equals(that.getEpicTasks()) && getSubTasks().equals(that.getSubTasks()) && historyManager.equals(that.historyManager);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTasks(), getEpicTasks(), getSubTasks(), historyManager, idGenerator);
    }
}
