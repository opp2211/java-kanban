package manager;

import historymanager.HistoryManager;
import tasks.*;

import java.util.ArrayList;
import java.util.List;

public class CSVTaskFormat {
    public static String taskToString(Task task) {
        StringBuilder result = new StringBuilder();
        result.append(task.getId());
        result.append(",");
        result.append(task.getTaskType());
        result.append(",");
        result.append(task.getName());
        result.append(",");
        result.append(task.getStatus());
        result.append(",");
        result.append(task.getDescription());
        if (task.getClass() == SubTask.class) {
            result.append(",");
            result.append(((SubTask)task).getEpicTaskId());
        }
        return result.toString();
    }
    public static Task taskFromString(String str) {
        Task task;

        /* id,type,name,status,description,epic */
        String[] split = str.split(",");

        TaskType taskType = TaskType.valueOf(split[1]);
        switch (taskType) {
            case TASK:
                task = new Task(Integer.parseInt(split[0]), split[2], split[4], TaskStatus.valueOf(split[3]));
                break;
            case EPIC_TASK:
                task = new EpicTask(Integer.parseInt(split[0]), split[2], split[4], TaskStatus.valueOf(split[3]));
                break;
            case SUB_TASK:
                task = new SubTask(Integer.parseInt(split[0]), split[2], split[4], TaskStatus.valueOf(split[3]),
                        Integer.parseInt(split[5]));
                break;
            default:
                task = null;
        }
        return task;
    }
    public static String historyToString(HistoryManager historyManager) {
        StringBuilder result = new StringBuilder();
        for (Task task : historyManager.getHistory()) {
            if (result.length() != 0)
                result.append(",");
            result.append(task.getId());
        }
        return result.toString();
    }
    public static List<Integer> historyFromString(String str) {
        List<Integer> list = new ArrayList<>();

        String[] split = str.split(",");
        for (String item : split) {
            list.add(Integer.parseInt(item));
        }
        return list;
    }
}
