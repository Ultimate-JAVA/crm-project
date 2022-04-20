package com.gz.crm.web.settings.web.controller;

import com.gz.crm.web.commons.constant.Constant;
import com.gz.crm.web.commons.domain.ReturnObject;
import com.gz.crm.web.commons.utils.DateUtils;
import com.gz.crm.web.settings.domain.User;
import com.gz.crm.web.settings.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/settings/qx/user")
public class UserController {

    @Resource
    private UserService userService;
//    url写的时候要和当前这个controller方法访问的资源目录保持一致,资源名称和方法名一致
//    跳转到主页面
    @RequestMapping("/toLogin.do")
    public String toLogin() {
        return "settings/qx/user/login";
    }

    //进行登录处理
    @RequestMapping("/login.do")
    public @ResponseBody //将java对象转为json对象
    Object login(String loginAct, String loginPwd, String isRemPwd, HttpServletRequest request, HttpSession session, HttpServletResponse response) {
        Map<String,Object> map = new HashMap<>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);
        User user = userService.queryUserByLoginActAndPwd(map);
        ReturnObject object = new ReturnObject();

        if (user == null) {
            /*用户名或密码错误*/
            object.setCode(Constant.LOGIN_FAILURE);
            object.setMessage("用户名或密码错误");
        }else {
            if (DateUtils.formatDateTime(new Date(),"yyyy-MM-dd HH:mm:ss").compareTo(user.getExpireTime()) > 0) {
                /*账号已经过期*/
                object.setCode(Constant.LOGIN_FAILURE);
                object.setMessage("账号已经过期");
            } else if (!user.getAllowIps().contains(request.getRemoteAddr())) {
                /*账号ip受限制*/
                object.setCode("0");
                object.setMessage(Constant.LOGIN_FAILURE);
            } else{
                /*登录成功*/
                object.setCode(Constant.SESSION_USER);
                object.setMessage("登录成功");
                session.setAttribute(Constant.SESSION_USER,user);
                if ("true".equals(isRemPwd)) {
                  /*用户勾选了记住密码*/
                    Cookie cookie1 = new Cookie("loginAct",user.getLoginAct());
                    cookie1.setMaxAge(10*24*60*60);
                    Cookie cookie2 = new Cookie("loginPwd", user.getLoginPwd());
                    cookie2.setMaxAge(10*24*60*60);

                    response.addCookie(cookie1);
                    response.addCookie(cookie2);

                }else {
                    /*用户未勾选记住密码*/
                    Cookie cookie1 = new Cookie("loginAct","1");
                    cookie1.setMaxAge(0);
                    Cookie cookie2 = new Cookie("loginPwd","1");
                    cookie2.setMaxAge(0);

                    response.addCookie(cookie1);
                    response.addCookie(cookie2);
                }
            }
        }
        return object;
    }

    @RequestMapping("/logout.do")
    public String logout(HttpSession session,HttpServletResponse response) {
        /*安全退出*/
        session.invalidate();
        Cookie cookie1 = new Cookie("loginAct", "1");
        cookie1.setMaxAge(0);
        Cookie cookie2 = new Cookie("loginPwd","1");
        cookie2.setMaxAge(0);
        response.addCookie(cookie1);
        response.addCookie(cookie2);
        return "redirect:/";
    }
}
