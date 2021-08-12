package com.example.QualityControlDemo.Controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.QualityControlDemo.Entity.*;
import com.example.QualityControlDemo.QualityControlDemoApplication;
import com.example.QualityControlDemo.Service.ReportService;
import com.example.QualityControlDemo.Service.ServiceImpl.ApiServiceImpl;
import com.example.QualityControlDemo.Util.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.print.attribute.standard.MediaSize;
import java.io.*;
import java.util.*;
@Api(tags="质控报告接口管理")
@CrossOrigin
@RestController
@RequestMapping("/api/v1/")
public class DataController {
    @Autowired
    private ApiServiceImpl apiService;
    @Autowired
    private ReportService reportService;

    private JSONObject normalizedJson;
    //缓存归一化json文件
    @PostConstruct
    public void getNormalizedJson() {
        try{
            String path = Thread.currentThread().getContextClassLoader().getResource("").getPath()+"static/normalization_dict_0810.json";
            File jsonFile = new File(path);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile),"UTF-8");
            int ch=0;
            StringBuilder sb = new StringBuilder();
            while ((ch=reader.read())!=-1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            String jsonStr = sb.toString();
            this.normalizedJson = JSONObject.parseObject(jsonStr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @GetMapping("/ner")
    public Result getNerData(@RequestParam("sentence") String sentence) {
        return ResultUtil.success( apiService.getEntity(sentence));
    }

    @GetMapping("/nre")
    public Result getNreData(@RequestParam("sentence") String sentence) {
        JSONObject data = apiService.getEntity(sentence);
        String jsonString = data.toJSONString();
        return ResultUtil.success(apiService.getRelation(jsonString));
    }

    @ApiOperation(value = "按ID查询结果")
    @ApiImplicitParam(name = "id", value="文本ID", required = true,paramType = "path")
    @GetMapping("/score/{id}")
    public Result getScore(@PathVariable("id") Long reportId) {
        Optional<Report> report = reportService.findByReportId(reportId);
        if (report.isEmpty()) {
            return ResultUtil.error(ResultEnum.TEXT_NOT_EXIST.getCode(), ResultEnum.TEXT_NOT_EXIST.getMsg());
        }
        Template template = new Template();
        if (report.get().finding.isEmpty()||report.get().conclusion.isEmpty()) {
            return ResultUtil.error(ResultEnum.TEXT_NULL.getCode(),ResultEnum.TEXT_NULL.getMsg());
        }
        else{
            FindingTemplate findingTemplate = new FindingTemplate();
            //调用接口得到实体与关系
            JSONObject apiData1 = apiService.getEntity(report.get().finding);
            JSONObject apiData2 = apiService.getRelation(apiData1.toString());
            JSONObject data = apiData2.getJSONObject("entities");
            JSONArray entities = data.getJSONArray("labels");
            JSONArray relations = data.getJSONArray("connections");
            String density = "";
            String size = "";
            String position = "";
            String margin = "";
            String pancreasDensity = "";
            String pancreasSize = "";
            Boolean jieduan=false;
            Boolean kuozhang=false;
            Peripancreatic peripancreatic = new Peripancreatic();
            Parenchyma parenchyma = new Parenchyma();
            Artery artery = new Artery();

            Vein vein = new Vein();
            SimpleTemplate mesentery = new SimpleTemplate();//肠系膜上动脉
            SimpleTemplate cavity = new SimpleTemplate();//腹腔干
            SimpleTemplate liver = new SimpleTemplate();//肝总动脉
            SimpleTemplate door = new SimpleTemplate();//门静脉
            SimpleTemplate mesenteryVein = new SimpleTemplate();//肠系膜上静脉
            SimpleTemplate arteryVariation = new SimpleTemplate();//动脉变异
            String cancerHydrant = "";
            String veinSideLoop="";
            String lymph="";
            OtherTemplate otherTemplate = new OtherTemplate();
            String increase="";
            Pancreas pancreas = new Pancreas();
            for (Iterator<Object> relation = relations.iterator(); relation.hasNext(); ) {
                JSONObject jsonObject1 = (JSONObject) relation.next();
                String from1 = jsonObject1.getString("fromId");
                String to1 = jsonObject1.getString("toId");
                String pos1 = jsonObject1.getString("categoryId");
                JSONObject from_entity1 = (JSONObject) entities.get(Integer.valueOf(from1));
                JSONObject to_entity1 = (JSONObject) entities.get(Integer.valueOf(to1));

                if (from_entity1.getString("categoryId").equals("7")) { //如果是部位
                    if (normalise("7",from_entity1.getString("text")).equals("胰管")) { //如果是胰管
                        if (to_entity1.getString("categoryId").equals("9") ) { //症状
                            if (normalise("9",to_entity1.getString("text")).equals("狭窄")  || (normalise("9",to_entity1.getString("text")).equals("截断")|| normalise("9",to_entity1.getString("text")).equals("扩张"))) {
                                if (pos1.equals("1")) {
                                    parenchyma.setSymptom1(true);
                                }
                                else {
                                    parenchyma.setSymptom1(false);

                                }
                            }
                        }
                        else if (to_entity1.getString("categoryId").equals("12") ) { //值
                            if (normalise("12",to_entity1.getString("text")).equals("狭窄") || normalise("12",to_entity1.getString("text")).equals("截断")||normalise("12",to_entity1.getString("text")).equals("扩张")) {
                                if (pos1.equals("1")) {
                                    parenchyma.setSymptom1(true);
                                }
                                else {
                                    parenchyma.setSymptom1(false);

                                }
                            }
                        }
                    }
                    else if (normalise("7",from_entity1.getString("text")).equals("胰头")) {
                        if (to_entity1.getString("categoryId").equals("9")) { //有症状
                            if (normalise("7",to_entity1.getString("text")).equals("增大")) {
                                if (pos1.equals("1")) {
                                    increase="胰头";
                                }
                            }
                        }
                        else if (to_entity1.getString("categoryId").equals("12")) { //值
                            if (normalise("12",to_entity1.getString("text")).equals("增大")) {
                                if (pos1.equals("1")) {
                                    increase="胰头";
                                }
                            }
                        }
                    }
                    else if (normalise("7",from_entity1.getString("text")).equals("胰颈")) {
                        if (to_entity1.getString("categoryId").equals("9")) { //有症状
                            if (normalise("7",to_entity1.getString("text")).equals("增大")) {
                                if (pos1.equals("1")) {
                                    increase="胰颈";
                                }
                            }
                        }
                        else if (to_entity1.getString("categoryId").equals("12")) { //值
                            if (normalise("12",to_entity1.getString("text")).equals("增大")) {
                                if (pos1.equals("1")) {
                                    increase="胰颈";
                                }
                            }
                        }
                    }
                    else if (normalise("7",from_entity1.getString("text")).equals("胰体")) {
                        if (to_entity1.getString("categoryId").equals("9")) { //有症状
                            if (normalise("7",to_entity1.getString("text")).equals("增大")) {
                                if (pos1.equals("1")) {
                                    increase="胰体";
                                }
                            }
                        }
                        else if (to_entity1.getString("categoryId").equals("12")) { //值
                            if (normalise("12",to_entity1.getString("text")).equals("增大")) {
                                if (pos1.equals("1")) {
                                    increase="胰体";
                                }
                            }
                        }
                    }
                    else if (normalise("7",from_entity1.getString("text")).equals("胰尾")) {
                        if (to_entity1.getString("categoryId").equals("9")) { //有症状
                            if (normalise("7",to_entity1.getString("text")).equals("增大")) {
                                if (pos1.equals("1")) {
                                    increase="胰尾";
                                }
                            }
                        }
                        else if (to_entity1.getString("categoryId").equals("12")) { //值
                            if (normalise("12",to_entity1.getString("text")).equals("增大")) {
                                if (pos1.equals("1")) {
                                    increase="胰尾";
                                }
                            }
                        }
                    }
                    else if (normalise("7",from_entity1.getString("text")).equals("胰腺")) {
                        if (to_entity1.getString("categoryId").equals("0")) { //属性
                            if (normalise("0",to_entity1.getString("text")).equals("边缘")) {
                                for (Iterator<Object> iterator = relations.iterator();iterator.hasNext();) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if (to1.equals(from2)) {
                                        if (to_entity2.getString("categoryId").equals("12")) { //值
                                            if (pos1.equals("1") && pos2.equals("1")) {
                                                margin = normalise("12",to_entity2.getString("text"));
                                            }
                                        }
                                    }
                                }
                            }
                            else if (normalise("0",to_entity1.getString("text")).equals("密度")) {

                                for (Iterator<Object> iterator = relations.iterator();iterator.hasNext();) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if (to1.equals(from2)) {

                                        if (to_entity2.getString("categoryId").equals("12")) { //值
                                            if (pos1.equals("1") && pos2.equals("1")) {
                                                pancreasDensity = normalise("12",to_entity2.getString("text"));
                                            }
                                        }
                                    }
                                }
                            }
                            else if (normalise("0",to_entity1.getString("text")).equals("大小")) {

                                for (Iterator<Object> iterator = relations.iterator();iterator.hasNext();) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if (to1.equals(from2)) {
                                        if (to_entity2.getString("categoryId").equals("12")) { //值
                                            if (pos1.equals("1") && pos2.equals("1")) {
                                                pancreas.setSize(normalise("12",to_entity2.getString("text")));
                                            }
                                            else {
                                                pancreas.setSize("无"+normalise("12",to_entity2.getString("text")));
                                            }
                                        }
                                    }
                                }
                            }
                            else if (normalise("0",to_entity1.getString("text")).equals("形态")) {

                                for (Iterator<Object> iterator = relations.iterator();iterator.hasNext();) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if (to1.equals(from2)) {
                                        if (to_entity2.getString("categoryId").equals("12")) { //值
                                            if (pos1.equals("1") && pos2.equals("1")) {
                                                pancreas.setShape(normalise("12",to_entity2.getString("text")));
                                            }
                                            else {
                                                pancreas.setShape("无"+normalise("12",to_entity2.getString("text")));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else if (to_entity1.getString("categoryId").equals("9")) { //症状
                            if (normalise("9",to_entity1.getString("text")).equals("异常密度影")) {
                                if (pos1.equals("1")) {
                                    pancreas.setIsDensityShadowUnusual(true);
                                }
                                else {
                                    pancreas.setIsDensityShadowUnusual(false);
                                }
                            }
                            else if (normalise("9",to_entity1.getString("text")).equals("强化")) {
                                if (pos1.equals("1")) {
                                    pancreas.setIsStrengtheningStoveUnusual(true);
                                }
                                else {
                                    pancreas.setIsStrengtheningStoveUnusual(false);
                                }
                            }
                        }
                        else if (to_entity1.getString("categoryId").equals("12")) { //值
                            if (normalise("12",to_entity1.getString("text")).equals("强化")) {
                                if (pos1.equals("1")) {
                                    pancreas.setIsStrengtheningStoveUnusual(true);
                                } else {
                                    pancreas.setIsStrengtheningStoveUnusual(false);
                                }
                            }
                        }
                    }
                    else if (normalise("7",from_entity1.getString("text")).equals("胰周")) {
                        if (to_entity1.getString("categoryId").equals("9")) { //症状
                            if (pos1.equals("1")) {
                                peripancreatic.setChange(true);
                            }
                            else {
                                peripancreatic.setChange(false);
                            }
                            if (normalise("9",to_entity1.getString("text")).equals("积液")) {
                                if (pos1.equals("1")) {
                                    peripancreatic.setEffusion(true);
                                }
                                else {
                                    peripancreatic.setEffusion(false);
                                }
                            }
                        }
                        else if (to_entity1.getString("categoryId").equals("12")) { //值
                            if (pos1.equals("1")) {
                                peripancreatic.setChange(true);
                            }
                            else {
                                peripancreatic.setChange(false);
                            }
                        }
                    }
                    else if (normalise("7",from_entity1.getString("text")).equals("胆道系统")) { //判断胆道系统
                        if (to_entity1.getString("categoryId").equals("9")) { //症状
                            if (normalise("9",to_entity1.getString("text")).equals("截断")  || normalise("9",to_entity1.getString("text")).equals("异常")  ){ //判断是否截断（异常）
                                if(pos1.equals("1")) {
                                    jieduan=true;
                                }
                                else {
                                    jieduan=false;
                                }

                            }
                            else if (normalise("9",to_entity1.getString("text")).equals("扩张")) { //判断有无扩张
                                if(pos1.equals("1")) {
                                    kuozhang=true;
                                }
                                else {
                                    kuozhang=false;
                                }
                            }
                        }
                    }
                    else if (normalise("7",from_entity1.getString("text")).equals("脾静脉")) {//如果是脾静脉
                        if (to_entity1.getString("categoryId").equals("7") && pos1.equals("1")) {
                            if (normalise("7",to_entity1.getString("text")).equals("癌栓")) { //判断癌栓
                                cancerHydrant+="脾静脉,";
                            }
                        }
                    }
                    else if (normalise("7",from_entity1.getString("text")).contains("上动脉")) { //判断肠系膜上动脉
                        mesentery.setName("肠系膜上动脉");
                        if (to_entity1.getString("categoryId").equals("9")) { //有症状
                            if (normalise("9",to_entity1.getString("text")).equals("侵犯") || normalise("9",to_entity1.getString("text")).equals("异常") ){
                                if (pos1.equals("1")) {
                                    mesentery.setSolidMassContactDegree(">180");
                                }
                                else {
                                    mesentery.setSolidMassContactDegree("<=180");
                                }

                            }
                            if (normalise("9",to_entity1.getString("text")).equals("侵犯")) { //先找侵犯症状
                                for (Iterator<Object> iterator = relations.iterator();iterator.hasNext();) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if (to2.equals(to1)) { //找到目标症状且位于 to
                                        if (from_entity2.getString("categoryId").equals("7")) { //如果是部位
                                            if (normalise("7",from_entity2.getString("text")).equals("空肠动脉")) { //再找部位
                                                if (pos1.equals("1")&&pos2.equals("1")) {
                                                    mesentery.setArteryIntestinalInvaded(true);//侵犯空肠动脉
                                                }
                                                else {
                                                    mesentery.setArteryIntestinalInvaded(false);//未侵犯空肠动脉
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }
                        else if (to_entity1.getString("categoryId").equals("12")) { //值
                            if (normalise("12",to_entity1.getString("text")).equals("侵犯") || normalise("9",to_entity1.getString("text")).equals("异常")) {
                                if (pos1.equals("1")) {
                                    mesentery.setSolidMassContactDegree(">180");
                                }
                                else {
                                    mesentery.setSolidMassContactDegree("<=180");
                                }

                            }
                        }
                        else if (to_entity1.getString("categoryId").equals("0")) { //属性
                            if (normalise("0",to_entity1.getString("text")).equals("边界")) { //边界模糊
                                for (Iterator<Object> iterator = relations.iterator();iterator.hasNext();) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if (from2.equals(to1)) {
                                        if (to_entity2.getString("categoryId").equals("12")) { //值
                                            if (normalise("12",to_entity2.getString("text")).equals("模糊")) {
                                                if (pos1.equals("1") && pos2.equals("1")) {
                                                    mesentery.setSolidMassBoundariesBlurred(">180");
                                                }
                                                else {
                                                    mesentery.setSolidMassBoundariesBlurred("<=180");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            else if (normalise("0",to_entity1.getString("text")).equals("接触面")) { //接触面呈条状
                                for (Iterator<Object> iterator = relations.iterator();iterator.hasNext();) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if (from2.equals(to1)) {
                                        if (to_entity2.getString("categoryId").equals("12")) { //值
                                            if (normalise("12",to_entity2.getString("text")).equals("条状")) {
                                                if (pos1.equals("1") && pos2.equals("1")) {
                                                    mesentery.setSolidMassBoundariesBlurred(">180");
                                                }
                                                else {
                                                    mesentery.setSolidMassBoundariesBlurred("<=180");
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            else if (normalise("0",to_entity1.getString("text")).equals("轮廓")) { //轮廓不规则
                                for (Iterator<Object> iterator = relations.iterator();iterator.hasNext();) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if (from2.equals(to1)) {
                                        if (to_entity2.getString("categoryId").equals("12")) { //值
                                            if (normalise("12",to_entity2.getString("text")).equals("不规则")) {
                                                if (pos1.equals("1") && pos2.equals("1")) {
                                                    mesentery.setArteryBloodVesselStenosis(true);
                                                }
                                                else {
                                                    mesentery.setArteryBloodVesselStenosis(false);
                                                }
                                            }
                                            else if (normalise("12",to_entity2.getString("text")).equals("规则")) {
                                                if (pos1.equals("0") || pos2.equals("0")) {
                                                    mesentery.setArteryBloodVesselStenosis(true);
                                                }
                                                else {
                                                    mesentery.setArteryBloodVesselStenosis(false);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else if (to_entity1.getString("categoryId").equals("7")) { //如果是部位
                            if (normalise("7",to_entity1.getString("text")).equals("血管")) {
                                for (Iterator<Object> iterator = relations.iterator();iterator.hasNext();) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if(to1.equals(from2)) {//找到血管
                                        if (from_entity2.getString("categoryId").equals("9")) {
                                            if (normalise("9",from_entity2.getString("text")).equals("狭窄")) { //血管狭窄
                                                if (pos1.equals("1")&&pos2.equals("1")) {
                                                    mesentery.setArteryBloodVesselStenosis(true);
                                                }
                                                else {
                                                    mesentery.setArteryBloodVesselStenosis(false);
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                    else if (normalise("7",from_entity1.getString("text")).equals("腹腔干")) { //判断腹腔干
                        cavity.setName("腹腔干");
                        if (to_entity1.getString("categoryId").equals("9")) { //有症状
                            if (normalise("9",to_entity1.getString("text")).equals("侵犯") || normalise("9",to_entity1.getString("text")).equals("异常") ){
                                if (pos1.equals("1")) {
                                    cavity.setSolidMassContactDegree(">180");
                                }
                                else {
                                    cavity.setSolidMassContactDegree("<=180");
                                }
                            }
                        }
                        else if (to_entity1.getString("categoryId").equals("12")) { //值
                            if (normalise("12",to_entity1.getString("text")).equals("侵犯") || normalise("9",to_entity1.getString("text")).equals("异常")) {
                                if (pos1.equals("1")) {
                                    cavity.setSolidMassContactDegree(">180");
                                }
                                else {
                                    cavity.setSolidMassContactDegree("<=180");
                                }

                            }
                        }
                        else if (to_entity1.getString("categoryId").equals("0")) {//属性
                            if (normalise("0",to_entity1.getString("text")).equals("边界")) { //边界模糊
                                for (Iterator<Object> iterator = relations.iterator();iterator.hasNext();) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if (from2.equals(to1)) {
                                        if (to_entity2.getString("categoryId").equals("12")) { //值
                                            if (normalise("12",to_entity2.getString("text")).equals("模糊")) {
                                                if (pos1.equals("1") && pos2.equals("1")) {
                                                    cavity.setSolidMassBoundariesBlurred(">180");
                                                }
                                                else {
                                                    cavity.setSolidMassBoundariesBlurred("<=180");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            else if (normalise("0",to_entity1.getString("text")).equals("接触面")) { //接触面呈条状
                                for (Iterator<Object> iterator = relations.iterator();iterator.hasNext();) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if (from2.equals(to1)) {
                                        if (to_entity2.getString("categoryId").equals("12")) { //值
                                            if (normalise("12",to_entity2.getString("text")).equals("条状")) {
                                                if (pos1.equals("1") && pos2.equals("1")) {
                                                    cavity.setSolidMassBoundariesBlurred(">180");
                                                }
                                                else {
                                                    cavity.setSolidMassBoundariesBlurred("<=180");
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            else if (normalise("0",to_entity1.getString("text")).equals("轮廓")) { //轮廓不规则
                                for (Iterator<Object> iterator = relations.iterator();iterator.hasNext();) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if (from2.equals(to1)) {
                                        if (to_entity2.getString("categoryId").equals("12")) { //值
                                            if (normalise("12",to_entity2.getString("text")).equals("不规则")) {
                                                if (pos1.equals("1") && pos2.equals("1")) {
                                                    cavity.setArteryBloodVesselStenosis(true);
                                                }
                                                else {
                                                    cavity.setArteryBloodVesselStenosis(false);
                                                }
                                            }
                                            else if (normalise("12",to_entity2.getString("text")).equals("规则")) {
                                                if (pos1.equals("0") || pos2.equals("0")) {
                                                    cavity.setArteryBloodVesselStenosis(true);
                                                }
                                                else {
                                                    cavity.setArteryBloodVesselStenosis(false);
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }

                    }
                    else if (normalise("7",from_entity1.getString("text")).equals("肝总动脉")) { //判断肝总动脉
                        System.out.println("有肝总动脉");
                        if (to_entity1.getString("categoryId").equals("9")) { //有症状
                            if (normalise("9",to_entity1.getString("text")).equals("侵犯") || normalise("9",to_entity1.getString("text")).equals("异常") ){
                                if (pos1.equals("1")) {
                                    liver.setSolidMassContactDegree(">180");
                                }
                                else {
                                    liver.setSolidMassContactDegree("<=180");
                                }
                            }
                            if (normalise("9",to_entity1.getString("text")).equals("侵犯")) { //先找侵犯症状
                                for (Iterator<Object> iterator = relations.iterator();iterator.hasNext();) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if (to2.equals(to1)) { //找到目标症状且位于 to
                                        if (from_entity2.getString("categoryId").equals("7")) { //如果是部位
                                            if (normalise("7",from_entity2.getString("text")).equals("腹腔干")) { //再找部位
                                                if (pos1.equals("1")&&pos2.equals("1")) {
                                                    liver.setCeliacArteryInvaded(true);
                                                }
                                                else {
                                                    liver.setCeliacArteryInvaded(false);
                                                }
                                            }
                                            else if (normalise("7",from_entity2.getString("text")).equals("肝左右动脉")) {
                                                if (pos1.equals("1")&&pos2.equals("1")) {
                                                    liver.setHepaticArteryInvaded(true);
                                                }
                                                else {
                                                    liver.setCeliacArteryInvaded(false);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else if (to_entity1.getString("categoryId").equals("12")) { //值
                            if (normalise("12",to_entity1.getString("text")).equals("侵犯") || normalise("9",to_entity1.getString("text")).equals("异常")) {
                                if (pos1.equals("1")) {
                                    liver.setSolidMassContactDegree(">180");
                                }
                                else {
                                    liver.setSolidMassContactDegree("<=180");
                                }

                            }
                        }
                        else if (to_entity1.getString("categoryId").equals("0")) {//属性
                            if (normalise("0",to_entity1.getString("text")).equals("边界")) { //边界模糊
                                for (Iterator<Object> iterator = relations.iterator();iterator.hasNext();) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if (from2.equals(to1)) {
                                        if (to_entity2.getString("categoryId").equals("12")) { //值
                                            if (normalise("12",to_entity2.getString("text")).equals("模糊")) {
                                                if (pos1.equals("1") && pos2.equals("1")) {
                                                    liver.setSolidMassBoundariesBlurred(">180");
                                                }
                                                else {
                                                    liver.setSolidMassBoundariesBlurred("<=180");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            else if (normalise("0",to_entity1.getString("text")).equals("接触面")) { //接触面呈条状
                                for (Iterator<Object> iterator = relations.iterator();iterator.hasNext();) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if (from2.equals(to1)) {
                                        if (to_entity2.getString("categoryId").equals("12")) { //值
                                            if (normalise("12",to_entity2.getString("text")).equals("条状")) {
                                                if (pos1.equals("1") && pos2.equals("1")) {
                                                    liver.setSolidMassBoundariesBlurred(">180");
                                                }
                                                else {
                                                    liver.setSolidMassBoundariesBlurred("<=180");
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            else if (normalise("0",to_entity1.getString("text")).equals("轮廓")) { //轮廓不规则
                                for (Iterator<Object> iterator = relations.iterator();iterator.hasNext();) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if (from2.equals(to1)) {
                                        if (to_entity2.getString("categoryId").equals("12")) { //值
                                            if (normalise("12",to_entity2.getString("text")).equals("不规则")) {
                                                if (pos1.equals("1") && pos2.equals("1")) {
                                                    liver.setArteryBloodVesselStenosis(true);
                                                }
                                                else {
                                                    liver.setArteryBloodVesselStenosis(false);
                                                }
                                            }
                                            else if (normalise("12",to_entity2.getString("text")).equals("规则")) {
                                                if (pos1.equals("0") || pos2.equals("0")) {
                                                    liver.setArteryBloodVesselStenosis(true);
                                                }
                                                else {
                                                    liver.setArteryBloodVesselStenosis(false);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if (normalise("7",from_entity1.getString("text")).equals("门静脉")) { //判断门静脉
                        System.out.println("有门静脉");
                        door.setName("门静脉");
                        if (to_entity1.getString("categoryId").equals("9")) { //有症状
                            if (normalise("9",to_entity1.getString("text")).equals("侵犯") || normalise("9",to_entity1.getString("text")).equals("异常")){
                                if (pos1.equals("1")) {
                                    door.setSolidMassContactDegree(">180");
                                }
                                else {
                                    door.setSolidMassContactDegree("<=180");
                                }
                            }
                        }
                        else if (to_entity1.getString("categoryId").equals("12") ) { //值
                            if (normalise("12",to_entity1.getString("text")).equals("侵犯") || normalise("9",to_entity1.getString("text")).equals("异常")) {
                                if (pos1.equals("1")) {
                                    door.setSolidMassContactDegree(">180");
                                }
                                else {
                                    door.setSolidMassContactDegree("<=180");
                                }

                            }
                        }
                        else if (to_entity1.getString("categoryId").equals("0")) {//属性
                            if (normalise("0",to_entity1.getString("text")).equals("边界")) { //边界模糊
                                for (Iterator<Object> iterator = relations.iterator();iterator.hasNext();) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if (from2.equals(to1)) {
                                        if (to_entity2.getString("categoryId").equals("12")) { //值
                                            if (normalise("12",to_entity2.getString("text")).equals("模糊")) {
                                                if (pos1.equals("1") && pos2.equals("1")) {
                                                    door.setSolidMassBoundariesBlurred(">180");
                                                }
                                                else {
                                                    door.setSolidMassBoundariesBlurred("<=180");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            else if (normalise("0",to_entity1.getString("text")).equals("接触面")) { //接触面呈条状
                                for (Iterator<Object> iterator = relations.iterator();iterator.hasNext();) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if (from2.equals(to1)) {
                                        if (to_entity2.getString("categoryId").equals("12")) { //值
                                            if (normalise("12",to_entity2.getString("text")).equals("条状")) {
                                                if (pos1.equals("1") && pos2.equals("1")) {
                                                    door.setSolidMassBoundariesBlurred(">180");
                                                }
                                                else {
                                                    door.setSolidMassBoundariesBlurred("<=180");
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            else if (normalise("0",to_entity1.getString("text")).equals("轮廓")) { //轮廓不规则
                                for (Iterator<Object> iterator = relations.iterator();iterator.hasNext();) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if (from2.equals(to1)) {
                                        if (to_entity2.getString("categoryId").equals("12")) { //值
                                            if (normalise("12",to_entity2.getString("text")).equals("不规则")) {
                                                if (pos1.equals("1") && pos2.equals("1")) {
                                                    door.setArteryBloodVesselStenosis(true);
                                                }
                                                else {
                                                    door.setArteryBloodVesselStenosis(false);
                                                }
                                            }
                                            else if (normalise("12",to_entity2.getString("text")).equals("规则")) {
                                                if (pos1.equals("0") || pos2.equals("0")) {
                                                    door.setArteryBloodVesselStenosis(true);
                                                }
                                                else {
                                                    door.setArteryBloodVesselStenosis(false);
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }
                        else if (to_entity1.getString("categoryId").equals("7") && pos1.equals("1")) {
                            if (normalise("7",to_entity1.getString("text")).equals("癌栓")) {
                                cancerHydrant+="门静脉";
                            }
                        }
                    }
                    else if (normalise("7",from_entity1.getString("text")).contains("上静脉")) { //判断肠系膜上静脉
                        System.out.println("有肠系膜上静脉");
                        mesenteryVein.setName("肠系膜上静脉");
                        if (to_entity1.getString("categoryId").equals("9")) { //有症状
                            if (normalise("9",to_entity1.getString("text")).equals("侵犯") || normalise("9",to_entity1.getString("text")).equals("异常") ){
                                if (pos1.equals("1")) {
                                    mesenteryVein.setSolidMassContactDegree(">180");
                                }
                                else {
                                    mesenteryVein.setSolidMassContactDegree("<=180");
                                }
                            }
                            if (normalise("9",to_entity1.getString("text")).equals("侵犯")){
                                for (Iterator<Object> iterator = relations.iterator();iterator.hasNext();) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if (to2.equals(to1)) { //找到目标症状且位于 to
                                        if (from_entity2.getString("categoryId").equals("7")) { //如果是部位
                                            if (normalise("7",from_entity2.getString("text")).equals("空肠引流支")) { //再找部位
                                                if (pos1.equals("1")&&pos2.equals("1")) {
                                                    mesenteryVein.setMesenteryVeinInvaded(true);//侵犯肠系膜上静脉空肠引流支
                                                }
                                                else {
                                                    mesenteryVein.setMesenteryVeinInvaded(false);
                                                }
                                            }

                                        }
                                    }
                                }
                            }
                        }
                        else if (to_entity1.getString("categoryId").equals("12")) { //值
                            if (normalise("12",to_entity1.getString("text")).equals("侵犯") || normalise("9",to_entity1.getString("text")).equals("异常")) {
                                if (pos1.equals("1")) {
                                    mesenteryVein.setSolidMassContactDegree(">180");
                                }
                                else {
                                    mesenteryVein.setSolidMassContactDegree("<=180");
                                }


                            }
                        }
                        else if (to_entity1.getString("categoryId").equals("0")) {//属性
                            if (normalise("0", to_entity1.getString("text")).equals("边界")) { //边界模糊
                                for (Iterator<Object> iterator = relations.iterator(); iterator.hasNext(); ) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if (from2.equals(to1)) {
                                        if (to_entity2.getString("categoryId").equals("12")) { //值
                                            if (normalise("12", to_entity2.getString("text")).equals("模糊")) {
                                                if (pos1.equals("1") && pos2.equals("1")) {
                                                    mesenteryVein.setSolidMassBoundariesBlurred(">180");
                                                }
                                                else {
                                                    mesenteryVein.setSolidMassBoundariesBlurred("<=180");
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (normalise("0", to_entity1.getString("text")).equals("接触面")) { //接触面呈条状
                                for (Iterator<Object> iterator = relations.iterator(); iterator.hasNext(); ) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if (from2.equals(to1)) {
                                        if (to_entity2.getString("categoryId").equals("12")) { //值
                                            if (normalise("12", to_entity2.getString("text")).equals("条状")) {
                                                if (pos1.equals("1") && pos2.equals("1")) {
                                                    mesenteryVein.setSolidMassBoundariesBlurred(">180");
                                                }
                                                else {
                                                    mesenteryVein.setSolidMassBoundariesBlurred("<=>180");
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (normalise("0", to_entity1.getString("text")).equals("轮廓")) { //轮廓不规则
                                for (Iterator<Object> iterator = relations.iterator(); iterator.hasNext(); ) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if (from2.equals(to1)) {
                                        if (to_entity2.getString("categoryId").equals("12")) { //值
                                            if (normalise("12", to_entity2.getString("text")).equals("不规则")) {
                                                if (pos1.equals("1") && pos2.equals("1")) {
                                                    mesenteryVein.setArteryBloodVesselStenosis(true);
                                                }
                                                else {
                                                    mesenteryVein.setArteryBloodVesselStenosis(false);
                                                }
                                            } else if (normalise("12", to_entity2.getString("text")).equals("规则")) {
                                                if (pos1.equals("0") || pos2.equals("0")) {
                                                    mesenteryVein.setArteryBloodVesselStenosis(true);
                                                }
                                                else {
                                                    mesenteryVein.setArteryBloodVesselStenosis(false);
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    else if (to_entity1.getString("categoryId").equals("7") && pos1.equals("1")) {
                            if (normalise("7",to_entity1.getString("text")).equals("癌栓")) {
                                cancerHydrant+="肠系膜上静脉";
                            }
                        }
                    }
                    else if (normalise("7",from_entity1.getString("text")).equals("癌栓")) { //判断癌栓
                        if (to_entity1.getString("categoryId").equals("7")  && pos1.equals("1")) {
                            if (normalise("7",to_entity1.getString("text")).equals("门静脉")) {

                                cancerHydrant+="门静脉,";
                            }
                            else if(normalise("7",to_entity1.getString("text")).equals("上静脉")) {
                                cancerHydrant+="肠系膜上静脉,";

                            }
                            else if(normalise("7",to_entity1.getString("text")).equals("脾静脉")) {
                                cancerHydrant+="脾静脉,";

                            }
                        }
                    }
                    else if (normalise("7",from_entity1.getString("text")).equals("侧支")) { //判断侧支循环
                        if (to_entity1.getString("categoryId").equals("9")) {
                            if (normalise("9",to_entity1.getString("text")).equals("循环")) {
                                for (Iterator<Object> iterator = relations.iterator();iterator.hasNext();) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if(to2.equals(to1)) {
                                        if (from_entity2.getString("categoryId").equals("7")) {
                                            if (pos1.equals("1")&&pos2.equals("1")) {
                                                if (normalise("7", from_entity2.getString("text")).equals("肝门")) {

                                                    veinSideLoop+="肝门,";
                                                }
                                                else if (normalise("7", from_entity2.getString("text")).equals("胰头")) {

                                                    veinSideLoop+="胰头周围,";
                                                }
                                                else if (normalise("7", from_entity2.getString("text")).equals("肠系膜根部")) {

                                                    veinSideLoop+="肠系膜根部,";
                                                }
                                                else if (normalise("7", from_entity2.getString("text")).equals("左上腹")) {

                                                    veinSideLoop+="左上腹,";
                                                }
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if (normalise("7",from_entity1.getString("text")).equals("肝脏")) {
                        if (to_entity1.getString("categoryId").equals("9") ) { //有症状即病变
                            if (pos1.equals("1")) {
                                otherTemplate.setLiverLesions(true);
                            }
                            else {
                                otherTemplate.setLiverLesions(false);
                            }
                        }
                    }
                    else if (normalise("7",from_entity1.getString("text")).equals("腹膜")) {
                        if (to_entity1.getString("categoryId").equals("9")  && normalise("9",to_entity1.getString("text")).equals("结节")) { //腹膜结节
                            if (pos1.equals("1")) {
                                otherTemplate.setPeritoneum(true);
                            }
                            else {
                                otherTemplate.setPeritoneum(false);
                            }
                        }
                    }
                    else if (normalise("7",from_entity1.getString("text")).equals("网膜")) {
                        if (to_entity1.getString("categoryId").equals("9") && normalise("9",to_entity1.getString("text")).equals("结节")) { //网膜结节
                            if (pos1.equals("1")) {
                                otherTemplate.setPeritoneum(true);
                            }
                           else {
                               otherTemplate.setPeritoneum(false);
                            }
                        }
                    }
                    else if (normalise("7",from_entity1.getString("text")).equals("胰周血管")) {
                        if (to_entity1.getString("categoryId").equals("9") ) { //有症状即侵犯
                            if (pos1.equals("1")) {
                                otherTemplate.setPancreasAbnormal(true);
                            }
                            else {
                                otherTemplate.setPancreasAbnormal(false);
                            }
                        }
                    }
                    else if (normalise("7",from_entity1.getString("text")).equals("胰周脂肪")) {

                        if (to_entity1.getString("categoryId").equals("9") ) { //有症状即侵犯
                            if (pos1.equals("1")) {
                                otherTemplate.setPancreasAbnormal(true);
                            }
                            else {
                                otherTemplate.setPancreasAbnormal(false);
                            }
                        }

                        else if (to_entity1.getString("categoryId").equals("0")) { //找属性
                            System.out.println(to_entity1.getString("text"));
                            if (normalise("0",to_entity1.getString("text")).equals("间隙")) {
                                for (Iterator<Object> iterator = relations.iterator();iterator.hasNext();) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if (to1.equals(from2)) {
                                        if (to_entity2.getString("categoryId").equals("12")) {
                                            if (pos1.equals("1")&&pos2.equals("1")) {
                                                peripancreatic.setFatGap(normalise("12",to_entity2.getString("text")));
                                            }
                                            else {
                                                peripancreatic.setFatGap("无"+normalise("12",to_entity2.getString("text")));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if (normalise("7",from_entity1.getString("text")).equals("胰周间隙")) {
                        if (to_entity1.getString("categoryId").equals("9")) { //有症状即侵犯
                            if (pos1.equals("1")) {
                                otherTemplate.setPancreasAbnormal(true);
                            }
                            else {
                                otherTemplate.setPancreasAbnormal(false);
                            }
                        }
                    }
                    else if (normalise("7",from_entity1.getString("text")).equals("淋巴结")) {
                        if (to_entity1.getString("categoryId").equals("7") && pos1.equals("1")) { //查看表中部位
                            if (normalise("7",to_entity1.getString("text")).equals("胰十二指肠前")) {
                                lymph+="胰十二指肠前,";
                            }
                            else if (normalise("7",to_entity1.getString("text")).equals("胰十二指肠后")) {
                                lymph+="胰十二指肠后,";
                            }
                            else if (normalise("7",to_entity1.getString("text")).equals("腹腔动脉干")) {
                                lymph+="腹腔动脉干周围,";
                            }
                            else if (normalise("7",to_entity1.getString("text")).equals("上动脉")) {
                                lymph+="肠系膜上动脉周围,";
                            }
                            else if (normalise("7",to_entity1.getString("text")).equals("主动脉")) {
                                lymph+="主动脉周围,";
                            }
                            else {
                                lymph+="其他部位可疑淋巴结";
                            }
                        }
                    }
                    else if (normalise("7",from_entity1.getString("text")).equals("血管")) {
                        if (to_entity1.getString("categoryId").equals("9")) { //有症状
                            if (normalise("9", to_entity1.getString("text")).equals("侵犯") || normalise("9", to_entity1.getString("text")).equals("异常")) {
                                if (pos1.equals("1")) {
                                    arteryVariation.setSolidMassContactDegree(">180");
                                } else {
                                    arteryVariation.setSolidMassContactDegree("<=180");
                                }
                            }
                            else if (normalise("9",to_entity1.getString("text")).equals("接触")) {
                                if (pos1.equals("1")) {
                                    arteryVariation.setVariableVascularContact(true);
                                }
                                else {
                                    arteryVariation.setVariableVascularContact(false);
                                }
                            }
                        }
                        else if (to_entity1.getString("categoryId").equals("12")) { //值
                            if (normalise("12",to_entity1.getString("text")).equals("侵犯") || normalise("9",to_entity1.getString("text")).equals("异常")) {
                                if (pos1.equals("1")) {
                                    arteryVariation.setSolidMassContactDegree(">180");
                                }
                                else {
                                    arteryVariation.setSolidMassContactDegree("<=180");
                                }

                            }
                        }
                        else if (to_entity1.getString("categoryId").equals("0")) {//属性
                            if (normalise("0",to_entity1.getString("text")).equals("边界")) { //边界模糊
                                for (Iterator<Object> iterator = relations.iterator();iterator.hasNext();) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if (from2.equals(to1)) {
                                        if (to_entity2.getString("categoryId").equals("12")) { //值
                                            if (normalise("12",to_entity2.getString("text")).equals("模糊")) {
                                                if (pos1.equals("1") && pos2.equals("1")) {
                                                    arteryVariation.setSolidMassBoundariesBlurred(">180");
                                                }
                                                else {
                                                    arteryVariation.setSolidMassBoundariesBlurred("<=180");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            else if (normalise("0",to_entity1.getString("text")).equals("接触面")) { //接触面呈条状
                                for (Iterator<Object> iterator = relations.iterator();iterator.hasNext();) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if (from2.equals(to1)) {
                                        if (to_entity2.getString("categoryId").equals("12")) { //值
                                            if (normalise("12",to_entity2.getString("text")).equals("条状")) {
                                                if (pos1.equals("1") && pos2.equals("1")) {
                                                    arteryVariation.setSolidMassBoundariesBlurred(">180");
                                                }
                                                else {
                                                    arteryVariation.setSolidMassBoundariesBlurred("<=180");
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            else if (normalise("0",to_entity1.getString("text")).equals("轮廓")) { //轮廓不规则
                                for (Iterator<Object> iterator = relations.iterator();iterator.hasNext();) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if (from2.equals(to1)) {
                                        if (to_entity2.getString("categoryId").equals("12")) { //值
                                            if (normalise("12",to_entity2.getString("text")).equals("不规则")) {
                                                if (pos1.equals("1") && pos2.equals("1")) {
                                                    arteryVariation.setArteryBloodVesselStenosis(true);
                                                }
                                                else {
                                                    arteryVariation.setArteryBloodVesselStenosis(false);
                                                }
                                            }
                                            else if (normalise("12",to_entity2.getString("text")).equals("规则")) {
                                                if (pos1.equals("0") || pos2.equals("0")) {
                                                    arteryVariation.setArteryBloodVesselStenosis(true);
                                                }
                                                else {
                                                    arteryVariation.setArteryBloodVesselStenosis(false);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if (normalise("7",from_entity1.getString("text")).equals("副肝右动脉")) {
                        if (to_entity1.getString("categoryId").equals("9")) {
                            if (normalise("9",to_entity1.getString("text")).equals("变异")) {
                                if (pos1.equals("1")) {
                                    arteryVariation.setArteryVariation("副肝右动脉");
                                }
                            }
                        }
                        else if (to_entity1.getString("categoryId").equals("12")) {
                            if (normalise("12",to_entity1.getString("text")).equals("变异")) {
                                if (pos1.equals("1")) {
                                    arteryVariation.setArteryVariation("副肝右动脉");
                                }
                            }
                        }
                    }
                    else if (normalise("7",from_entity1.getString("text")).equals("异味肝右动脉")) {
                        if (to_entity1.getString("categoryId").equals("9")) {
                            if (normalise("9",to_entity1.getString("text")).equals("变异")) {
                                if (pos1.equals("1")) {
                                    arteryVariation.setArteryVariation("异味肝右动脉");
                                }
                            }
                        }
                        else if (to_entity1.getString("categoryId").equals("12")) {
                            if (normalise("12",to_entity1.getString("text")).equals("变异")) {
                                if (pos1.equals("1")) {
                                    arteryVariation.setArteryVariation("异味肝右动脉");
                                }
                            }
                        }
                    }
                    else if (normalise("7",from_entity1.getString("text")).equals("异味肝总动脉")) {
                        if (to_entity1.getString("categoryId").equals("9")) {
                            if (normalise("9",to_entity1.getString("text")).equals("变异")) {
                                if (pos1.equals("1")) {
                                    arteryVariation.setArteryVariation("异味肝总动脉");
                                }
                            }
                        }
                        else if (to_entity1.getString("categoryId").equals("12")) {
                            if (normalise("12",to_entity1.getString("text")).equals("变异")) {
                                if (pos1.equals("1")) {
                                    arteryVariation.setArteryVariation("异味肝总动脉");
                                }
                            }
                        }
                    }
                    if (normalise("7",from_entity1.getString("text")).contains("胰")) { //判断胰腺实质
                        if (to_entity1.getString("categoryId").equals("9")) {//症状
                            if (normalise("9", to_entity1.getString("text")).equals("异常密度影")) {
                                System.out.println("有结节影");
                                position = normalise("7",from_entity1.getString("text"));//有结节影且部位包含“胰”
                                for (Iterator<Object> iterator = relations.iterator(); iterator.hasNext(); ) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if (from2.equals(to1)) { //找到目标实体
                                        if (to_entity2.getString("categoryId").equals("0")) { //属性
                                            if (normalise("0", to_entity2.getString("text")).equals("密度")) {
                                                for (Iterator<Object> iterator1 = relations.iterator();iterator1.hasNext();) {
                                                    JSONObject jsonObject3 = (JSONObject) iterator1.next();
                                                    String from3 = jsonObject3.getString("fromId");
                                                    String to3 = jsonObject3.getString("toId");
                                                    String pos3 = jsonObject3.getString("categoryId");
                                                    JSONObject from_entity3 = (JSONObject) entities.get(Integer.valueOf(from3));
                                                    JSONObject to_entity3 = (JSONObject) entities.get(Integer.valueOf(to3));
                                                    if (from3.equals(to2)) {
                                                        if (to_entity3.getString("categoryId").equals("12")) { //值
                                                            String pos="有";
                                                            if (pos1.equals("0") || pos2.equals("0") || pos3.equals("0")) {
                                                                pos="无";
                                                            }
                                                            density = pos + normalise("12", to_entity3.getString("text"));
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                            else if (normalise("0", to_entity2.getString("text")).equals("大小")) {
                                                for (Iterator<Object> iterator1 = relations.iterator();iterator1.hasNext();) {
                                                    JSONObject jsonObject3 = (JSONObject) iterator1.next();
                                                    String from3 = jsonObject3.getString("fromId");
                                                    String to3 = jsonObject3.getString("toId");
                                                    String pos3 = jsonObject3.getString("categoryId");
                                                    JSONObject from_entity3 = (JSONObject) entities.get(Integer.valueOf(from3));
                                                    JSONObject to_entity3 = (JSONObject) entities.get(Integer.valueOf(to3));
                                                    if (from3.equals(to2)) {
                                                        if (to_entity3.getString("categoryId").equals("12")) { //值
                                                            String pos="有";
                                                            if (pos1.equals("0") || pos2.equals("0") || pos3.equals("0")) {
                                                                pos="无";
                                                            }
                                                            size = pos + normalise("12", to_entity3.getString("text"));
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                    }
                                }
                            }
                        }
                        String part = normalise("7",from_entity1.getString("text"));
                        if (!part.equals("胰头") && !part.equals("胰颈") && !part.equals("胰体") && !part.equals("胰尾")) {
                            if (to_entity1.getString("categoryId").equals("9")) { //症状
                                if (normalise("9",to_entity1.getString("text")).equals("增大")) {
                                    if (pos1.equals("1")) {
                                        increase="弥漫性增大";
                                    }
                                }
                            }
                            else if (to_entity1.getString("categoryId").equals("12")) { //值
                                if (normalise("12",to_entity1.getString("text")).equals("增大")) {
                                    if (pos1.equals("1")) {
                                        increase="弥漫性增大";
                                    }
                                }
                            }
                        }
                    }
                }
                if (to_entity1.getString("categoryId").equals("9")) {
                    if (normalise("9",to_entity1.getString("text")).equals("腹水")) {
                        if (pos1.equals("1")) {
                            otherTemplate.setAscites(true);
                        }
                        else {
                            otherTemplate.setAscites(false);
                        }
                    }
                }
                if (to_entity1.getString("categoryId").equals("7")) {
                    if (normalise("7",to_entity1.getString("text")).equals("淋巴结")) {
                        if (from_entity1.getString("categoryId").equals("7") && pos1.equals("1")) {
                            if (normalise("7",from_entity1.getString("text")).equals("胰十二指肠前")) {
                                lymph+="胰十二指肠前,";
                            }
                            else if (normalise("7",from_entity1.getString("text")).equals("胰十二指肠后")) {
                                lymph+="胰十二指肠后,";
                            }
                            else if (normalise("7",from_entity1.getString("text")).equals("腹腔动脉干")) {
                                lymph+="腹腔动脉干周围,";
                            }
                            else if (normalise("7",from_entity1.getString("text")).equals("上动脉")) {
                                lymph+="肠系膜上动脉周围,";
                            }
                            else if (normalise("7",from_entity1.getString("text")).equals("主动脉")) {
                                lymph+="主动脉周围,";
                            }
                            else {
                                lymph+="其他部位可疑淋巴结,";
                            }
                        }
                    }
                    else if(normalise("7",to_entity1.getString("text")).equals("上动脉")) {
                        if (from_entity1.getString("categoryId").equals("7")) {
                            if (normalise("7",from_entity1.getString("text")).equals("血管")) {
                                for (Iterator<Object> iterator = relations.iterator();iterator.hasNext();) {
                                    JSONObject jsonObject2 = (JSONObject) iterator.next();
                                    String from2 = jsonObject2.getString("fromId");
                                    String to2 = jsonObject2.getString("toId");
                                    String pos2 = jsonObject2.getString("categoryId");
                                    JSONObject from_entity2 = (JSONObject) entities.get(Integer.valueOf(from2));
                                    JSONObject to_entity2 = (JSONObject) entities.get(Integer.valueOf(to2));
                                    if (from1.equals(from2)) {
                                        if (to_entity2.getString("categoryId").equals("9")) {
                                            if (normalise("9",to_entity2.getString("text")).equals("狭窄")) {
                                                if (pos1.equals("1")&&pos2.equals("1")) {
                                                    mesentery.setArteryBloodVesselStenosis(true);
                                                }
                                                else {
                                                    mesentery.setArteryBloodVesselStenosis(false);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if(kuozhang==true&&jieduan==true) {
                parenchyma.setSymptom2(true);
            }
            else {
                parenchyma.setSymptom2(false);
            }
            //胰腺
            pancreas.setDensity(pancreasDensity);
            pancreas.setMargin(margin);
            pancreas.setIncreaseNature(increase);
            //其他评价
            otherTemplate.setLymph(lymph);
            //动脉评价
            artery.setCeliac(cavity);
            artery.setLiver(liver);
            artery.setMesentery(mesentery);
            artery.setVariation(arteryVariation);
            //静脉评价
            vein.setMesenteryVein(mesenteryVein);
            vein.setCancerHydrant(cancerHydrant);
            vein.setDoor(door);
            vein.setVeinSideLoop(veinSideLoop);
            //胰腺实质
            parenchyma.setDensity(density);
            parenchyma.setSize(size);
            parenchyma.setPosition(position);

            findingTemplate.setPancreas(pancreas);
            findingTemplate.setPeripancreatic(peripancreatic);
            findingTemplate.setParenchyma(parenchyma);
            findingTemplate.setArtery(artery);
            findingTemplate.setOther(otherTemplate);
            findingTemplate.setVein(vein);
            int score=100;
            List<Map<Integer,String>> shortcut = new ArrayList<>();
            //打分
            //胰腺
            if (pancreas.isDensityShadowUnusual==null) {
                Map<Integer,String> map = new HashMap<>();
                map.put(StandardEnum.PANCREAS_ERROR.getCode(), "平扫后异常密度影项缺失");
                shortcut.add(map);
                score-=5;
            }
            if (pancreas.isStrengtheningStoveUnusual==null) {
                Map<Integer,String> map = new HashMap<>();
                map.put(StandardEnum.PANCREAS_ERROR.getCode(), "增强后异常强化灶项缺失");
                shortcut.add(map);
                score-=5;
            }
            if (pancreas.density.isEmpty()) {
                Map<Integer,String> map = new HashMap<>();
                map.put(StandardEnum.PANCREAS_ERROR.getCode(), "密度项缺失");
                shortcut.add(map);
                score-=5;
            }
            if (pancreas.shape==null) {
                Map<Integer,String> map = new HashMap<>();
                map.put(StandardEnum.PANCREAS_ERROR.getCode(), "形态项缺失");
                shortcut.add(map);
                score-=5;
            }
            if (pancreas.margin.isEmpty()) {
                Map<Integer,String> map = new HashMap<>();
                map.put(StandardEnum.PANCREAS_ERROR.getCode(), "边缘项缺失");
                shortcut.add(map);
                score-=5;
            }
            if (pancreas.increaseNature.isEmpty()) {
                Map<Integer,String> map = new HashMap<>();
                map.put(StandardEnum.PANCREAS_ERROR.getCode(), "形态增大性质项缺失");
                shortcut.add(map);
                score-=5;
            }
            //胰周
            if (peripancreatic.change==null) {
                Map<Integer,String> map = new HashMap<>();
                map.put(StandardEnum.PANCREAS_ERROR.getCode(), "胰周改变项缺失");
                shortcut.add(map);
                score-=5;
            }
            if (peripancreatic.isEffusion==null) {
                Map<Integer,String> map = new HashMap<>();
                map.put(StandardEnum.PANCREAS_ERROR.getCode(), "胰周积液项缺失");
                shortcut.add(map);
                score-=5;
            }
            if (peripancreatic.fatGap==null) {
                Map<Integer,String> map = new HashMap<>();
                map.put(StandardEnum.PANCREAS_ERROR.getCode(), "胰周脂肪间隙项缺失");
                shortcut.add(map);
                score-=5;
            }
            //实质
            if (parenchyma.density==null) {
                Map<Integer,String> map = new HashMap<>();
                map.put(StandardEnum.PANCREAS_ERROR.getCode(), "胰腺实质密度项缺失");
                shortcut.add(map);
                score-=5;
            }
            if (parenchyma.size==null) {
                Map<Integer,String> map = new HashMap<>();
                map.put(StandardEnum.PANCREAS_ERROR.getCode(), "胰腺实质大小项缺失");
                shortcut.add(map);
                score-=5;
            }
            if (parenchyma.position.isEmpty()) {
                Map<Integer,String> map = new HashMap<>();
                map.put(StandardEnum.PANCREAS_ERROR.getCode(), "胰腺实质位置项缺失");
                shortcut.add(map);
                score-=5;
            }
            if (parenchyma.symptom1==null) {
                Map<Integer,String> map = new HashMap<>();
                map.put(StandardEnum.PANCREAS_ERROR.getCode(), "胰管狭窄项缺失");
                shortcut.add(map);
                score-=5;
            }
            if (parenchyma.symptom2==null) {
                Map<Integer,String> map = new HashMap<>();
                map.put(StandardEnum.PANCREAS_ERROR.getCode(), "胆道系统截断项缺失");
                shortcut.add(map);
                score-=5;
            }

            if (mesentery.name!=null) {
                if (mesentery.SolidMassContactDegree==null) {
                    Map<Integer,String> map = new HashMap<>();
                    map.put(StandardEnum.ARTERY_ERROR.getCode(), "肠系膜上动脉实性肿块接触程度项缺失");
                    shortcut.add(map);
                    score-=1;
                }
                else if (mesentery.SolidMassBoundariesBlurred==null) {
                    Map<Integer,String> map = new HashMap<>();
                    map.put(StandardEnum.ARTERY_ERROR.getCode(), "肠系膜上动脉实性肿块边界模糊项缺失");
                    shortcut.add(map);
                    score-=1;
                }
                else if (mesentery.isArteryBloodVesselStenosis==null) {
                    Map<Integer,String> map = new HashMap<>();
                    map.put(StandardEnum.ARTERY_ERROR.getCode(), "肠系膜上动脉局部血管狭窄项缺失");
                    shortcut.add(map);
                    score-=1;
                }
                else if (mesentery.isArteryIntestinalInvaded==null) {
                    Map<Integer,String> map = new HashMap<>();
                    map.put(StandardEnum.ARTERY_ERROR.getCode(), "肠系膜上动脉侵犯肠系膜上动脉第一分支项缺失");
                    shortcut.add(map);
                    score-=1;
                }
            }
            //腹腔干
            if (cavity.name!=null) {
                if (cavity.SolidMassContactDegree==null) {
                    Map<Integer,String> map = new HashMap<>();
                    map.put(StandardEnum.ARTERY_ERROR.getCode(), "腹腔干动脉实性肿块接触程度项缺失");
                    shortcut.add(map);
                    score-=1;
                }
                else if (cavity.SolidMassBoundariesBlurred==null) {
                    Map<Integer,String> map = new HashMap<>();
                    map.put(StandardEnum.ARTERY_ERROR.getCode(), "腹腔干动脉实性肿块边界模糊项缺失");
                    shortcut.add(map);
                    score-=1;
                }
                else if (cavity.isArteryBloodVesselStenosis==null) {
                    Map<Integer,String> map = new HashMap<>();
                    map.put(StandardEnum.ARTERY_ERROR.getCode(), "腹腔干动脉局部血管狭窄项缺失");
                    shortcut.add(map);
                    score-=1;
                }
            }
            //肝总动脉
            if (liver.name!=null) {
                if (liver.SolidMassContactDegree==null) {
                    Map<Integer,String> map = new HashMap<>();
                    map.put(StandardEnum.ARTERY_ERROR.getCode(), "肝总动脉实性肿块接触程度项缺失");
                    shortcut.add(map);
                    score-=1;
                }
                else if (liver.SolidMassBoundariesBlurred==null) {
                    Map<Integer,String> map = new HashMap<>();
                    map.put(StandardEnum.ARTERY_ERROR.getCode(), "肝总动脉实性肿块边界模糊项缺失");
                    shortcut.add(map);
                    score-=1;
                }
                else if (liver.isArteryBloodVesselStenosis==null) {
                    Map<Integer,String> map = new HashMap<>();
                    map.put(StandardEnum.ARTERY_ERROR.getCode(), "肝总动脉局部血管狭窄项缺失");
                    shortcut.add(map);
                    score-=1;
                }
                else if (liver.isCeliacArteryInvaded==null) {
                    Map<Integer,String> map = new HashMap<>();
                    map.put(StandardEnum.ARTERY_ERROR.getCode(), "肝总动脉侵犯腹腔动脉干项缺失");
                    shortcut.add(map);
                    score-=1;
                }
                else if (liver.isHepaticArteryInvaded==null) {
                    Map<Integer,String> map = new HashMap<>();
                    map.put(StandardEnum.ARTERY_ERROR.getCode(), "肝总动脉侵犯肝动脉分叉部项缺失");
                    shortcut.add(map);
                    score-=1;
                }
            }
            //动脉变异
            if (arteryVariation.name!=null) {
                if (arteryVariation.SolidMassContactDegree==null) {
                    Map<Integer,String> map = new HashMap<>();
                    map.put(StandardEnum.ARTERY_ERROR.getCode(), "动脉变异实性肿块接触程度项缺失");
                    shortcut.add(map);
                    score-=1;
                }
                else if (arteryVariation.SolidMassBoundariesBlurred==null) {
                    Map<Integer,String> map = new HashMap<>();
                    map.put(StandardEnum.ARTERY_ERROR.getCode(), "动脉变异实性肿块边界模糊项缺失");
                    shortcut.add(map);
                    score-=1;
                }
                else if (arteryVariation.isArteryBloodVesselStenosis==null) {
                    Map<Integer,String> map = new HashMap<>();
                    map.put(StandardEnum.ARTERY_ERROR.getCode(), "动脉变异局部血管狭窄项缺失");
                    shortcut.add(map);
                    score-=1;
                }
                else if (arteryVariation.arteryVariation==null) {
                    Map<Integer,String> map = new HashMap<>();
                    map.put(StandardEnum.ARTERY_ERROR.getCode(), "动脉变异解剖变异项缺失");
                    shortcut.add(map);
                    score-=1;
                }
                else if (arteryVariation.isVariableVascularContact==null) {
                    Map<Integer,String> map = new HashMap<>();
                    map.put(StandardEnum.ARTERY_ERROR.getCode(), "动脉变异变异血管接触项缺失");
                    shortcut.add(map);
                    score-=1;
                }
            }
            if (door.name!=null) {
                if (door.SolidMassContactDegree==null) {
                    Map<Integer,String> map = new HashMap<>();
                    map.put(StandardEnum.VEIN_ERROR.getCode(), "门静脉实性肿块接触程度项缺失");
                    shortcut.add(map);
                    score-=1;
                }
                else if (door.SolidMassBoundariesBlurred==null) {
                    Map<Integer,String> map = new HashMap<>();
                    map.put(StandardEnum.VEIN_ERROR.getCode(), "门静脉实性肿块边界模糊项缺失");
                    shortcut.add(map);
                    score-=1;
                }
                else if (door.isArteryBloodVesselStenosis==null) {
                    Map<Integer,String> map = new HashMap<>();
                    map.put(StandardEnum.VEIN_ERROR.getCode(), "门静脉局部血管狭窄项缺失");
                    shortcut.add(map);
                    score-=1;
                }
            }
            //肠系膜上静脉
            if (mesenteryVein.name!=null) {
                if (mesenteryVein.SolidMassContactDegree==null) {
                    Map<Integer,String> map = new HashMap<>();
                    map.put(StandardEnum.VEIN_ERROR.getCode(), "肠系膜上静脉实性肿块接触程度项缺失");
                    shortcut.add(map);
                    score-=1;
                }
                else if (mesenteryVein.SolidMassBoundariesBlurred==null) {
                    Map<Integer,String> map = new HashMap<>();
                    map.put(StandardEnum.VEIN_ERROR.getCode(), "肠系膜上静脉实性肿块边界模糊项缺失");
                    shortcut.add(map);
                    score-=1;
                }
                else if (mesenteryVein.isArteryBloodVesselStenosis==null) {
                    Map<Integer,String> map = new HashMap<>();
                    map.put(StandardEnum.VEIN_ERROR.getCode(), "肠系膜上静脉局部血管狭窄项缺失");
                    shortcut.add(map);
                    score-=1;
                }
                else if (mesenteryVein.isMesenteryVeinInvaded==null) {
                    Map<Integer,String> map = new HashMap<>();
                    map.put(StandardEnum.VEIN_ERROR.getCode(), "肠系膜上静脉侵犯肠系膜上静脉空肠引流支项缺失");
                    shortcut.add(map);
                    score-=1;
                }
            }
            if (cancerHydrant.isEmpty()) {
                Map<Integer,String> map = new HashMap<>();
                map.put(StandardEnum.VEIN_ERROR.getCode(), "静脉内栓癌项缺失");
                shortcut.add(map);
                score-=1;
            }
            if (veinSideLoop.isEmpty()) {
                Map<Integer,String> map = new HashMap<>();
                map.put(StandardEnum.VEIN_ERROR.getCode(), "静脉侧支循环项缺失");
                shortcut.add(map);
                score-=1;
            }
            //其他评价
            if (otherTemplate.liverLesions==null) {
                Map<Integer,String> map = new HashMap<>();
                map.put(StandardEnum.OTHER_ERROR.getCode(), "肝脏病变项缺失");
                shortcut.add(map);
                score-=2;
            }
            if (otherTemplate.peritoneum==null) {
                Map<Integer,String> map = new HashMap<>();
                map.put(StandardEnum.OTHER_ERROR.getCode(), "腹膜项缺失");
                shortcut.add(map);
                score-=2;
            }
            if (otherTemplate.isAscites==null) {
                Map<Integer,String> map = new HashMap<>();
                map.put(StandardEnum.OTHER_ERROR.getCode(), "腹水项缺失");
                shortcut.add(map);
                score-=2;
            }
            if (otherTemplate.isPancreasAbnormal==null) {
                Map<Integer,String> map = new HashMap<>();
                map.put(StandardEnum.OTHER_ERROR.getCode(), "胰周异常项缺失");
                shortcut.add(map);
                score-=2;
            }
            if (otherTemplate.lymph.isEmpty()) {
                Map<Integer,String> map = new HashMap<>();
                map.put(StandardEnum.OTHER_ERROR.getCode(), "可疑淋巴结项缺失");
                shortcut.add(map);
                score-=2;
            }
            if (score<0) {
                score=0;
            }
            findingTemplate.setShortcut(shortcut);
            findingTemplate.setScore(score+0.0);
            report.get().setState("已处理");
            report.get().setFindingScore(score+0.0);
            template.setFindingTemplate(findingTemplate);


            StringBuilder diagnose = new StringBuilder();
            StringBuilder examination = new StringBuilder();
            ConclusionTemplate conclusionTemplate = new ConclusionTemplate();
            //调用接口得到实体
            JSONObject apiData = apiService.getEntity(report.get().conclusion);

            JSONObject data2 = apiData.getJSONObject("entities");
            JSONArray entities2 = data2.getJSONArray("labels");

            //判断实体有无疾病诊断，检查名称
            List<Map<Integer,String>> conclusionShortcut = new ArrayList<>();
            for (Iterator<Object> it=entities2.iterator();it.hasNext();) {
                JSONObject entity = (JSONObject) it.next();
                String content=entity.getString("text");//文本
                String type=entity.getString("categoryId");//类型
                if (type.equals("2")) {//如果是疾病诊断
                    diagnose.append(normalise("2", content)).append(" ");
                }
                else if(type.equals("3")) {//检查检验
                    diagnose.append(normalise("3", content)).append(" ");
                }
            }
            conclusionTemplate.setDiagnose(diagnose.toString());
            conclusionTemplate.setExaminationName(examination.toString());
            score=100;
            if (conclusionTemplate.diagnose.isEmpty()) {
                Map<Integer,String> map = new HashMap<>();
                map.put(StandardEnum.DIAGNOSE_UNCLEAR.getCode(), StandardEnum.DIAGNOSE_UNCLEAR.getMsg());
                conclusionShortcut.add(map);
                score-=50;
            }
            if (conclusionTemplate.examinationName.isEmpty()) {

                Map<Integer,String> map = new HashMap<>();
                map.put(StandardEnum.EXAMINATION_UNRECORDED.getCode(), StandardEnum.EXAMINATION_UNRECORDED.getMsg());
                conclusionShortcut.add(map);
                score-=50;
            }
            conclusionTemplate.setShortcut(conclusionShortcut);
            conclusionTemplate.setScore(score+0.0);
            //数据库操更新
            report.get().setState("已检查");
            report.get().setConclusionScore(score+0.0);
            reportService.addReport(report.get());
            template.setConclusionTemplate(conclusionTemplate);
        }
        return ResultUtil.success(template);
    }

    //按类别归一
    public String normalise(String categoryId, String content) {
        JSONObject js = (JSONObject) normalizedJson.getJSONObject(categoryId);
        if (!js.containsKey(content)) {
            return content;
        }
        return js.getString(content);
    }



}

