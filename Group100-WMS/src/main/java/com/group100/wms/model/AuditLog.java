package com.group100.wms.model;

import java.time.LocalDateTime;

public class AuditLog {
    private int id;
    private int userId;
    private String action;
    private String tableName;
    private String recordId;
    private String details;
    private LocalDateTime createdAt;

    public AuditLog() {}
    public AuditLog(int id, int userId, String action, String tableName,
                    String recordId, String details, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.action = action;
        this.tableName = tableName;
        this.recordId = recordId;
        this.details = details;
        this.createdAt = createdAt;
    }

    public int getId()                    { return id; }
    public int getUserId()                { return userId; }
    public String getAction()             { return action; }
    public String getTableName()          { return tableName; }
    public String getRecordId()           { return recordId; }
    public String getDetails()            { return details; }
    public LocalDateTime getCreatedAt()   { return createdAt; }

    public void setId(int id)                          { this.id = id; }
    public void setUserId(int userId)                  { this.userId = userId; }
    public void setAction(String action)               { this.action = action; }
    public void setTableName(String tableName)         { this.tableName = tableName; }
    public void setRecordId(String recordId)           { this.recordId = recordId; }
    public void setDetails(String details)             { this.details = details; }
    public void setCreatedAt(LocalDateTime createdAt)  { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "AuditLog{id=" + id + ", userId=" + userId
                + ", action='" + action + "', tableName='" + tableName
                + "', createdAt=" + createdAt + "}";
    }
}