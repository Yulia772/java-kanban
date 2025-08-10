package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    File file;

    @Override
    protected FileBackedTaskManager createManager() {
        try {
            file = File.createTempFile("tasks", ".csv");
            file.deleteOnExit();
            return new FileBackedTaskManager(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void saveAndLoad_withTimingFields() {
        Task t = manager.addTask(new Task("T", "d", Status.NEW,
                Duration.ofMinutes(45), LocalDateTime.of(2025, 8, 10, 14, 0)));
        Epic e = manager.addEpic(new Epic("E", "ed"));
        manager.addSubtask(new Subtask("S", "sd", Status.NEW, e.getId(),
                Duration.ofMinutes(15), LocalDateTime.of(2025, 8, 10, 15, 0)));

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, loaded.getAllTasks().size());
        assertEquals(1, loaded.getAllEpics().size());
        assertEquals(1, loaded.getAllSubtasks().size());

        Task lt = loaded.getAllTasks().get(0);
        assertEquals(LocalDateTime.of(2025, 8, 10, 14, 0), lt.getStartTime());
        assertEquals(Duration.ofMinutes(45), lt.getDuration());
        assertEquals(LocalDateTime.of(2025, 8, 10, 14, 45), lt.getEndTime());
    }

    @Test
    void idCounterRestoresAfterLoad() {
        Task t1 = manager.addTask(new Task("A", "d", Status.NEW));
        Epic e = manager.addEpic(new Epic("E", "d"));
        Subtask s = manager.addSubtask(new Subtask("S", "d", Status.NEW, e.getId()));
        int lastId = s.getId();

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        Task newTask = loaded.addTask(new Task("N", "d", Status.NEW));

        assertTrue(newTask.getId() > lastId, "idCounter должен увеличиваться после загрузки");
    }

    @Test
    void loadFromNonExistingFile_throwsManagerSaveException() {
        File missing = new File("definitely-not-exists-12345.csv");
        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(missing));
    }
}

