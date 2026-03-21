package com.group100.wms.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LeaveRequest {
    private int requestId;
    private int employeeId;
    private String employeeName;
    private String section;
    private LocalDate requestDate;
    private String leaveType;
    private String reason;
    private String status;
    private int createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;

    public LeaveRequest() {}

    public int getRequestId()            { return requestId; }
    public int getEmployeeId()           { return employeeId; }
    public String getEmployeeName()      { return employeeName; }
    public String getSection()           { return section; }
    public LocalDate getRequestDate()    { return requestDate; }
    public String getLeaveType()         { return leaveType; }
    public String getReason()            { return reason; }
    public String getStatus()            { return status; }
    public int getCreatedBy()            { return createdBy; }
    public LocalDateTime getCreatedAt()  { return createdAt; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }

    public void setRequestId(int v)            { this.requestId = v; }
    public void setEmployeeId(int v)           { this.employeeId = v; }
    public void setEmployeeName(String v)      { this.employeeName = v; }
    public void setSection(String v)           { this.section = v; }
    public void setRequestDate(LocalDate v)    { this.requestDate = v; }
    public void setLeaveType(String v)         { this.leaveType = v; }
    public void setReason(String v)            { this.reason = v; }
    public void setStatus(String v)            { this.status = v; }
    public void setCreatedBy(int v)            { this.createdBy = v; }
    public void setCreatedAt(LocalDateTime v)  { this.createdAt = v; }
    public void setReviewedAt(LocalDateTime v) { this.reviewedAt = v; }
}
