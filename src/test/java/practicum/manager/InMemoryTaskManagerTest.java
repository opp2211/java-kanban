package practicum.manager;

import org.junit.jupiter.api.BeforeEach;
import practicum.manager.memory.InMemoryTaskManager;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }
}
