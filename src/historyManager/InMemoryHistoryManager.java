package historyManager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_HISTORY_SIZE = 10;
    private final List<Task> viewsHistory;

    public InMemoryHistoryManager() {
        viewsHistory = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (viewsHistory.size() == MAX_HISTORY_SIZE) {
            viewsHistory.remove(0);
        }
        viewsHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return viewsHistory;
    }
}
