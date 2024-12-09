package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;
import task.Status;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void shouldSavePreviosVersion() {
        Task originalTask = new Task("Task", "Description", Status.NEW);
        originalTask.setId(1);

        historyManager.add((originalTask));
        Task retrievedTask = historyManager.getHistory().get(0);

        assertEquals(originalTask.getName(), retrievedTask.getName(), "Task name should be the same.");
        assertEquals(originalTask.getDescription(), retrievedTask.getDescription(), "Task description should be the same.");
        assertEquals(originalTask.getStatus(), retrievedTask.getStatus(), "Task status should be the same.");
        assertEquals(originalTask.getId(), retrievedTask.getId(), "Task ID should be the same.");
    }

    @Test
    void shouldPreventDuplicatesAndMoveTaskToEnd() {
        Task task1 = new Task("Task1", "Description1", Status.NEW);
        Task task2 = new Task("Task2", "Description2", Status.NEW);
        Task task3 = new Task("Task3", "Description3", Status.NEW);
        Task task4 = new Task("Task4", "Description4", Status.NEW);
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);
        task3.setId(4);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);

        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();

        assertEquals(4, history.size(), "History should still contains only 3 task.");
        assertEquals(task2, history.get(history.size() - 1), "Task2 should be move to the end.");
        assertEquals(task1, history.get(0), "Task1 should remain in the first position.");
        assertEquals(task3, history.get(1), "Task3 should remain in the second position");
        assertEquals(task4, history.get(2), "Task4 should be in the second position");
    }

    @Test
    void shouldLimitHistoryToMaxTasks() {
        for (int i = 1; i <= 15; i++) {
            Task task = new Task("Task" + i, "Description" + i, Status.NEW);
            task.setId(i);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size(), "History size should not exceed the limit of 10.");

        for (int i = 0; i < 10; i++) {
            int expectedTaskId = i + 6;
            assertEquals(expectedTaskId, history.get(i).getId(), "History should only contain last 10 tasks.");
        }
    }

    @Test
    void shouldNotAddNullTask() {
        assertTrue(historyManager.getHistory().isEmpty(), "History Should be empty initially");

        historyManager.add(null);

        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "History should still be empty after adding null.");
    }
}
