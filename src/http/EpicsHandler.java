package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import task.Epic;

import java.io.IOException;
import java.util.List;
import java.util.Map;

class EpicsHandler extends BaseHttpHandler implements com.sun.net.httpserver.HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    EpicsHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        try {
            String method = h.getRequestMethod();
            Map<String, String> q = queryParams(h);
            String idStr = q.get("id");

            switch (method) {
                case "GET" -> {
                    if (idStr == null) {
                        List<Epic> all = manager.getAllEpics();
                        send200(h, gson.toJson(all));
                    } else {
                        int id = Integer.parseInt(idStr);
                        Epic e = manager.getEpic(id);
                        if (e == null) send404(h, "Epic " + id + " not found");
                        else send200(h, gson.toJson(e));
                    }
                }
                case "POST" -> {
                    String body = readBody(h);
                    Epic incoming = gson.fromJson(body, Epic.class);
                    Epic created = manager.addEpic(incoming);
                    send200(h, gson.toJson(created));
                }
                case "DELETE" -> {
                    if (idStr == null) {
                        send500(h, "DELETE /epics requires ?id=");
                    } else {
                        int id = Integer.parseInt(idStr);
                        if (manager.getEpic(id) == null) send404(h, "Epic " + id + " not found");
                        else {
                            manager.deleteEpic(id);
                            send201(h);
                        }
                    }
                }
                default -> send500(h, "Unsupported method " + method);
            }
        } catch (Exception e) {
            send500(h, e.getMessage());
        }
    }
}
