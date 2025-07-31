package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManagersTest {

    @Test
    void shouldReturnInitializedTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "TaskManager instanse should not be null.");
        assertTrue(taskManager.getAllTasks().isEmpty(), "TaskManager should be initialized and ready to use.");
    }

    @Test
    void shouldReturnInitializedHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "HistoryManager instance should be null.");
        assertTrue(historyManager.getHistory().isEmpty(), "HistoryManager should be initialized and ready to use.");
    }

}