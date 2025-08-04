package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest {
    File file;
    FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        file = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(file);
    }

    @Test
    void saveAndLoadEmptyFile() {
        manager.save();
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        assertTrue(loaded.getAllTasks().isEmpty());
        assertTrue(loaded.getAllEpics().isEmpty());
        assertTrue(loaded.getAllSubtasks().isEmpty());
    }

    @Test
    void saveAndLoadSeveralTasks() {
        Task task = new Task("Test Task", "Desc", Status.NEW);
        Epic epic = new Epic("Test Epic", "Epic Desc");
        Subtask subtask = new Subtask("Test Subtask", "Sub Desc", Status.NEW, 2);

        manager.addTask(task);
        manager.addEpic(epic);
        manager.addSubtask(subtask);
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        assertEquals(1, loaded.getAllTasks().size());
        assertEquals(1, loaded.getAllEpics().size());
        assertEquals(1, loaded.getAllSubtasks().size());
    }

    @Test
    void saveAfterDeletingTasks() {
        Task task1 = new Task("Task1", "Desc1", Status.NEW);
        Task task2 = new Task("Task2", "Desc2", Status.NEW);
        task1 = manager.addTask(task1);
        task2 = manager.addTask(task2);

        manager.deleteTask(task1.getId());

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, loaded.getAllTasks().size());
        boolean found = false;
        for (Task t : loaded.getAllTasks()) {
            if (t.getName().equals("Task2")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    void saveAfterDeletingEpicsAndSubtasks() {

        Epic epic = new Epic("Epic1", "DescEpic");
        epic = manager.addEpic(epic);

        Subtask subtask = new Subtask("Sub1", "DescSub", Status.NEW, epic.getId());
        manager.addSubtask(subtask);

        manager.deleteEpic(epic.getId());

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        assertTrue(loaded.getAllEpics().isEmpty(), "Эпики должны быть удалены");
        assertTrue(loaded.getAllSubtasks().isEmpty(), "Сабтаски должны быть удалены");
    }

    @Test
    void idCounterRestoresCorrectlyAfterLoad() {
        Task task1 = manager.addTask(new Task("Task1", "Desc1", Status.NEW));
        Epic epic = manager.addEpic(new Epic("Epic1", "DescEpic"));
        Subtask sub1 = manager.addSubtask(new Subtask("Sud1", "DescSub", Status.NEW, epic.getId()));

        int lastUsedId = sub1.getId();

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        Task newTask = loaded.addTask(new Task("TaskNew", "DescNew", Status.NEW));

        assertTrue(newTask.getId() > lastUsedId, "idCounter должен увеличиваться после загрузки");
    }
}


