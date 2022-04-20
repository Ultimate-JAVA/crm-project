package com.gz.crm.web.workbench.service;

import com.gz.crm.web.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityService {

    int saveCreatActivity(Activity activity);

    List<Activity> queryActivityByConditionForPage(Map<String,Object> map);

    int queryCountByConditionForPage(Map<String,Object> map);

    int deleteActivityById(String[] id);

    Activity queryActivityById(String id);

    int editActivityById(Activity activity);

    List<Activity> queryAllActivitys();



}
