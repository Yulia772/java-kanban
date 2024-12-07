package task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {

    @Test
    void shouldLinkToCorrectEpic() {
        Epic epic = new Epic("Epic", "Description");
        epic.setId(1);
        Subtask subtask = new Subtask("Subtask", "Description", Status.NEW, epic.getId());

        assertEquals(epic.getId(), subtask.getEpicId(), "Subtask should belong to the correct epic.");
    }

    @Test
    void shouildBeEqualIfIdIsSame() {
        Subtask subtask1 = new Subtask("Subtask1", "Description", Status.NEW, 1);
        Subtask subtask2 = new Subtask("Subtask2", "Another description", Status.IN_PROGRESS, 1);
        subtask1.setId(1);
        subtask2.setId(1);

        assertEquals(subtask1, subtask2, "Epics with the same id should be equal.");
    }
}
