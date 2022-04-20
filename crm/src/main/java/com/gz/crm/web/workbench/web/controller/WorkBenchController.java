package com.gz.crm.web.workbench.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/workbench")
public class WorkBenchController {

    @RequestMapping("/index.do")
    public String index(){
        return "workbench/index";
    }
}
