package com.srms.desktop.model;

/** Department item shown in the combo box. */
public class Department {
    private final int id;
    private final String code, name;

    public Department(int id, String code, String name) { this.id = id; this.code = code; this.name = name; }
    public int getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    @Override public String toString() { return code + " - " + name; }
}
