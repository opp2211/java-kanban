package tasks;

public class SubTask extends Task {
    protected int epicTaskId;

    public SubTask(Integer id, String name, String description, String status, int epicTaskId) {
        super(id, name, description, status);
        this.epicTaskId = epicTaskId;
    }

    public SubTask(String name, String description, String status, int epicTaskId) {
        super(name, description, status);
        this.epicTaskId = epicTaskId;
    }

    public int getEpicTaskId() {
        return epicTaskId;
    }

    public void setEpicTaskId(int epicTaskId) {
        this.epicTaskId = epicTaskId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicTaskId=" + epicTaskId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
