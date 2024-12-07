package manager;

import task.*;
import java.util.List;

public interface TaskManager {
    Task addTask(Task task);
    Epic addEpic(Epic epic);
    Subtask addSubtask(Subtask subtask);

    Task getTask(int id);
    Epic getEpic(int id);
    Subtask getSubtask(int id);

    void deleteTask(int id);
    void deleteEpic(int id);
    void deleteSubtask(int id);
    void deleteAllTasks();

    List<Task> getAllTasks();
    List<Epic> getAllEpics();
    List<Subtask> getAllSubtasks();
    List<Subtask> getSubtasksByEpic(int epicId);

    List<Task> getHistory();
}
