package http;

import org.junit.jupiter.api.Test;
import task.Epic;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class EpicsApiTest extends AbstractHttpTest {

    @Test
    void postEpic_returns200_and_stored() throws Exception {
        Epic e = new Epic("Переезд","Собрать вещи");
        HttpResponse<String> resp = POST("/epics", gson.toJson(e));
        assertEquals(200, resp.statusCode());

        assertEquals(1, manager.getAllEpics().size());
        assertEquals("Переезд", manager.getAllEpics().get(0).getName());
    }

    @Test
    void getEpic_notFound_404() throws Exception {
        HttpResponse<String> r = GET("/epics?id=777");
        assertEquals(404, r.statusCode());
    }

    @Test
    void deleteEpic_byId_returns201_then_404_on_get() throws Exception {
        HttpResponse<String> created = POST("/epics", gson.toJson(new Epic("E","D")));
        assertEquals(200, created.statusCode());
        int id = manager.getAllEpics().get(0).getId();

        HttpResponse<String> del = DELETE("/epics?id=" + id);
        assertEquals(201, del.statusCode());

        HttpResponse<String> get = GET("/epics?id=" + id);
        assertEquals(404, get.statusCode());
    }
}
