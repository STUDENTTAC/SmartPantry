package com.example.smartpantry;

/** One recipe: its name and preparation method (model class). */
public class Recipe {

    private int id;
    private String name;
    private String method;

    public Recipe(int id, String name, String method) {
        this.id = id;
        this.name = name;
        this.method = method;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMethod() {
        return method;
    }
}