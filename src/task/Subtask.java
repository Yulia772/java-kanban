package task;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public Subtask copy() {
        Subtask copy = new Subtask(this.name, this.description, this.status, this.epicId);
        copy.setId(this.id);
        return copy;
    }

    @Override
    public String toString() {
        return "Subtask{id=" + id + ", name='" + name + "', status=" + status + ", epicId=" + epicId + "}";
    }
}
