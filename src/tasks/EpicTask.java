package tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EpicTask extends Task {
    protected ArrayList<Integer> subTaskIds;

    public EpicTask(Integer id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
        subTaskIds = new ArrayList<>();
    }

    public EpicTask(String name, String description) {
        super(name, description, TaskStatus.NEW);
        subTaskIds = new ArrayList<>();
    }

    public void updateStatus(Map<Integer, SubTask> subTasks) {

        if (getSubTaskIds().isEmpty()) {
            status = TaskStatus.NEW;
            return;
        }

        Set<TaskStatus> subTaskStatuses = new HashSet<>();
        for (Integer subTaskId : getSubTaskIds()) {
            TaskStatus subTaskStatus = subTasks.get(subTaskId).getStatus();
            subTaskStatuses.add(subTaskStatus);
        }

        if (subTaskStatuses.contains(TaskStatus.NEW)
                && !subTaskStatuses.contains(TaskStatus.IN_PROGRESS) && !subTaskStatuses.contains(TaskStatus.DONE)) {
            status = TaskStatus.NEW;

        } else if (subTaskStatuses.contains(TaskStatus.DONE)
                && !subTaskStatuses.contains(TaskStatus.IN_PROGRESS) && !subTaskStatuses.contains(TaskStatus.NEW)) {
            status = TaskStatus.DONE;
        } else {
            status = TaskStatus.IN_PROGRESS;
        }
    }

    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void addSubTaskId(int id) {
        subTaskIds.add(id);
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "subTaskIds=" + subTaskIds +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
