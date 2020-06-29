package com.codurance.training.tasks;

import java.util.ArrayList;
import java.util.List;

public class Project {
    private List<Task> tasks = new ArrayList<>();
    private String name;
    public Project(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void addTask(Task task){
        this.tasks.add(task);
    }
}
