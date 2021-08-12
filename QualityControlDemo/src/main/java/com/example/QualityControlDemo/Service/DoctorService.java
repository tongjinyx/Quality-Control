package com.example.QualityControlDemo.Service;

import com.example.QualityControlDemo.Entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface DoctorService {
    Optional<Doctor> findById(Long id);
    Page<Doctor> findAll(Pageable pageable);
}
