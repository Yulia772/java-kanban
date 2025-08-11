package task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, Status status, int epicId) {
        this(name, description, status, epicId, null, null);
    }

    public Subtask(String name, String description, Status status, int epicId, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public Subtask copy() {
        Subtask copy = new Subtask(this.name, this.description, this.status, this.epicId, this.duration, this.startTime);
        copy.setId(this.id);
        return copy;
    }

    @Override
    public String toString() {
        return "Subtask{id=" + id + ", name='" + name + "', status=" + status + ", epicId=" + epicId + "}";
    }
}
