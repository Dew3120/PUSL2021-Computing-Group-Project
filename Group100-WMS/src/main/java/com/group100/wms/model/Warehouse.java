package com.group100.wms.model;

/**
 * Represents a physical warehouse location.
 */
public class Warehouse {

    private int    id;
    private String name;
    private String location;
    private String managerName;
    private boolean isActive;

    public Warehouse() {}

    public Warehouse(int id, String name, String location,
                     String managerName, boolean isActive) {
        this.id          = id;
        this.name        = name;
        this.location    = location;
        this.managerName = managerName;
        this.isActive    = isActive;
    }

    public int     getId()          { return id; }
    public String  getName()        { return name; }
    public String  getLocation()    { return location; }
    public String  getManagerName() { return managerName; }
    public boolean isActive()       { return isActive; }

    public void setId(int id)                     { this.id = id; }
    public void setName(String name)               { this.name = name; }
    public void setLocation(String location)       { this.location = location; }
    public void setManagerName(String managerName) { this.managerName = managerName; }
    public void setActive(boolean active)          { this.isActive = active; }

    @Override
    public String toString() {
        return "Warehouse{id=" + id + ", name='" + name + "', location='" + location + "'}";
    }
}