package com.codurance.training.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public final class App implements Runnable {
    private static final String QUIT = "quit";

    private final List<Project> projects = new ArrayList<>();
    private final BufferedReader in;
    private final PrintWriter out;


    public static void main(String[] args) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(System.out);
        new App(in, out).run();
    }

    public App(BufferedReader reader, PrintWriter writer) {
        this.in = reader;
        this.out = writer;
        Task.resetId();
    }

    public void run() {
        while (true) {
            out.print("> ");
            out.flush();
            String command;
            try {
                command = in.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (command.equals(QUIT)) {
                break;
            }
            execute(command);
        }
    }

    private void execute(String commandLine) {
        String[] commandRest = commandLine.split(" ", 2);
        String command = commandRest[0];
        switch (command) {
            case "show":
                show();
                break;
            case "add":
                add(commandRest[1]);
                break;
            case "check":
                check(commandRest[1]);
                break;
            case "uncheck":
                uncheck(commandRest[1]);
                break;
            case "delete":
                deleteTask(commandRest[1]);
                break;
            case "help":
                help();
                break;
            default:
                error(command);
                break;
        }
    }

    private void show() {
        for (Project project : projects) {
            out.println(project.getName());
            for (Task task : project.getTasks()) {
                out.printf("    [%c] %d: %s%n", (task.isDone() ? 'x' : ' '), task.getId(), task.getDescription());
            }
            out.println();
        }
    }

    private void add(String commandLine) {
        String[] subcommandRest = commandLine.split(" ", 2);
        String subcommand = subcommandRest[0];
        if (subcommand.equals("project")) {
            if (subcommandRest.length<2 || subcommandRest[1].equals("")){
                out.print("Please enter a project name");
                out.println();
                return;
            }
            addProject(subcommandRest[1]);
        } else if (subcommand.equals("task")) {
            if (subcommandRest.length<2 || subcommandRest[1].equals("")){
                out.print("Please enter the project name and a task name");
                out.println();
                return;
            }
            String[] projectTask = subcommandRest[1].split(" ", 2);

            if (projectTask.length<2 || projectTask[1].equals("")){
                out.print("Please enter a task name");
                out.println();
                return;
            }
            addTask(projectTask[0], projectTask[1]);
        }
    }

    private void addProject(String name) {
        projects.add(new Project(name));
    }

    private void addTask(String projectName, String description) {
        Project foundProject = null;
        for (Project project: projects) {
            if (project.getName().equals(projectName)){
                foundProject = project;
            }
        }
        if (foundProject==null) {
            out.printf("Could not find a project with the name \"%s\".", projectName);
            out.println();
            return;
        }else{
            foundProject.addTask(new Task(description, false));
            return;
        }
    }

    private void check(String idString) {
        setDone(idString, true);
    }

    private void uncheck(String idString) {
        setDone(idString, false);
    }

    private void setDone(String idString, boolean done) {
        int id = Integer.parseInt(idString);
        for (Project project : projects) {
            Task foundTask = project.findTaskById(id);
            if (foundTask != null){
                foundTask.setDone(done);
                return;
            }
        }
        out.printf("Could not find a task with an ID of %d.", id);
        out.println();
    }

    private void deleteTask(String idString) {
        int id = Integer.parseInt(idString);
        for (Project project : projects) {
            Task foundTask = project.findTaskById(id);
            if (foundTask != null){
                project.removeTask(foundTask);
                return;
            }
        }
        out.printf("Could not find a task with an ID of %d.", id);
        out.println();
    }

    private void help() {
        out.println("Commands:");
        out.println("  show");
        out.println("  add project <project name>");
        out.println("  add task <project name> <task description>");
        out.println("  check <task ID>");
        out.println("  uncheck <task ID>");
        out.println("  delete <task ID>");
        out.println();
    }

    private void error(String command) {
        out.printf("I don't know what the command \"%s\" is.", command);
        out.println();
    }
}
