package http;

import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubtasksApiTest extends AbstractHttpTest {

    @Test
    void postSubtask_ok_and_getByEpic() throws Exception {
        int epicId = manager.addEpic(new Epic("E","D")).getId();

        Subtask s = new Subtask("S","desc", Status.NEW, epicId,
                Duration.ofMinutes(90), LocalDateTime.parse("2025-08-12T11:00:00"));
        HttpResponse<String> resp = POST("/subtasks", gson.toJson(s));
        assertEquals(200, resp.statusCode());

        HttpResponse<String> listByEpic = GET("/subtasks?epicId=" + epicId);
        assertEquals(200, listByEpic.statusCode());
        assertTrue(listByEpic.body().contains("\"epicId\":" + epicId));
    }

    @Test
    void postSubtask_whenEpicMissing_returns404() throws Exception {
        Subtask s = new Subtask("S","", Status.NEW, 999, null, null);
        HttpResponse<String> resp = POST("/subtasks", gson.toJson(s));
        assertEquals(404, resp.statusCode());
    }

    @Test
    void deleteSubtask_returns201_and_then404() throws Exception {
        int epicId = manager.addEpic(new Epic("E","D")).getId();
        int subId = manager.addSubtask(new Subtask("S","", Status.NEW, epicId, null, null)).getId();

        HttpResponse<String> del = DELETE("/subtasks?id=" + subId);
        assertEquals(201, del.statusCode());

        HttpResponse<String> get = GET("/subtasks?id=" + subId);
        assertEquals(404, get.statusCode());
    }
}
