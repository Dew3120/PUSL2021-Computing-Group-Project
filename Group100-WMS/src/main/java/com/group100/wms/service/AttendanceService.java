// =============================================================================
// AttendanceService.java
// Part of: Centralized Apparel Warehouse Management System (WMS)
// Module: Service Layer — Attendance Business Logic
//
// OOP CONCEPTS USED IN THIS CLASS:
// - ENCAPSULATION: All fields are private. The classification rule is hidden
//   inside the private classifyAttendance() method — public methods expose
//   only what callers need (record, update, fetch), while the status logic
//   remains internal and reused consistently across recordAttendance() and
//   updateAttendance() without exposing it to outside classes.
// - ABSTRACTION: AttendanceRepository abstracts all direct database operations
//   (save, update, find). AuditLogger abstracts audit trail writing.
//   SessionManager abstracts current user session state. This service only
//   expresses attendance business rules — not how data is stored or logged.
// - POLYMORPHISM: classifyAttendance() is called with clock-in/out values
//   from two different contexts (a fresh record in recordAttendance(), and an
//   existing record in updateAttendance()), producing the correct status in
//   both cases from the same method — parametric reuse across call sites.
// - INHERITANCE: AttendanceRecord follows the standard JavaBean contract,
//   with all fields accessed through getters and setters inherited from the
//   model layer's design pattern used consistently across the application.
// =============================================================================

package com.group100.wms.service;

import com.group100.wms.core.AuditLogger;
import com.group100.wms.core.SessionManager;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.AttendanceRecord;
import com.group100.wms.repository.AttendanceRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AttendanceService {

    // The standard workday start time (08:00) — defined as a constant for
    // potential future use in late-arrival or shift-based classification logic
    private static final LocalTime STANDARD_START = LocalTime.of(8, 0);

    // The standard number of hours in a full workday (9.0 hours) — defined
    // as a constant for reference in scheduling or reporting calculations
    private static final double    STANDARD_HOURS  = 9.0;

    // Repository dependency for all attendance database operations (save, update, find).
    // Injected via constructor to allow independent testing and swapping of implementations.
    private final AttendanceRepository attendanceRepository;

    // Constructor that injects the AttendanceRepository dependency.
    // Follows the Dependency Injection pattern so the repository can be
    // swapped or mocked without modifying this service class.
    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    // Creates and persists a new attendance record for a given employee on a specific date.
    // Automatically classifies the attendance status (PRESENT, HALF_DAY, or ABSENT)
    // based on the clock-in and clock-out times using classifyAttendance().
    // Stamps the record with the current logged-in user as the approver,
    // saves it to the database, and writes an audit log entry.
    // Returns the saved AttendanceRecord. Throws DatabaseException on save failure.
    public AttendanceRecord recordAttendance(int employeeId, LocalDate date,
                                             LocalTime clockIn, LocalTime clockOut)
            throws DatabaseException {
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeId(employeeId);
        record.setDate(date);
        record.setClockIn(clockIn);
        record.setClockOut(clockOut);
        record.setStatus(classifyAttendance(clockIn, clockOut));
        record.setApprovedBy(SessionManager.getCurrentUser().getId());
        attendanceRepository.save(record);
        AuditLogger.log(SessionManager.getCurrentUser().getId(),
                "CREATE", "attendance_records", String.valueOf(record.getId()),
                "Attendance recorded for employeeId=" + employeeId + " date=" + date);
        return record;
    }

    // Retrieves all attendance records for a given month and year.
    // Delegates directly to the repository and returns the full result list.
    // Throws DatabaseException if the query fails.
    public List<AttendanceRecord> getAttendanceByMonthYear(int month, int year)
            throws DatabaseException {
        return attendanceRepository.findByMonthYear(month, year);
    }

    // Retrieves the complete attendance history for a specific employee by their ID.
    // Delegates directly to the repository and returns the full result list.
    // Throws DatabaseException if the query fails.
    public List<AttendanceRecord> getAttendanceByEmployee(int employeeId)
            throws DatabaseException {
        return attendanceRepository.findByEmployee(employeeId);
    }

    // Updates an existing attendance record in the database.
    // Recalculates and sets the attendance status from the record's current
    // clock-in and clock-out values before persisting, ensuring the status
    // always reflects the actual hours worked. Writes an audit log entry on success.
    // Throws DatabaseException if the update fails.
    public void updateAttendance(AttendanceRecord record) throws DatabaseException {
        record.setStatus(classifyAttendance(record.getClockIn(), record.getClockOut()));
        attendanceRepository.update(record);
        AuditLogger.log(SessionManager.getCurrentUser().getId(),
                "UPDATE", "attendance_records", String.valueOf(record.getId()),
                "Attendance updated for employeeId=" + record.getEmployeeId());
    }

    // Derives the attendance status string from a pair of clock-in and clock-out times.
    // Returns "ABSENT" if either time is null (employee did not clock in or out).
    // Returns "PRESENT" if the employee worked 8 or more hours.
    // Returns "HALF_DAY" if the employee worked between 4 and 8 hours.
    // Returns "ABSENT" if the employee worked fewer than 4 hours.
    // Used by both recordAttendance() and updateAttendance() to ensure consistent classification.
    private String classifyAttendance(LocalTime clockIn, LocalTime clockOut) {
        if (clockIn == null || clockOut == null) return "ABSENT";
        double worked = (clockOut.toSecondOfDay() - clockIn.toSecondOfDay()) / 3600.0;
        if (worked >= 8.0) return "PRESENT";
        if (worked >= 4.0) return "HALF_DAY";
        return "ABSENT";
    }
}