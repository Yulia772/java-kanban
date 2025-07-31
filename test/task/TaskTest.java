package task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {

    @Test
    void shouldBeEqualIfIdIsSame() {
        Task task1 = new Task("Task1", "Description", Status.NEW);
        Task task2 = new Task("Task2", "Another description", Status.NEW);
        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2, "Tasks with the same id should be equal.");
    }

}