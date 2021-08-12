package com.example.QualityControlDemo.Controller;

import com.example.QualityControlDemo.Entity.Doctor;
import com.example.QualityControlDemo.Entity.Report;
import com.example.QualityControlDemo.Entity.Result;
import com.example.QualityControlDemo.Service.DoctorService;
import com.example.QualityControlDemo.Util.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
@Api(tags="医生信息接口管理")
@CrossOrigin
@RestController
@RequestMapping("/api/v1/")
public class DoctorController {
    @Autowired
    private DoctorService doctorService;

    @ApiOperation(value = "按ID查询医生信息")
    @ApiImplicitParam(name = "id", value="医生ID", required = true,paramType = "path")
    @GetMapping("doctors/{id}")
    public Result getDoctor(@PathVariable("id") Long id) {
        Optional<Doctor> doctor =  doctorService.findById(id);
        return ResultUtil.success(doctor);
    }
    @ApiOperation(value = "查询医生信息")
    @GetMapping("doctors")
    @ApiImplicitParam(name = "page", value="页数", required = true,defaultValue = "0",paramType = "query")
    public Result getDoctorList(@RequestParam(required = true, value = "page", defaultValue = "0") Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.Direction.ASC, "name");
        Page<Doctor> res = doctorService.findAll(pageable);
        return ResultUtil.success(res);
    }
}
