package com.mountain.springbootmountain.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class HelloController {

    @Autowired
    KaKaoService ks;

    @GetMapping("/") @ResponseBody
    public Map<String,Object> welcome(HttpServletRequest request){
        log.info("home controller");
        Map result = new HashMap<String,Object>();
        result.put("hello","World");
        result.put("test","data");
        return result;
    }

    @PostMapping("/post")
    @ResponseBody
    public Map<String,Object> welcome2(@RequestBody HashMap<String,Object> result){
        log.info("post controller");

        System.out.println("result = " + result);


        Map result2 = new HashMap<String,Object>();
        result2.put("hello","World");
        result2.put("test","data");
        return result2;
    }


    @GetMapping("/map")
    public String map(){
        log.info("map controller");
        return "map";
    }

    @GetMapping("/kakao")
    public String getCI(@RequestParam String code, Model model) throws IOException {

        log.info("kakao controller");

        System.out.println("code = " + code);
        String access_token = ks.getToken(code);
        Map<String, Object> userInfo = ks.getUserInfo(access_token);
        model.addAttribute("code", code);
        model.addAttribute("access_token", access_token);
        model.addAttribute("userInfo", userInfo);

        //ci는 비즈니스 전환후 검수신청 -> 허락받아야 수집 가능
        return "welcome";
    }




}
