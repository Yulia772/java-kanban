package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.util.List;
import java.util.Map;

class TasksHandler extends BaseHttpHandler implements com.sun.net.httpserver.HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    TasksHandler(TaskManager manager, Gson gson) { this.manager = manager; this.gson = gson; }

    @Override
    public void handle(HttpExchange h) throws IOException {
        try {
            String method = h.getRequestMethod();
            Map<String,String> q = queryParams(h);
            String idStr = q.get("id");

            switch (method) {
                case "GET" -> {
                    if (idStr == null) {
                        List<Task> all = manager.getAllTasks();
                        send200(h, gson.toJson(all));
                    } else {
                        int id = Integer.parseInt(idStr);
                        Task t = manager.getTask(id);
                        if (t == null) send404(h, "Task " + id + " not found");
                        else send200(h, gson.toJson(t));
                    }
                }
                case "POST" -> {
                    String body = readBody(h);
                    Task incoming = gson.fromJson(body, Task.class);
                    try {
                        Task created = manager.addTask(incoming);
                        send200(h, gson.toJson(created));
                    } catch (IllegalStateException ise) {
                        send406(h, ise.getMessage());
                    }
                }
                case "DELETE" -> {
                    if (idStr == null) {
                        manager.deleteAllTasks();
                        send201(h);
                    } else {
                        int id = Integer.parseInt(idStr);
                        if (manager.getTask(id) == null) send404(h, "Task " + id + " not found");
                        else { manager.deleteTask(id); send201(h); }
                    }
                }
                default -> send500(h, "Unsupported method " + method);
            }
        } catch (Exception e) {
            send500(h, e.getMessage());
        }
    }
}
