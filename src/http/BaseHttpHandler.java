package http;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class BaseHttpHandler {

    protected String readBody(HttpExchange h) throws IOException {
        try (InputStream is = h.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    protected Map<String, String> queryParams(HttpExchange h) {
        URI uri = h.getRequestURI();
        String query = uri.getRawQuery();
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return map;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            String key = decode(keyValue[0]);
            String value = "";
            if (keyValue.length > 1) {
                value = decode(keyValue[1]);
            }
            map.put(key, value);
        }
        return map;
    }

    private String decode(String s) {
        return java.net.URLDecoder.decode(s, StandardCharsets.UTF_8);
    }

    protected void sendJson(HttpExchange h, int code, String json) throws IOException {
        byte[] resp;
        if (json == null) {
            resp = new byte[0];
        } else {
            resp = json.getBytes(StandardCharsets.UTF_8);
        }
        h.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        h.sendResponseHeaders(code, resp.length);
        if (resp.length > 0) {
            h.getResponseBody().write(resp);
        }
        h.close();
    }

    protected void send200(HttpExchange h, String json) throws IOException {
        if (json == null) {
            sendJson(h, 200, "");
        } else {
            sendJson(h, 200, json);
        }
    }


    protected void send201(HttpExchange h) throws IOException {
        sendJson(h, 201, "");
    }

    protected void send404(HttpExchange h, String message) throws IOException {
        sendJson(h, 404, "{\"error\":\"" + escape(message) + "\"}");
    }


    protected void send406(HttpExchange h, String message) throws IOException {
        sendJson(h, 406, "{\"error\":\"" + escape(message) + "\"}");
    }

    protected void send500(HttpExchange h, String message) throws IOException {
        sendJson(h, 500, "{\"error\":\"" + escape(message) + "\"}");
    }

    private String escape(String s) {
        if (s == null) {
            return "";
        } else {
            return s.replace("\"", "\\\"");
        }
    }
}
