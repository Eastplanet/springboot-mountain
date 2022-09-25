package com.mountain.springbootmountain.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@Slf4j
public class HelloController {

    @GetMapping("/")
    public String welcome(){
        log.info("home controller");
        return "map";
    }

    @GetMapping("/map")
    public String map(){
        log.info("map controller");
        return "map";
    }




}
