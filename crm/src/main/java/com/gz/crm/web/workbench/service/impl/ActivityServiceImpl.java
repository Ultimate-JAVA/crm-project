package com.gz.crm.web.workbench.service.impl;

import com.gz.crm.web.workbench.domain.Activity;
import com.gz.crm.web.workbench.mapper.ActivityMapper;
import com.gz.crm.web.workbench.service.ActivityService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class ActivityServiceImpl implements ActivityService {
    @Resource
    private ActivityMapper activityMapper;

    @Override
    public int saveCreatActivity(Activity activity) {
        return activityMapper.insertActivity(activity);
    }

    @Override
    public List<Activity> queryActivityByConditionForPage(Map<String, Object> map) {
        return activityMapper.selectActivityByConditionForPage(map);
    }

    @Override
    public int queryCountByConditionForPage(Map<String, Object> map) {
        return activityMapper.selectCountByConditionForPage(map);
    }

    @Override
    public int deleteActivityById(String[] id) {
        return activityMapper.deleteActivityById(id);
    }

    @Override
    public Activity queryActivityById(String id) {
        return activityMapper.selectActivityById(id);
    }

    @Override
    public int editActivityById(Activity activity) {
        return activityMapper.updateActivityById(activity);
    }

    @Override
    public List<Activity> queryAllActivitys() {

        return activityMapper.selectAllActivitys();
    }
}
