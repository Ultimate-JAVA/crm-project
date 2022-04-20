package com.gz.crm.web.workbench.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/workbench/main")
public class MainController {
    @RequestMapping("/index.do")
    public String index(){
        return "workbench/main/index";
    }
}
