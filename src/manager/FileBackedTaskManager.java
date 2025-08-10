package manager;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;
                Task task = fromString(line);

                if (task instanceof Epic e) {
                    manager.epics.put(e.getId(), e);
                } else if (task instanceof Subtask s) {
                    manager.subtasks.put(s.getId(), s);
                } else {
                    manager.tasks.put(task.getId(), task);
                }
                manager.idCounter = Math.max(manager.idCounter, task.getId() + 1);
            }

            for (Subtask s : manager.subtasks.values()) {
                Epic e = manager.epics.get(s.getEpicId());
                if (e != null) e.addSubtaskId(s.getId());
            }
            for (Epic e : manager.epics.values()) {
                manager.updateEpicStatus(e);
                manager.updateEpicTiming(e);
            }
            manager.tasks.values().forEach(manager::addToPrioritized);
            manager.subtasks.values().forEach(manager::addToPrioritized);

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке из файла: " + file.getName(), e);
        }

        return manager;
    }

    public static void main(String[] args) {
        File file = new File("tasks.csv");

        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Task task1 = new Task("Задача1", "полить цветы", Status.NEW);
        Task task2 = new Task("Задача2", "убрать в комнате", Status.IN_PROGRESS);
        task1 = manager.addTask(task1);
        task2 = manager.addTask(task2);

        Epic epic = new Epic("Эпик с подзадачами", "переезд");
        epic = manager.addEpic(epic);

        Subtask sub1 = new Subtask("Подзадача1", "Упаковать коробки", Status.NEW, epic.getId());
        Subtask sub2 = new Subtask("Подзадача2", "перенести вещи", Status.NEW, epic.getId());


        sub1 = manager.addSubtask(sub1);
        sub2 = manager.addSubtask(sub2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        System.out.println("Все задачи из файла:");
        System.out.println(loadedManager.getAllTasks());
        System.out.println(loadedManager.getAllEpics());
        System.out.println(loadedManager.getAllSubtasks());
    }

    private static Task fromString(String line) {
        String[] fields = line.split(",", -1);
        int id = Integer.parseInt(fields[0]);
        String type = fields[1];
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

        Integer epicId;
        if (fields[5].isEmpty()) {
            epicId = null;
        } else {
            epicId = Integer.parseInt(fields[5]);
        }

        Duration duration;
        if (fields[6].isEmpty()) {
            duration = null;
        } else {
            duration = Duration.ofMinutes(Long.parseLong(fields[6]));
        }

        LocalDateTime start;
        if (fields[7].isEmpty()) {
            start = null;
        } else {
            start = LocalDateTime.parse(fields[7], DTF);
        }

        switch (type) {
            case "TASK" -> {
                Task task = new Task(name, description, status, duration, start);
                task.setId(id);
                return task;
        }
            case "EPIC" -> {
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                return epic;
        }
            case "SUBTASK" -> {
                if (epicId == null) throw new IllegalArgumentException("SUBTASK без epicId");
                Subtask subtask = new Subtask(name, description, status, epicId, duration, start);
                subtask.setId(id);
                return subtask;
        }
            default -> throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    @Override
    public Task addTask(Task task) {
        Task newTask = super.addTask(task);
        save();
        return newTask;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic newEpic = super.addEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Subtask newSubtask = super.addSubtask(subtask);
        save();
        return newSubtask;
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = getEpic(id);
        if (epic != null) {
            for (int subtaskId : new ArrayList<>(epic.getSubtaskIds())) {
                super.deleteSubtask(subtaskId);
            }
            epics.remove(id);
            historyManager.remove(id);
            save();
        }

    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    private void save() {
        try (Writer writer = new FileWriter(file, StandardCharsets.UTF_8, false)) {

            writer.write("id,type,name,status,description,epic,durationMinutes,startTime,endTime\n");

            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask) + "\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранени в файл: " + file.getName(), e);
        }
    }


    private String toString(Task task) {
        String type;
        String epicId = "";

        if (task instanceof Epic) {
            type = "EPIC";
        } else if (task instanceof Subtask) {
            type = "SUBTASK";
            epicId = String.valueOf(((Subtask) task).getEpicId());
        } else {
            type = "TASK";
        }
        String durationMin;
        if (task.getDuration() == null) {
            durationMin ="";
        } else {
            durationMin = String.valueOf(task.getDuration().toMinutes());
        }

        String start;
        if (task.getStartTime() == null) {
            start = "";
        } else {
            start = task.getStartTime().format(DTF);
        }

        String end;
        if (task.getEndTime() == null) {
            end = "";
        } else  {
            end = task.getEndTime().format(DTF);
        }

        return String.join(",",
                String.valueOf(task.getId()),
                type,
                task.getName(),
                task.getStatus().name(),
                task.getDescription(),
                epicId,
                durationMin,
                start,
                end
        );
    }
}

