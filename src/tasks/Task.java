package tasks;

import java.util.Objects;

public class Task {
    protected Integer id;
    protected TaskType taskType;
    protected String name;
    protected String description;
    protected TaskStatus status;

    public Task(Integer id, String name, String description, TaskStatus status) {
        this.id = id;
        this.taskType = TaskType.TASK;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description, TaskStatus status) {
        this.taskType = TaskType.TASK;
        this.name = name;
        this.description = description;
        this.status = status;
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

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(getId(), task.getId()) && getTaskType() == task.getTaskType() && Objects.equals(getName(), task.getName()) && Objects.equals(getDescription(), task.getDescription()) && getStatus() == task.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTaskType(), getName(), getDescription(), getStatus());
    }
}
