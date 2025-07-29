package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskIds;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.subtaskIds = new ArrayList<>();
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int subtaskId) {
        if (subtaskId == this.id) {
            System.out.println("Нельзя добавить эпик как субтаск");
            return;
        }
        subtaskIds.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskId) {
        subtaskIds.remove(Integer.valueOf(subtaskId));
    }

    @Override
    public Epic copy() {
        Epic copy = new Epic(this.name, this.description);
        copy.setId(this.id);
        copy.status = this.status;
        copy.subtaskIds = new ArrayList<>(this.subtaskIds);
        return copy;
    }

    @Override
    public String toString() {
        return "Epic{id=" + id + ", name='" + name + "', status=" + status + ", subtaskIds=" + subtaskIds + "}";
    }
}
