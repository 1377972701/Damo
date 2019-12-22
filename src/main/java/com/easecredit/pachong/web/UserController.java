package com.easecredit.pachong.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: GaoXing
 * @Description
 * @data: Created by GaoXing on 2019/3/20.
 * @Modified By;
 */
@Controller
public class UserController {
             @RequestMapping("ttt")
            public  String   test(){

                return "index";
            }
}
