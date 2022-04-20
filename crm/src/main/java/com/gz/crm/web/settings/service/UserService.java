package com.gz.crm.web.settings.service;

import com.gz.crm.web.settings.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface UserService {
    User queryUserByLoginActAndPwd(Map<String, Object> map);

    List<User> queryAllUsers();
}
