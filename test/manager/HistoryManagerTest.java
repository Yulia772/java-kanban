package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;

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
        task4.setId(4);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);

        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();

        assertEquals(4, history.size(), "History should still contain 4 task.");
        assertEquals(task1, history.get(0), "Task1 should remain at the beginning.");
        assertEquals(task3, history.get(1), "Task3 should be second.");
        assertEquals(task4, history.get(2), "Task4 should be third");
        assertEquals(task2, history.get(3), "Task2 should be moved to the end");
    }

    @Test
    void shouldNotAddNullTask() {
        assertTrue(historyManager.getHistory().isEmpty(), "History Should be empty initially");

        historyManager.add(null);

        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "History should still be empty after adding null.");
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        Task task1 = new Task("Task1", "Desc", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Task2", "Desc", Status.NEW);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Only one task shouid remain");
        assertEquals(task2, history.get(0), "Remaining task shouid be Task2");
    }

    @Test
    void shouldKeepOrderAfterMultipleAddAndRemoves() {
        Task task1 = new Task("Task1", "Desc", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Task2", "Desc", Status.NEW);
        task2.setId(2);
        Task task3 = new Task("Task3", "Desc", Status.NEW);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(2);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(List.of(task1, task3, task2), history, "Task2 should be last after re-adding");
    }
}
