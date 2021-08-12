package com.example.QualityControlDemo.Service.ServiceImpl;

import com.example.QualityControlDemo.Entity.Report;
import com.example.QualityControlDemo.Repository.ReportRepository;
import com.example.QualityControlDemo.Service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportRepository reportRepository;
    @Override
    public Optional<Report> findByReportId(Long id) {
        return reportRepository.findById(id);
    }

    @Override
    public Page<Report> findByDate(Pageable pageable, Date date) {
        return reportRepository.findReportByDate(pageable, date);
    }
    @Override
    public Page<Report> findByDoctorId(Pageable pageable, Long doctorId) {
        return reportRepository.findReportByDoctorId(pageable, doctorId);
    }



    @Override
    public Page<Report> findByState(Pageable pageable, String state) {
        return reportRepository.findReportByState(pageable, state);
    }


    @Override
    public Page<Report> findAll(Pageable pageable) {
        return reportRepository.findAll(pageable);
    }



    @Override
    public void addReport(Report report) {
        reportRepository.save(report);
    }

    @Override
    public void deleteById(Long id) {
        reportRepository.deleteById(id);
    }
}
