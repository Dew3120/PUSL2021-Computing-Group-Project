package com.group100.wms.service;

import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.AttendanceRecord;
import com.group100.wms.model.Payroll;
import com.group100.wms.repository.AttendanceRepository;
import com.group100.wms.repository.PayrollRepository;
import com.group100.wms.util.ExcelExporter;
import com.group100.wms.util.PdfExporter;

import java.util.List;

public class ReportService {

    private final PayrollRepository payrollRepository;
    private final AttendanceRepository attendanceRepository;

    public ReportService(PayrollRepository payrollRepository,
                         AttendanceRepository attendanceRepository) {
        this.payrollRepository = payrollRepository;
        this.attendanceRepository = attendanceRepository;
    }

    public void exportPayrollPdf(int month, int year, String outputPath)
            throws DatabaseException {
        List<Payroll> payrolls = payrollRepository.findByMonthYear(month, year);
        PdfExporter.exportPayroll(payrolls, month, year, outputPath);
    }

    public void exportPayrollExcel(int month, int year, String outputPath)
            throws DatabaseException {
        List<Payroll> payrolls = payrollRepository.findByMonthYear(month, year);
        ExcelExporter.exportPayroll(payrolls, month, year, outputPath);
    }

    public void exportAttendancePdf(int month, int year, String outputPath)
            throws DatabaseException {
        List<AttendanceRecord> records = attendanceRepository.findByMonthYear(month, year);
        PdfExporter.exportAttendance(records, month, year, outputPath);
    }

    public void exportAttendanceExcel(int month, int year, String outputPath)
            throws DatabaseException {
        List<AttendanceRecord> records = attendanceRepository.findByMonthYear(month, year);
        ExcelExporter.exportAttendance(records, month, year, outputPath);
    }
}