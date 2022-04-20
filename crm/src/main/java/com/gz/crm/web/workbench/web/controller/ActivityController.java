package com.gz.crm.web.workbench.web.controller;

import com.gz.crm.web.commons.constant.Constant;
import com.gz.crm.web.commons.domain.ReturnObject;
import com.gz.crm.web.commons.utils.DateUtils;
import com.gz.crm.web.commons.utils.UUIDUtils;
import com.gz.crm.web.settings.domain.User;
import com.gz.crm.web.settings.service.UserService;
import com.gz.crm.web.workbench.domain.Activity;
import com.gz.crm.web.workbench.service.ActivityService;
import com.sun.deploy.net.HttpResponse;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;

@Controller
@RequestMapping("/workbench/activity")
public class ActivityController {

    @Resource
    private UserService userService;
    @Resource
    private ActivityService activityService;


    @RequestMapping("/index.do")
    public String index(HttpServletRequest request) {
        List<User> userList = userService.queryAllUsers();
        request.setAttribute("userList", userList);
        return "workbench/activity/index";
    }

    @RequestMapping("/saveCreateActivity.do")
    @ResponseBody
    public Object saveCreateActivity(Activity activity, HttpSession session) {
        User user = (User) session.getAttribute(Constant.SESSION_USER);
        activity.setId(UUIDUtils.getUUID());
        activity.setCreateTime(DateUtils.formatDateTime(new Date(), "yyyy-MM-dd HH:mm:ss"));
        activity.setCreateBy(user.getId());
        ReturnObject object = new ReturnObject();

        try {
            int result = activityService.saveCreatActivity(activity);
            if (result > 0) {
                object.setCode(Constant.LOGIN_SUCCESS);
            } else {
                object.setCode(Constant.LOGIN_FAILURE);
                object.setMessage("系统忙，请稍后...");

            }
        } catch (Exception e) {
            e.printStackTrace();
            object.setCode(Constant.LOGIN_FAILURE);
            object.setMessage("系统忙，请稍后...");

        }
        return object;
    }

    /**
     * @param name
     * @param owner
     * @param startDate
     * @param endDate
     * @param pageNo    获取哪某一页的数据
     * @param pageSize  每一页最大显示数
     * @return
     */
    @RequestMapping("/queryActivityByConditionForPage.do")
    public @ResponseBody
    Object queryActivityByConditionForPage(String name, String owner, String startDate, String endDate,
                                           int pageNo, int pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("owner", owner);
        map.put("startDate", startDate);
        System.out.println(startDate);

        map.put("endDate", endDate);
        map.put("beginNo", (pageNo - 1) * pageSize);
        map.put("pageSize", pageSize);

        List<Activity> activityList = activityService.queryActivityByConditionForPage(map);
        int totalRows = activityService.queryCountByConditionForPage(map);
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("activityList", activityList);
        objectMap.put("totalRows", totalRows);

        return objectMap;
    }

    @RequestMapping("/deleteActivityById.do")
    public @ResponseBody
    Object deleteActivityById(String[] id) {
        ReturnObject object = new ReturnObject();
        try {
            int result = activityService.deleteActivityById(id);
            if (result > 0) {
                //删除成功
                object.setCode(Constant.LOGIN_SUCCESS);
            } else {
                object.setCode(Constant.LOGIN_FAILURE);
                object.setMessage("系统忙，请稍后重试...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            object.setCode(Constant.LOGIN_FAILURE);
            object.setMessage("系统忙，请稍后重试...");
        }
        return object;
    }

    @RequestMapping("/queryActivityById.do")
    public @ResponseBody
    Object queryActivityById(String id) {
        Activity activity = activityService.queryActivityById(id);
        return activity;
    }


    @RequestMapping("/editActivityById.do")
    public @ResponseBody
    Object editActivityById(Activity activity, HttpSession session) {
        User user = (User) session.getAttribute(Constant.SESSION_USER);
        activity.setEditTime(DateUtils.formatDateTime(new Date(), "yyyy-MM-dd HH:mm:ss"));
        activity.setEditBy(user.getId());
        ReturnObject object = new ReturnObject();
        try {
            int result = activityService.editActivityById(activity);
            if (result > 0) {
                //修改成功
                object.setCode(Constant.LOGIN_SUCCESS);
            } else {
                object.setCode(Constant.LOGIN_FAILURE);
                object.setMessage("系统忙，请稍后重试...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            object.setCode(Constant.LOGIN_FAILURE);
            object.setMessage("系统忙，请稍后重试...");
        }
        return object;
    }

    //文件下载
    @RequestMapping("/queryAllActivitys.do")
    public void queryAllActivitys(HttpServletResponse response) throws IOException {
        List<Activity> activityList = activityService.queryAllActivitys();

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("市场活动列表");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("ID");
        cell = row.createCell(1);
        cell.setCellValue("所有者");
        cell = row.createCell(2);
        cell.setCellValue("名称");
        cell = row.createCell(3);
        cell.setCellValue("开始时间");
        cell = row.createCell(4);
        cell.setCellValue("结束时间");
        cell = row.createCell(5);
        cell.setCellValue("成本");
        cell = row.createCell(6);
        cell.setCellValue("描述");
        cell = row.createCell(7);
        cell.setCellValue("创建时间");
        cell = row.createCell(8);
        cell.setCellValue("创建者");
        cell = row.createCell(9);
        cell.setCellValue("修改时间");
        cell = row.createCell(10);
        cell.setCellValue("修改者");
        //把查询的数据写到excel表格当中
        Activity activity = null;
        if (activityList != null && activityList.size() > 0) {
            for (int i = 0; i < activityList.size(); i++) {
                activity = activityList.get(i);
                row = sheet.createRow(i + 1);
                cell = row.createCell(0);
                cell.setCellValue(activity.getId());
                cell = row.createCell(1);
                cell.setCellValue(activity.getOwner());
                cell = row.createCell(2);
                cell.setCellValue(activity.getName());
                cell = row.createCell(3);
                cell.setCellValue(activity.getStartDate());
                cell = row.createCell(4);
                cell.setCellValue(activity.getEndDate());
                cell = row.createCell(5);
                cell.setCellValue(activity.getCost());
                cell = row.createCell(6);
                cell.setCellValue(activity.getDescription());
                cell = row.createCell(7);
                cell.setCellValue(activity.getCreateTime());
                cell = row.createCell(8);
                cell.setCellValue(activity.getCreateBy());
                cell = row.createCell(9);
                cell.setCellValue(activity.getEditTime());
                cell = row.createCell(10);
                cell.setCellValue(activity.getEditBy());
            }
            //写到磁盘上效率太低
            /*
            OutputStream out = new FileOutputStream("F:\\Devjava\\activityList.xls");
            wb.write(out);
            wb.close();
            */

            //到磁盘里读取文件
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=activityList.xls");
            OutputStream outputStream = response.getOutputStream();
            //从磁盘里面读了再写到浏览器，效率太低，不使用
            /*InputStream inputStream = new FileInputStream("F:\\Devjava\\activityList.xls");
            int len = 0;
            byte[] bytes = new byte[256];
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }*/

            wb.write(outputStream);
            wb.close();
            outputStream.flush();
        }
    }
    //文件上传
    @RequestMapping("/upload.do")
    public void upload(){
        System.out.println("a1");
        System.out.println("sdfihaskdjhfasjdflasjdfsadfsa");
    }
}
