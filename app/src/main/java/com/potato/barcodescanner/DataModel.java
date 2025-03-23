package com.potato.barcodescanner;

public class DataModel {
    private int id;
    private String name;

    // constructors -> right click -> Generate...->Constructor
    // The construcor mehod is called whenever a new instance of the object is created.
    public DataModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public DataModel() {
    }

    // toString is necessary for printing the contents of a clas object


    @Override
    public String toString() {
        return "JavaModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    //Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
