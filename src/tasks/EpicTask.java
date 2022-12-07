package tasks;

import java.util.ArrayList;

public class EpicTask extends Task {
    protected ArrayList<Integer> subTaskIds;

    public EpicTask(Integer id, String name, String description, String status) {
        super(id, name, description, status);
        subTaskIds = new ArrayList<>();
    }

    public EpicTask(String name, String description) {
        super(name, description, "NEW");
        subTaskIds = new ArrayList<>();
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
