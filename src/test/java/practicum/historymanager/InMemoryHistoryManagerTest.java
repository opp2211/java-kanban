package practicum.historymanager;

import org.junit.jupiter.api.BeforeEach;
public class InMemoryHistoryManagerTest extends HistoryManagerTest<InMemoryHistoryManager>{

    @BeforeEach
    public void setUp(){
        historyManager = new InMemoryHistoryManager();
    }

}
