package practicum.tasks;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected Integer id;
    protected TaskType taskType;
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected long duration;
    protected LocalDateTime startTime;

    public Task(Integer id, String name, String description, TaskStatus status, long duration
            , LocalDateTime startTime) {
        this.id = id;
        this.taskType = TaskType.TASK;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, String description, TaskStatus status, long duration
            , LocalDateTime startTime) {
        this.taskType = TaskType.TASK;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public TaskType getTaskType() {
        return taskType;
    }
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Long getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null)
            return null;
        return startTime.plusMinutes(duration);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", taskType=" + taskType +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return getDuration() == task.getDuration() && Objects.equals(getId(),
                task.getId()) && getTaskType() == task.getTaskType() && Objects.equals(getName(),
                task.getName()) && Objects.equals(getDescription(),
                task.getDescription()) && getStatus() == task.getStatus() && Objects.equals(getStartTime(),
                task.getStartTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTaskType(), getName(),
                getDescription(), getStatus(), getDuration(), getStartTime());
    }
}
