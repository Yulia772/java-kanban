package http;

import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TasksApiTest extends AbstractHttpTest {

    @Test
    void addTask_returns200_and_managerContainsIt() throws Exception {
        Task t = new Task("Test", "Via API", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("2025-08-12T09:00:00"));

        HttpResponse<String> resp = POST("/tasks", gson.toJson(t));
        assertEquals(200, resp.statusCode());

        List<task.Task> list = manager.getAllTasks();
        assertEquals(1, list.size());
        assertEquals("Test", list.get(0).getName());
    }

    @Test
    void getTask_notFound_returns404() throws Exception {
        HttpResponse<String> resp = GET("/tasks?id=999");
        assertEquals(404, resp.statusCode());
    }

    @Test
    void deleteAllTasks_returns201_and_listEmpty() throws Exception {
        POST("/tasks", gson.toJson(new Task("A","", Status.NEW, null, null)));
        HttpResponse<String> del = DELETE("/tasks");
        assertEquals(201, del.statusCode());
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    void addTask_timeConflict_returns406() throws Exception {
        POST("/tasks", gson.toJson(new Task("A","", Status.NEW, Duration.ofMinutes(60),
                LocalDateTime.parse("2025-08-12T10:00:00"))));
        Task conflict = new Task("B","", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("2025-08-12T10:30:00"));
        HttpResponse<String> resp = POST("/tasks", gson.toJson(conflict));
        assertEquals(406, resp.statusCode());
    }
}
