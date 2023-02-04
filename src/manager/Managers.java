package manager;

import historymanager.HistoryManager;
import historymanager.InMemoryHistoryManager;

import java.io.File;

public class Managers {

    public static TaskManager getDefault() {
        return FileBackedTaskManager.loadFromFile(new File("taskManagerData.csv"));
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }
}
