package com.group100.wms.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AttendanceRecordTest {

    @Test
    void calculatesWorkedAndOvertimeHours() {
        AttendanceRecord record = new AttendanceRecord(
                1, 10, LocalDate.of(2026, 7, 18),
                LocalTime.of(8, 0), LocalTime.of(18, 30), "PRESENT", 1);

        assertEquals(10.5, record.getHoursWorked(), 0.001);
        assertEquals(2.5, record.getOvertimeHours(), 0.001);
    }
}
