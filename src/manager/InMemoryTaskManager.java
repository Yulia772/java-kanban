package manager;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final NavigableSet<Task> prioritized = new TreeSet<>((t1, t2) -> {
        LocalDateTime s1 = t1.getStartTime();
        LocalDateTime s2 = t2.getStartTime();

        if (s1 == null && s2 == null) {
            return Integer.compare(t1.getId(), t2.getId());
        }
        if (s1 == null) return 1;
        if (s2 == null) return -1;

        int cmp = s1.compareTo(s2);
        if (cmp != 0) return cmp;

        return Integer.compare(t1.getId(), t2.getId());
    });
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected HistoryManager historyManager;
    protected int idCounter = 1;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    private int generateId() {
        return idCounter++;
    }

    @Override
    public Task addTask(Task task) {
        int id = generateId();
        Task copy = task.copy();
        copy.setId(id);

        if (intersectsNeighbors(copy)) {
            throw new IllegalStateException("Временной конфликт: задача пересекается с другой");
        }

        tasks.put(id, copy);
        addToPrioritized(copy);
        return copy;
    }

    @Override
    public Epic addEpic(Epic epic) {
        int id = generateId();
        Epic copy = epic.copy();
        copy.setId(id);
        epics.put(id, copy);
        return copy;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {

        int id = generateId();
        Subtask copy = subtask.copy();
        copy.setId(id);

        if (copy.getEpicId() == copy.getId()) {
            System.out.println("Subtask cannot be its own epic.");
            return null;
        }

        if (!epics.containsKey(copy.getEpicId())) {
            System.out.println("Subtask refers to non-existent epic");
            return null;
        }

        if (intersectsNeighbors(copy)) {
            throw new IllegalStateException("Временной конфликт: подзадача пересекается с другой задачей");
        }

        subtasks.put(id, copy);

        Epic epic = epics.get((copy.getEpicId()));
        epic.addSubtaskId(copy.getId());
        updateEpicStatus(epic);
        updateEpicTiming(epic);
        addToPrioritized(copy);

        historyManager.add(copy);
        return copy;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public void deleteTask(int id) {
        Task t = tasks.remove(id);
        removeFromPrioritized(t);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                Subtask removed = subtasks.remove(subtaskId);
                removeFromPrioritized(removed);
                historyManager.remove(subtaskId);
            }
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            removeFromPrioritized(subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic);
                updateEpicTiming(epic);
            }
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
        prioritized.clear();
        historyManager.clearHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritized);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getSubtasksByEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return new ArrayList<>();
        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    protected void addToPrioritized(Task task) {
        if (task != null && task.getStartTime() != null) prioritized.add(task);
    }

    protected void removeFromPrioritized(Task task) {
        if (task != null && task.getStartTime() != null) prioritized.remove(task);
    }

    protected void updateEpicStatus(Epic epic) {
        List<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        boolean allDone = true;
        boolean allNew = true;

        for (int id : subtaskIds) {
            Subtask subtask = subtasks.get(id);
            if (subtask != null) {
                if (subtask.getStatus() != Status.DONE) {
                    allDone = false;
                }
                if (subtask.getStatus() != Status.NEW) {
                    allNew = false;
                }
            }
        }

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    protected void updateEpicTiming(Epic epic) {
        List<Subtask> subs = epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        epic.recalcTiming(subs);
    }

    private boolean intersects(Task a, Task b) {
        LocalDateTime aS = a.getStartTime();
        LocalDateTime aE = a.getEndTime();
        LocalDateTime bS = b.getStartTime();
        LocalDateTime bE = b.getEndTime();
        if (aS == null || aE == null || bS == null || bE == null) return false;
        return aS.isBefore(bE) && bS.isBefore(aE);
    }

    private boolean intersectsNeighbors(Task cand) {
        if (cand.getStartTime() == null) return false;
        Task lower = prioritized.lower(cand);
        Task higher = prioritized.higher(cand);
        return (lower != null && intersects(cand, lower)) || (higher != null && intersects(cand, higher));
    }
}

