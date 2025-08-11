package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;

    protected abstract T createManager();

    @BeforeEach
    void setUp() {
        manager = createManager();
    }

    // ------- helpers -------
    protected Task task(String name) {
        return new Task(name, "desc", Status.NEW);
    }

    protected Task taskWithTime(String name, int minutes, LocalDateTime start) {
        return new Task(name, "desc", Status.NEW, Duration.ofMinutes(minutes), start);
    }

    protected Epic epic(String name) {
        return new Epic(name, "epic-desc");
    }

    protected Subtask subNoTime(String name, int epicId) {
        return new Subtask(name, "sub-desc", Status.NEW, epicId);
    }

    protected Subtask subWithTime(String name, int epicId, int minutes, LocalDateTime start) {
        return new Subtask(name, "sub-desc", Status.NEW, epicId, Duration.ofMinutes(minutes), start);
    }

    @Test
    void addAndGetTask() {
        Task added = manager.addTask(task("Task1")); // используем возвращённую копию
        Task got = manager.getTask(added.getId());
        assertNotNull(got);
        assertEquals(added.getId(), got.getId());
        assertEquals("Task1", got.getName());
    }

    @Test
    void shouldAddAndGetEpic() {
        Epic epic = manager.addEpic(epic("Epic1")); // берём копию от менеджера
        Epic retrieved = manager.getEpic(epic.getId());
        assertNotNull(retrieved);
        assertEquals(epic.getName(), retrieved.getName());
    }

    @Test
    void shouldAddSubtaskLinkedToEpic() {
        Epic epic = manager.addEpic(epic("Epic1")); // получаем id из возвращённого объекта
        Subtask subtask = manager.addSubtask(subNoTime("Sub1", epic.getId()));

        assertTrue(manager.getEpic(epic.getId()).getSubtaskIds().contains(subtask.getId()));
        assertEquals(epic.getId(), manager.getSubtask(subtask.getId()).getEpicId());
    }

    @Test
    void epicStatusAllNew() {
        Epic epic = manager.addEpic(epic("Epic1"));
        manager.addSubtask(new Subtask("S1", "D", Status.NEW, epic.getId()));
        manager.addSubtask(new Subtask("S2", "D", Status.NEW, epic.getId()));
        assertEquals(Status.NEW, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void epicStatusAllDone() {
        Epic epic = manager.addEpic(epic("Epic1"));
        manager.addSubtask(new Subtask("S1", "D", Status.DONE, epic.getId()));
        manager.addSubtask(new Subtask("S2", "D", Status.DONE, epic.getId()));
        assertEquals(Status.DONE, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void epicStatusMixedNewAndDone() {
        Epic epic = manager.addEpic(epic("Epic1"));
        manager.addSubtask(new Subtask("S1", "D", Status.NEW, epic.getId()));
        manager.addSubtask(new Subtask("S2", "D", Status.DONE, epic.getId()));
        assertEquals(Status.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void epicStatusAllInProgress() {
        Epic epic = manager.addEpic(epic("Epic1"));
        manager.addSubtask(new Subtask("S1", "D", Status.IN_PROGRESS, epic.getId()));
        manager.addSubtask(new Subtask("S2", "D", Status.IN_PROGRESS, epic.getId()));
        assertEquals(Status.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void prioritizedTasks_orderByStartTime_ignoreNulls() {
        // задача без времени не должна попасть в приоритетный список
        manager.addTask(task("NoTime"));

        Task a = manager.addTask(taskWithTime("A", 30, LocalDateTime.of(2025, 8, 10, 10, 0)));
        Task b = manager.addTask(taskWithTime("B", 15, LocalDateTime.of(2025, 8, 10, 9, 0)));
        Task c = manager.addTask(taskWithTime("C", 20, LocalDateTime.of(2025, 8, 10, 12, 0)));

        List<Task> p = manager.getPrioritizedTasks();
        assertEquals(List.of(b, a, c), p);
    }

    @Test
    void shouldPreventTimeIntersection() {
        manager.addTask(taskWithTime("T1", 30, LocalDateTime.of(2025, 8, 10, 10, 0)));
        Task overlapping = taskWithTime("T2", 30, LocalDateTime.of(2025, 8, 10, 10, 15));
        assertThrows(IllegalStateException.class, () -> manager.addTask(overlapping),
                "Должны запретить пересечение задач по времени");
    }
}