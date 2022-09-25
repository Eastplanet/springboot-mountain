package com.mountain.springbootmountain.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
@Slf4j
public class HelloController {

    @GetMapping("/")
    public String welcome(){
        log.info("home controller");
        return "hello";
    }

    @GetMapping("/callbackpos")
    public String callback(HttpSession session){
        log.info("callback controller");
        return "callback";
    }


}
