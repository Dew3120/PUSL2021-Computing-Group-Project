package com.group100.wms.model;

/**
 * Represents a system role — Admin, Manager, Supervisor, Accountant, etc.
 */
public class Role {

    private int    id;
    private String roleName;
    private String description;

    public Role() {}

    public Role(int id, String roleName, String description) {
        this.id          = id;
        this.roleName    = roleName;
        this.description = description;
    }

    public int    getId()          { return id; }
    public String getRoleName()    { return roleName; }
    public String getDescription() { return description; }

    public void setId(int id)                   { this.id = id; }
    public void setRoleName(String roleName)     { this.roleName = roleName; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "Role{id=" + id + ", roleName='" + roleName + "'}";
    }
}