package task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {

    @Test
    void shouldNotAddingItselfAsSubtask() {
        Epic epic = new Epic("Epic", "Description");
        epic.setId(1);

        epic.addSubtaskId(epic.getId());
        assertFalse(epic.getSubtaskIds().contains(epic.getId()), "Epic cannot have itself as a subtask.");
    }

    @Test
    void shouildBeEqualIfIdIsSame() {
        Epic epic1 = new Epic("Epic1", "Description");
        Epic epic2 = new Epic("Epic2", "Another description");
        epic1.setId(1);
        epic2.setId(1);

        assertEquals(epic1, epic2, "Epics with the same id should be equal.");
    }
}
