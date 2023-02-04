package tasks;

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
}
