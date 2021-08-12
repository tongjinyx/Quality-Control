package com.example.QualityControlDemo.Controller;

import com.example.QualityControlDemo.Entity.Doctor;
import com.example.QualityControlDemo.Entity.Report;
import com.example.QualityControlDemo.Entity.Result;
import com.example.QualityControlDemo.Entity.ResultEnum;
import com.example.QualityControlDemo.Service.DoctorService;
import com.example.QualityControlDemo.Service.ReportService;
import com.example.QualityControlDemo.Util.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Api(tags="胰腺报告接口管理")
@CrossOrigin
@RestController
@RequestMapping("/api/v1/")
public class ReportController {
    @Autowired
    private ReportService reportService;
    @Autowired
    private DoctorService doctorService;

    @ApiOperation(value = "查询报告信息",notes="请保证非必选参数只有一个")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", value="页数", required = true,defaultValue = "0"),
                        @ApiImplicitParam(name = "doctorId", value="医生ID", required = false),
                        @ApiImplicitParam(name = "state", value="文本状态(已检查或未处理)", required = false),
                        @ApiImplicitParam(name = "textId", value="文本ID", required = false)})
    @GetMapping("/reports")
    public Result getReportList(@RequestParam(required = true, value = "page", defaultValue = "0") Integer page,
                                @RequestParam(required = false, value = "doctorId") Long doctorId,
                                @RequestParam(required = false, value = "state") String state,
                                @RequestParam(required = false, value = "date") String time) {
        Pageable pageable = PageRequest.of(page, 10, Sort.Direction.DESC, "date");
        if(doctorId==null  && state==null && time==null ) {
            Page<Report> res = reportService.findAll(pageable);
            return ResultUtil.success(res);
        }
        else if(doctorId!=null  && state==null && time==null ) {
            Page<Report> res = reportService.findByDoctorId(pageable, doctorId);
            return ResultUtil.success(res);
        }

        else if(doctorId==null && state!=null && time==null ) {
            Page<Report> res = reportService.findByState(pageable, state);
            return ResultUtil.success(res);
        }
        else if(doctorId==null && state==null && time!=null ) {
            Date date = null;
            try {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
                date = dateFormat.parse(time);
            }
            catch (Exception e) {

            }
            if (date==null) {
                return ResultUtil.error(ResultEnum.DATE_FORMAT_ERROR.getCode(), ResultEnum.DATE_FORMAT_ERROR.getMsg());
            }
            Page<Report> res = reportService.findByDate(pageable, date);
            return ResultUtil.success(res);
        }

        return ResultUtil.error(ResultEnum.UNKNOWN_ERROR.getCode(), ResultEnum.UNKNOWN_ERROR.getMsg());
    }
    @ApiOperation(value = "按ID查询报告信息")
    @GetMapping("/reports/{id}")
    @ApiImplicitParam(name = "id", value="文本ID", required = true,paramType = "path")
    public Result getReport(@PathVariable("id") Long id) {
        Optional<Report> report = reportService.findByReportId(id);
        if (report.isEmpty()) {
            return ResultUtil.error(ResultEnum.TEXT_NOT_EXIST.getCode(), ResultEnum.TEXT_NOT_EXIST.getMsg());
        }
        return ResultUtil.success(report.get());
    }
    @ApiOperation(value = "上传报告信息")
    @PostMapping("/reports")
    @ApiImplicitParams({@ApiImplicitParam(name = "conclusion", value="检查结论文本内容", required = true),
                        @ApiImplicitParam(name = "doctorId", value="医生ID", required = true),
                        @ApiImplicitParam(name = "finding", value="检查所见文本内容", required = true),
                        @ApiImplicitParam(name = "date", value="日期(yyyy-mm-dd)", required = true)})
    public Result addReport(@RequestParam("conclusion") String conclusion,
                            @RequestParam("finding") String finding,
                            @RequestParam("doctorId") Long doctorId,
                            @RequestParam("date") String time
                            ) {
        Date date = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
            date = dateFormat.parse(time);
        }
        catch (Exception e) {

        }
        if (date==null) {
            return ResultUtil.error(ResultEnum.DATE_FORMAT_ERROR.getCode(), ResultEnum.DATE_FORMAT_ERROR.getMsg());
        }
        //医生不存在
        Optional<Doctor> doctor = doctorService.findById(doctorId);
        if (doctor.isEmpty()) {
            return ResultUtil.error(ResultEnum.DOCTOR_NOT_EXISTS.getCode(), ResultEnum.DOCTOR_NOT_EXISTS.getMsg());
        }
        Report report = new Report(conclusion,finding,doctorId,date,"未处理",0.0,0.0);
        reportService.addReport(report);
        return ResultUtil.success();
    }

    @DeleteMapping("/reports/{id}")
    public Result delReport(@PathVariable("id") Long id) {
        Optional<Report> report = reportService.findByReportId(id);
        if (report.isEmpty()) {
            return ResultUtil.error(ResultEnum.TEXT_NOT_EXIST.getCode(), ResultEnum.TEXT_NOT_EXIST.getMsg());
        }
        reportService.deleteById(id);
        return ResultUtil.success();
    }
}
