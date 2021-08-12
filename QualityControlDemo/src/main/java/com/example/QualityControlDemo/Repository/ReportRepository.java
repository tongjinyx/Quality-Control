package com.example.QualityControlDemo.Repository;

import com.example.QualityControlDemo.Entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    Page<Report> findAll(Pageable pageable);
    Page<Report> findReportByDate(Pageable pageable, Date date);
    Page<Report> findReportByDoctorId(Pageable pageable, Long doctorId);
    Page<Report> findReportByState(Pageable pageable, String state);
}
