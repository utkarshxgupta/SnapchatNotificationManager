package com.utkarshxgupta.snapnotifmanager.Model;

public class WhitelistModel {

    private int id;
    private String task;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task.trim();
    }
}
