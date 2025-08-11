package http;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

abstract class AbstractHttpTest {
    protected TaskManager manager;
    protected HttpTaskServer server;
    protected HttpClient client;
    protected Gson gson;

    @BeforeEach
    void setUp() throws Exception {
        manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager);
        gson = HttpTaskServer.createGson();
        server.start();
        client = HttpClient.newHttpClient();

        manager.deleteAllTasks();
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    protected HttpResponse<String> GET(String path) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://localhost:8080" + path)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
    }
    protected HttpResponse<String> POST(String path, String json) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://localhost:8080" + path))
                        .header("Content-Type","application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json)).build(),
                HttpResponse.BodyHandlers.ofString());
    }
    protected HttpResponse<String> DELETE(String path) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://localhost:8080" + path)).DELETE().build(),
                HttpResponse.BodyHandlers.ofString());
    }
}
