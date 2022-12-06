package tasks;

import java.util.ArrayList;
import java.util.Objects;

public class EpicTask extends Task {
    protected ArrayList<Integer> subTaskIds;

    public EpicTask(Integer id, String name, String description, String status) {
        super(id, name, description, status);
        subTaskIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void addSubTaskId (int id) {
        subTaskIds.add(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EpicTask epicTask = (EpicTask) o;
        return Objects.equals(subTaskIds, epicTask.subTaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTaskIds);
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
