package com.example.QualityControlDemo.Service;

import com.example.QualityControlDemo.Entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ReportService {
    Page<Report> findByDoctorId(Pageable pageable, Long doctorId);
    Page<Report> findByDate(Pageable pageable, Date date);
    Optional<Report> findByReportId(Long id);
    Page<Report> findByState(Pageable pageable, String state);
    Page<Report> findAll(Pageable pageable);
    void addReport(Report report);
    void deleteById(Long id);
}
