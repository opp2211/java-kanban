package tasks;

import java.time.LocalDateTime;
import java.util.*;

public class EpicTask extends Task {
    protected ArrayList<Integer> subTaskIds;
    private LocalDateTime endTime;

    public EpicTask(Integer id, String name, String description) {
        super(id, name, description, TaskStatus.NEW, 0L, null);
        taskType = TaskType.EPIC_TASK;
        subTaskIds = new ArrayList<>();
    }

    public EpicTask(String name, String description) {
        super(name, description, TaskStatus.NEW, 0L, null);
        taskType = TaskType.EPIC_TASK;
        subTaskIds = new ArrayList<>();
    }

    public void updateEpicData(Map<Integer, SubTask> subTasks) {
        //Обновляем статус
        if (getSubTaskIds().isEmpty()) {
            status = TaskStatus.NEW;
        } else {
            Set<TaskStatus> subTaskStatuses = new HashSet<>();
            for (int subTaskId : subTaskIds) {
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

        //Обновляем время
        startTime = subTaskIds.stream()
                .map(id -> subTasks.get(id).getStartTime())
                .filter(Objects::nonNull)
                .min(Comparator.comparingInt(LocalDateTime::getMinute)).orElse(null);
        endTime = subTaskIds.stream()
                .map(id -> subTasks.get(id).getEndTime())
                .filter(Objects::nonNull)
                .max(Comparator.comparingInt(LocalDateTime::getMinute)).orElse(null);
        duration = subTaskIds.stream()
                .map(id -> subTasks.get(id).getDuration())
                .mapToLong(Long::longValue).sum();
    }

    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void addSubTaskId(int id) {
        subTaskIds.add(id);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "subTaskIds=" + subTaskIds +
                ", id=" + id +
                ", taskType=" + taskType +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }
}
