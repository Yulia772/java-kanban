package main;

import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Задача1", "полить цветы", Status.NEW);
        Task task2 = new Task("Задача2", "убрать в комнате", Status.NEW);
        task1 = taskManager.addTask(task1);
        task2 = taskManager.addTask(task2);

        Epic epicWithSubs = new Epic("Эпик с подзадачами", "переезд");
        epicWithSubs = taskManager.addEpic(epicWithSubs);
        int epicId = epicWithSubs.getId();

        Subtask sub1 = new Subtask("Подзадача1", "Упаковать коробки", Status.NEW, epicWithSubs.getId());
        Subtask sub2 = new Subtask("Подзадача2", "перенести вещи", Status.IN_PROGRESS, epicWithSubs.getId());
        Subtask sub3 = new Subtask("Подзадача3", "загрузить машину", Status.DONE, epicWithSubs.getId());

        sub1 = taskManager.addSubtask(sub1);
        sub2 = taskManager.addSubtask(sub2);
        sub3 = taskManager.addSubtask(sub3);

        Epic epicWithoutSubs = new Epic("Эпик без подзадач", "полить цветы");
        epicWithoutSubs = taskManager.addEpic(epicWithoutSubs);

        taskManager.getTask(task1.getId());
        taskManager.getEpic(epicWithSubs.getId());
        taskManager.getTask(task2.getId());
        taskManager.getSubtask(sub2.getId());
        taskManager.getEpic(epicWithoutSubs.getId());
        taskManager.getSubtask(sub1.getId());
        taskManager.getSubtask(sub3.getId());
        taskManager.getSubtask(sub1.getId());

        System.out.println("История просмотров:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        taskManager.deleteTask(task1.getId());

        System.out.println("\nИстория после удаления task1:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        taskManager.deleteEpic(epicWithSubs.getId());

        System.out.println("\nИстория после удаления эпика с подзадачами");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}
