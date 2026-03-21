package com.group100.wms.model;

import java.time.LocalDateTime;

/**
 * Represents a system user with login credentials and role assignment.
 */
public class User {

    private int           id;
    private String        username;
    private String        passwordHash;
    private int           roleId;
    private int           employeeId;
    private boolean       isActive;
    private LocalDateTime createdAt;

    public User() {}

    public User(int id, String username, String passwordHash,
                int roleId, int employeeId, boolean isActive,
                LocalDateTime createdAt) {
        this.id           = id;
        this.username     = username;
        this.passwordHash = passwordHash;
        this.roleId       = roleId;
        this.employeeId   = employeeId;
        this.isActive     = isActive;
        this.createdAt    = createdAt;
    }

    public int           getId()           { return id; }
    public String        getUsername()     { return username; }
    public String        getPasswordHash() { return passwordHash; }
    public int           getRoleId()       { return roleId; }
    public int           getEmployeeId()   { return employeeId; }
    public boolean       isActive()        { return isActive; }
    public LocalDateTime getCreatedAt()    { return createdAt; }

    public void setId(int id)                         { this.id = id; }
    public void setUsername(String username)           { this.username = username; }
    public void setPasswordHash(String passwordHash)   { this.passwordHash = passwordHash; }
    public void setRoleId(int roleId)                 { this.roleId = roleId; }
    public void setEmployeeId(int employeeId)         { this.employeeId = employeeId; }
    public void setActive(boolean active)             { this.isActive = active; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', roleId=" + roleId + "}";
    }
}