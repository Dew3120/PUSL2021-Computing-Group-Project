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

    private static final LocalTime STANDARD_START = LocalTime.of(8, 0);
    private static final double    STANDARD_HOURS  = 9.0;

    private final AttendanceRepository attendanceRepository;

    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

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

    public List<AttendanceRecord> getAttendanceByMonthYear(int month, int year)
            throws DatabaseException {
        return attendanceRepository.findByMonthYear(month, year);
    }

    public List<AttendanceRecord> getAttendanceByEmployee(int employeeId)
            throws DatabaseException {
        return attendanceRepository.findByEmployee(employeeId);
    }

    public void updateAttendance(AttendanceRecord record) throws DatabaseException {
        record.setStatus(classifyAttendance(record.getClockIn(), record.getClockOut()));
        attendanceRepository.update(record);
        AuditLogger.log(SessionManager.getCurrentUser().getId(),
                "UPDATE", "attendance_records", String.valueOf(record.getId()),
                "Attendance updated for employeeId=" + record.getEmployeeId());
    }

    private String classifyAttendance(LocalTime clockIn, LocalTime clockOut) {
        if (clockIn == null || clockOut == null) return "ABSENT";
        double worked = (clockOut.toSecondOfDay() - clockIn.toSecondOfDay()) / 3600.0;
        if (worked >= 8.0) return "PRESENT";
        if (worked >= 4.0) return "HALF_DAY";
        return "ABSENT";
    }
}