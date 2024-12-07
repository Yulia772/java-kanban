package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void shouldAddTask() {
        Task task = new Task("Test task", "Descriptions", Status.NEW);
        taskManager.addTask(task);

        Task retrievedTask = taskManager.getTask(task.getId());
        assertEquals(task, retrievedTask, "Task should be added and retrievable.");
    }

    @Test
    void shouldAddEpic() {
        Epic epic = new Epic("Test epic", "Epic descriptions");
        taskManager.addEpic(epic);

        Task retrievedEpic = taskManager.getEpic(epic.getId());
        assertEquals(epic, retrievedEpic, "Epic should be added and retrievable.");
    }

    @Test
    void shouldAddSubtask() {
        Epic epic = new Epic("Test epic", "Epic descriptions");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Test subtask", "Subtask description", Status.NEW, epic.getId());
        taskManager.addSubtask(subtask);

        Subtask retrievedSubtask = taskManager.getSubtask(subtask.getId());
        assertEquals(subtask, retrievedSubtask, "Subtask should be added and retrievable.");
    }


    @Test
    void shouldNotAllowSubtaskToBeItsOwnEpic() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Description", Status.NEW, epic.getId());
        subtask.setId(epic.getId());

        Subtask result = taskManager.addSubtask(subtask);

        assertNull(result, "Subtask cannot be its own epic.");
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "No invalid subtask added.");
    }

    @Test
    void shouldNotConflictBetweenGeneratedAndSetId() {
        Task manualTask = new Task("Manual Task", "Task with manual id", Status.NEW);
        manualTask.setId(1);
        taskManager.addTask(manualTask);

        Task generatedTask = new Task("Generated Task", "Task with generated id", Status.NEW);
        taskManager.addTask(generatedTask);

        Task retrievedManualTask = taskManager.getTask(1);
        Task retrievedGeneratedTask = taskManager.getTask(generatedTask.getId());

        assertEquals(manualTask, retrievedManualTask, "Manual task should be retrievable by its ID.");
        assertEquals(generatedTask, retrievedGeneratedTask, "Generated task should be retrievable by its ID");
        assertNotEquals(manualTask, generatedTask, "Manual and generated task should not conflict.");
    }

    @Test
    void shouldTrackHistory() {
        Task task = new Task("Test task", "Description", Status.NEW);
        taskManager.addTask(task);

        taskManager.getTask(task.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size(), "History should have one task.");
        assertEquals(task, history.get(0), "Task in history should match the retrieved task.");
    }

    @Test
    void shouldNotChangeTaskFieldsAfterAdding() {
        Task task = new Task("Test task", "Description", Status.NEW);
        task.setId(1);

        taskManager.addTask(task);

        Task retrievedTask = taskManager.getTask(task.getId());

        assertEquals(task.getName(), retrievedTask.getName(), "Task name should not change.");
        assertEquals(task.getDescription(), retrievedTask.getDescription(), "Task description dhould not change.");
        assertEquals(task.getStatus(), retrievedTask.getStatus(), "Task status should not change.");
        assertEquals(task.getId(), retrievedTask.getId(), "Task ID should not change.");
    }
}