package tasks;

import java.time.LocalDateTime;

public class SubTask extends Task {
    protected int epicTaskId;

    public SubTask(Integer id, String name, String description, TaskStatus status, int epicTaskId
            , long duration, LocalDateTime startTime) {
        super(id, name, description, status, duration, startTime);
        taskType = TaskType.SUB_TASK;
        this.epicTaskId = epicTaskId;
    }

    public SubTask(String name, String description, TaskStatus status, int epicTaskId
            , long duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        taskType = TaskType.SUB_TASK;
        this.epicTaskId = epicTaskId;
    }

    public int getEpicTaskId() {
        return epicTaskId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicTaskId=" + epicTaskId +
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
