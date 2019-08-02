package com.example.todolist;

public class TaskModel {
    public String task;
    public boolean isCompleted;

    public TaskModel(String task, boolean isCompleted) {
        this.task = task;
        this.isCompleted = isCompleted;
    }
}
