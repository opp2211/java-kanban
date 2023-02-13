package manager;

import historymanager.HistoryManager;
import tasks.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CSVTaskFormat {
    public static String taskToString(Task task) {
        List<String> listToJoin = new ArrayList<>();
        listToJoin.add(task.getId().toString());
        listToJoin.add(task.getTaskType().toString());
        listToJoin.add(task.getName());
        listToJoin.add(task.getStatus().toString());
        listToJoin.add(task.getDescription());
        if (task.getClass() == SubTask.class) {
            Integer id = ((SubTask) task).getEpicTaskId();
            listToJoin.add(id.toString());
        }
        return String.join(",", listToJoin);
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
                task = new EpicTask(Integer.parseInt(split[0]), split[2], split[4]);
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
        return historyManager.getHistory()
                .stream()
                .map(task -> task.getId().toString())
                .collect(Collectors.joining(","));
    }
    public static List<Integer> historyFromString(String str) {
        List<Integer> list = new ArrayList<>();
        if (str != null) {
            String[] split = str.split(",");
            for (String item : split) {
                list.add(Integer.parseInt(item));
            }
        }
        return list;
    }
}
