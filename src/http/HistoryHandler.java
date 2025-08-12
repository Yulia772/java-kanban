package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;

class HistoryHandler extends BaseHttpHandler implements com.sun.net.httpserver.HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    HistoryHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        try {
            if (!"GET".equals(h.getRequestMethod())) {
                send500(h, "Only GET is supported");
                return;
            }
            send200(h, gson.toJson(manager.getHistory()));
        } catch (Exception e) {
            send500(h, e.getMessage());
        }
    }
}
