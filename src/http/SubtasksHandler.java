package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import task.Subtask;

import java.io.IOException;
import java.util.List;
import java.util.Map;

class SubtasksHandler extends BaseHttpHandler implements com.sun.net.httpserver.HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    SubtasksHandler(TaskManager manager, Gson gson) { this.manager = manager; this.gson = gson; }

    @Override
    public void handle(HttpExchange h) throws IOException {
        try {
            String method = h.getRequestMethod();
            Map<String,String> q = queryParams(h);
            String idStr = q.get("id");
            String epicIdStr = q.get("epicId");

            switch (method) {
                case "GET" -> {
                    if (epicIdStr != null) {
                        int epicId = Integer.parseInt(epicIdStr);
                        send200(h, gson.toJson(manager.getSubtasksByEpic(epicId)));
                    } else if (idStr == null) {
                        send200(h, gson.toJson(manager.getAllSubtasks()));
                    } else {
                        int id = Integer.parseInt(idStr);
                        Subtask s = manager.getSubtask(id);
                        if (s == null) send404(h, "Subtask " + id + " not found");
                        else send200(h, gson.toJson(s));
                    }
                }
                case "POST" -> {
                    String body = readBody(h);
                    Subtask incoming = gson.fromJson(body, Subtask.class);
                    try {
                        Subtask created = manager.addSubtask(incoming);
                        if (created == null) {
                            send404(h, "Epic not found or invalid subtask reference");
                        } else {
                            send200(h, gson.toJson(created));
                        }
                    } catch (IllegalStateException ise) {
                        send406(h, ise.getMessage());
                    }
                }
                case "DELETE" -> {
                    if (idStr == null) {
                        send500(h, "DELETE /subtasks requires ?id=");
                    } else {
                        int id = Integer.parseInt(idStr);
                        if (manager.getSubtask(id) == null) send404(h, "Subtask " + id + " not found");
                        else { manager.deleteSubtask(id); send201(h); }
                    }
                }
                default -> send500(h, "Unsupported method " + method);
            }
        } catch (Exception e) {
            send500(h, e.getMessage());
        }
    }
}
