package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskIds;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Status.NEW, null, null);
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
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void recalcTiming(List<Subtask> subs) {
        if (subs.isEmpty()) {
            this.duration = null;
            this.startTime = null;
            this.endTime = null;
            return;
        }

        long totalMin = 0;
        LocalDateTime minStart = null;
        LocalDateTime maxEnd = null;

        for (Subtask s : subs) {
            if (s == null) continue;
            if (s.getDuration() != null) totalMin += s.getDuration().toMinutes();
            LocalDateTime st = s.getStartTime();
            LocalDateTime et = s.getEndTime();
            if (st != null && (minStart == null || st.isBefore(minStart))) minStart = st;
            if (et != null && (maxEnd == null || et.isAfter(maxEnd))) maxEnd = et;
        }

        if (totalMin > 0) {
            this.duration = Duration.ofMinutes(totalMin);
        } else {
            this.duration = null;
        }
        this.startTime = minStart;
        this.endTime = maxEnd;
    }

    @Override
    public Epic copy() {
        Epic copy = new Epic(this.name, this.description);
        copy.setId(this.id);
        copy.status = this.status;
        copy.subtaskIds = new ArrayList<>(this.subtaskIds);
        copy.duration = this.duration;
        copy.startTime = this.startTime;
        copy.endTime = this.endTime;
        return copy;
    }

    @Override
    public String toString() {
        return "Epic{id=" + id + ", name='" + name + "', status=" + status + ", subtaskIds=" + subtaskIds + "}";
    }
}
