package main;

import manager.TaskManager;
import manager.Managers;
import task.*;
import java.util.List;

public class Main {

    public static void main(String[] args) {
       TaskManager manager = Managers.getDefault();

       Task task1 = new Task("Задача1", "полить цветы", Status.NEW);
       Task task2 = new Task("Задача2", "убрать в комнате", Status.IN_PROGRESS);
       manager.addTask(task1);
       manager.addTask(task2);

       Epic epic1 = new Epic("Epic1", "переезд");
       manager.addEpic(epic1);
       Subtask subtask1 = new Subtask("Subtask1", "Упаковать коробки", Status.NEW, epic1.getId());
       Subtask subtask2 = new Subtask("Subtask2", "перенести вещи", Status.DONE, epic1.getId());
       manager.addSubtask(subtask1);
       manager.addSubtask(subtask2);

       Epic epic2 = new Epic("Epic2", "полить цветы");
       manager.addEpic(epic2);

       Subtask subtask3 = new Subtask("Subtask3", "набрать воду", Status.IN_PROGRESS, epic2.getId());
       manager.addSubtask(subtask3);

       List<Subtask> subtasks = manager.getSubtasksByEpic(epic1.getId());
       System.out.println("Подзадачи для эпика " + epic1.getName() + "':");
       for (Subtask subtask : subtasks) {
          System.out.println(subtask);
       }

       System.out.println("Начальная история:");
       System.out.println(manager.getHistory());
       manager.getTask(task1.getId());
       manager.getEpic(epic1.getId());
       manager.getSubtask(subtask1.getId());

       System.out.println("Обновленная история:");
       System.out.println(manager.getHistory());
    }
}
