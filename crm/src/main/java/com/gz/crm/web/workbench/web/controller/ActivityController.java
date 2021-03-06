package com.gz.crm.web.workbench.web.controller;

import com.gz.crm.web.commons.constant.Constant;
import com.gz.crm.web.commons.domain.ReturnObject;
import com.gz.crm.web.commons.utils.DateUtils;
import com.gz.crm.web.commons.utils.UUIDUtils;
import com.gz.crm.web.settings.domain.User;
import com.gz.crm.web.settings.service.UserService;
import com.gz.crm.web.workbench.domain.Activity;
import com.gz.crm.web.workbench.service.ActivityService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                object.setMessage("?????????????????????...");

            }
        } catch (Exception e) {
            e.printStackTrace();
            object.setCode(Constant.LOGIN_FAILURE);
            object.setMessage("?????????????????????...");

        }
        return object;
    }

    /**
     * @param name
     * @param owner
     * @param startDate
     * @param endDate
     * @param pageNo    ???????????????????????????
     * @param pageSize  ????????????????????????
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
                //????????????
                object.setCode(Constant.LOGIN_SUCCESS);
            } else {
                object.setCode(Constant.LOGIN_FAILURE);
                object.setMessage("???????????????????????????...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            object.setCode(Constant.LOGIN_FAILURE);
            object.setMessage("???????????????????????????...");
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
                //????????????
                object.setCode(Constant.LOGIN_SUCCESS);
            } else {
                object.setCode(Constant.LOGIN_FAILURE);
                object.setMessage("???????????????????????????...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            object.setCode(Constant.LOGIN_FAILURE);
            object.setMessage("???????????????????????????...");
        }
        return object;
    }

    //????????????
    @RequestMapping("/queryAllActivitys.do")
    public void queryAllActivitys(HttpServletResponse response) throws IOException {
        List<Activity> activityList = activityService.queryAllActivitys();

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("??????????????????");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("ID");
        cell = row.createCell(1);
        cell.setCellValue("?????????");
        cell = row.createCell(2);
        cell.setCellValue("??????");
        cell = row.createCell(3);
        cell.setCellValue("????????????");
        cell = row.createCell(4);
        cell.setCellValue("????????????");
        cell = row.createCell(5);
        cell.setCellValue("??????");
        cell = row.createCell(6);
        cell.setCellValue("??????");
        cell = row.createCell(7);
        cell.setCellValue("????????????");
        cell = row.createCell(8);
        cell.setCellValue("?????????");
        cell = row.createCell(9);
        cell.setCellValue("????????????");
        cell = row.createCell(10);
        cell.setCellValue("?????????");
        //????????????????????????excel????????????
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
            //???????????????????????????
            /*
            OutputStream out = new FileOutputStream("F:\\Devjava\\activityList.xls");
            wb.write(out);
            wb.close();
            */

            //????????????????????????
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=activityList.xls");
            OutputStream outputStream = response.getOutputStream();
            //??????????????????????????????????????????????????????????????????
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
}
