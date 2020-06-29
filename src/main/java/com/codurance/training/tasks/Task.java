package com.codurance.training.tasks;

public final class Task {
    private final long id;
    private final String description;
    private boolean done;

    private static long lastId =0;

    public Task( String description, boolean done) {
        this.id = nextId();
        this.description = description;
        this.done = done;

    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
    private long nextId() {
        return ++lastId;
    }
    public static void resetId(){
        Task.lastId =0;
    }
}
