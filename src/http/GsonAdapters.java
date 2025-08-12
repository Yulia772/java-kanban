package http;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;

public final class GsonAdapters {
    public static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        @Override
        public JsonElement serialize(LocalDateTime src, Type t, JsonSerializationContext c) {
            if (src == null) {
                return JsonNull.INSTANCE;
            } else {
                return new JsonPrimitive(src.toString());
            }
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type t, JsonDeserializationContext c) {
            if (json == null || json.isJsonNull()) {
                return null;
            } else {
                return LocalDateTime.parse(json.getAsString());
            }
        }
    }

    public static class DurationAdapter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {
        @Override
        public JsonElement serialize(Duration src, Type t, JsonSerializationContext c) {
            if (src == null) {
                return JsonNull.INSTANCE;
            } else {
                return new JsonPrimitive(src.toMinutes());
            }
        }

        @Override
        public Duration deserialize(JsonElement json, Type t, JsonDeserializationContext c) {
            if (json == null || json.isJsonNull()) return null;
            return Duration.ofMinutes(json.getAsLong());
        }
    }
}
