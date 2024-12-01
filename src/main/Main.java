package main;

import task.*;
import manager.TaskManager;

public class Main {

    public static void main(String[] args) {
       TaskManager manager = new TaskManager();

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

       System.out.println("Все задачи");
       System.out.println(manager.getAllTasks());
       System.out.println("\nВсе эпики:");
       System.out.println(manager.getAllEpics());

       System.out.println("\nСабтаски Epic1:");
       System.out.println(manager.getSubtasksByEpic(epic1.getId()));
       System.out.println("\nСабтаски Epic2:");
       System.out.println(manager.getSubtasksByEpic(epic2.getId()));

       task1.setStatus(Status.DONE);
       manager.updateTask(task1);
       subtask1.setStatus(Status.DONE);
       manager.updateSubtask(subtask1);
       subtask2.setStatus(Status.IN_PROGRESS);
       manager.updateSubtask(subtask2);
       subtask3.setStatus(Status.DONE);
       manager.updateSubtask(subtask3);

       System.out.println("\nПосле обновления статусов:");
       System.out.println("Все задачи:");
       System.out.println(manager.getAllTasks());
       System.out.println("\nВсе эпики:");
       System.out.println(manager.getAllEpics());
       System.out.println("\nСабтаски Epic1:");
       System.out.println(manager.getSubtasksByEpic(epic1.getId()));
       System.out.println("\nСабтаски Epic2:");
       System.out.println(manager.getSubtasksByEpic(epic2.getId()));

       manager.deleteSubtask(subtask1.getId());
       System.out.println("\nПосле удаления subtask1:");
       System.out.println("Эпик Epic1:" + manager.getEpic(epic1.getId()));
       System.out.println("Сабтаски Epic1:" + manager.getSubtasksByEpic(epic1.getId()));

       manager.deleteSubtask(subtask2.getId());
       System.out.println("\nПосле удаления всех сабтасок Epic1:");
       System.out.println("Epic1:" + manager.getEpic(epic1.getId()));
       System.out.println("Caбтаски Epic1:" + manager.getSubtasksByEpic(epic1.getId()));

       manager.deleteTask(task2.getId());
       System.out.println("\nПосле удаления Task2:");
       System.out.println("Все задачи:");
       System.out.println(manager.getAllTasks());

       manager.deleteEpic(epic1.getId());
       System.out.println("\nПосле удаления Epic1:");
       System.out.println("Все эпики:");
       System.out.println(manager.getAllEpics());
       System.out.println("\nСабтаски Epic1:");
       System.out.println(manager.getSubtasksByEpic(epic1.getId()));
       System.out.println("\nСабтаски Epic2:");
       System.out.println(manager.getSubtasksByEpic(epic2.getId()));

    }
}
