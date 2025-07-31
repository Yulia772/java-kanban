package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void shouldAddTask() {
        Task task = new Task("Test task", "Descriptions", Status.NEW);
        Task added = taskManager.addTask(task);
        Task retrieved = taskManager.getTask(added.getId());

        assertNotNull(retrieved, "Task should be retrieved by ID");
        assertEquals(added.getId(), retrieved.getId());
        assertEquals("Test task", retrieved.getName());
        assertEquals("Descriptions", retrieved.getDescription());
        assertEquals(Status.NEW, retrieved.getStatus());
    }

    @Test
    void shouldAddEpic() {
        Epic epic = new Epic("Test epic", "Epic description");
        taskManager.addEpic(epic);
        List<Epic> allEpics = taskManager.getAllEpics();

        assertEquals(1, allEpics.size(), "Epic should be added to the manager");

        Epic retrieved = allEpics.get(0);

        assertEquals("Test epic", retrieved.getName());
        assertEquals("Epic description", retrieved.getDescription());
        assertEquals(Status.NEW, retrieved.getStatus());
        assertTrue(retrieved.getSubtaskIds().isEmpty(), "Subtask list should be empty initially");
    }

    @Test
    void shouldAddSubtask() {
        Epic epic = new Epic("Epic with subtasks", "Epic desc");
        taskManager.addEpic(epic);

        int epicId = taskManager.getAllEpics().get(0).getId();

        Subtask subtask = new Subtask("Subtask 1", "Sub description", Status.NEW, epicId);
        taskManager.addSubtask(subtask);

        List<Subtask> allSubtasks = taskManager.getAllSubtasks();
        assertEquals(1, allSubtasks.size(), "Subtask should be added");

        Subtask retrieved = allSubtasks.get(0);

        assertEquals("Subtask 1", retrieved.getName());
        assertEquals("Sub description", retrieved.getDescription());
        assertEquals(Status.NEW, retrieved.getStatus());
        assertEquals(epicId, retrieved.getEpicId(), "Subtask should be linked to the correct epic");

        Epic updatedEpic = taskManager.getEpic(epicId);
        assertTrue(updatedEpic.getSubtaskIds().contains(retrieved.getId()), "Epic should reference the subtask");
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

        List<Task> allTasks = taskManager.getAllTasks();

        Task retrievedManualTask = taskManager.getTask(1);
        Task retrievedGeneratedTask = null;
        for (Task task : allTasks) {
            if ("Generated Task".equals(task.getName())) {
                retrievedGeneratedTask = task;
                break;
            }
        }

        assertEquals(manualTask.getName(), retrievedManualTask.getName(), "Manual task should be retrievable by its ID.");
        assertNotNull(retrievedGeneratedTask, "Generated task should be retrievable");
        assertNotEquals(retrievedManualTask.getId(), retrievedGeneratedTask.getId(), "Manual and generated task should not conflict.");
    }

    @Test
    void shouldTrackHistory() {
        Task task = new Task("Test task", "Description", Status.NEW);
        taskManager.addTask(task);

        Task savedTask = taskManager.getAllTasks().get(0);

        taskManager.getTask(savedTask.getId());

        List<Task> history = taskManager.getHistory();

        assertEquals(1, history.size(), "History should have one task.");
        assertEquals(savedTask, history.get(0), "Task in history should match the saved task.");
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

    @Test
    void shouldClearSubtaskIdFromEpicWhenSubtaskDeleted() {
        Epic epic = new Epic("Epic", "Desc");
        taskManager.addEpic(epic);

        Epic savedEpic = taskManager.getAllEpics().get(0);

        Subtask subtask = new Subtask("Sub", "Desc", Status.NEW, savedEpic.getId());
        taskManager.addSubtask(subtask);

        int subId = subtask.getId();
        taskManager.deleteSubtask(subId);

        Epic updatedEpic = taskManager.getEpic(savedEpic.getId());
        assertNotNull(updatedEpic, "Epic should not be null after deletion");
        assertFalse(updatedEpic.getSubtaskIds().contains(subId), "Epic should not contain deleted subtask ID");
    }

    @Test
    void shouldNotAffectManagerWhenChagingOriginalTaskAfterAdding() {
        Task task = new Task("Original", "Description", Status.NEW);
        Task added = taskManager.addTask(task);

        task.setStatus(Status.DONE);

        Task fromManager = taskManager.getTask(added.getId());

        assertEquals(Status.NEW, fromManager.getStatus(), "Changing the original object should not affect the task stored in manager");
    }
}