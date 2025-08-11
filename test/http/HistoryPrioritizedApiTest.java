package http;

import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HistoryPrioritizedApiTest extends AbstractHttpTest {

    @Test
    void history_returns200() throws Exception {
        // чуть активности, чтобы не было пусто
        Task t = manager.addTask(new Task("T","", Status.NEW, Duration.ofMinutes(5),
                LocalDateTime.parse("2025-08-12T07:00:00")));
        GET("/tasks?id=" + t.getId());

        HttpResponse<String> hist = GET("/history");
        assertEquals(200, hist.statusCode());
        assertTrue(hist.body().startsWith("["));
    }

    @Test
    void prioritized_returns200_andJsonArray() throws Exception {
        HttpResponse<String> pr = GET("/prioritized");
        assertEquals(200, pr.statusCode());
        assertTrue(pr.body().startsWith("["));
    }
}
