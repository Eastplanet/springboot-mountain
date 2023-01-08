package com.mountain.springbootmountain.web;

import com.mountain.springbootmountain.web.dto.MountainEndCount;
import com.mountain.springbootmountain.web.dto.MountainNameResDto;
import com.mountain.springbootmountain.web.dto.MountainStartCount;
import com.mountain.springbootmountain.web.mountainDTO.Feature;
import com.mountain.springbootmountain.web.mountainDTO.Mountain;
import com.mountain.springbootmountain.web.mountainspotDTO.MountainSpot;
import com.mountain.springbootmountain.web.pathFindDTO.Difficulty;
import com.mountain.springbootmountain.web.pathFindDTO.Edge;
import com.mountain.springbootmountain.web.pathFindDTO.MountainMetaData;
import com.mountain.springbootmountain.web.pathFindDTO.Pos;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class HelloController {

    @Autowired
    KaKaoService ks;

    final private String DIR = "C:/Users/PC/OneDrive/프로젝트/springboot-mountain/src/main/resources/data/";
    public MountainMetaData mountainMetaData = new MountainMetaData();

    public HelloController() throws IOException, ParseException {
    }

    //welcome page
    //테스트용
    @GetMapping("/") @ResponseBody
    public String welcome(HttpServletRequest request) throws IOException, ParseException {
        log.info("home controller");

        return "hello";
    }



    @GetMapping("/search") @ResponseBody
    public ModelAndView searchPathController(Model model,@RequestParam("MNTN_NM") String name, @RequestParam("STARTNUM") int start,@RequestParam("ENDNUM")int end) throws IOException, ParseException {
        log.info("search path controller");
        
        name = mountainMetaData.getMainMountainName(name);
        System.out.println("name = " + name);

        if(start > 0){
            start--;
        }
        if(end > 0){
            end --;
        }

        System.out.println("name = " + name);
        System.out.println("start = " + start);
        System.out.println("end = " + end);



        SearchPath searchPath = new SearchPath(name,start,end);

        ArrayList<ArrayList<ArrayList<Double>>> lineString = searchPath.lineString;


        model.addAttribute("lineString",lineString);

        return new ModelAndView("maprout");
    }



    @PostMapping("/path") @ResponseBody
    public String findPath(@RequestBody HashMap<String,Object> result){

        String mntn_nm = (String) result.get("MNTN_NM");
        int start = (int)result.get("START");
        int end = (int)result.get("END");




        return "hello";
    }


    @GetMapping("/startmap")
    public ModelAndView mountainStartCount(@RequestParam("MNTN_NM") String name, Model model){
        log.info("mountainStartCount");
        name = mountainMetaData.getMainMountainName(name);


        ArrayList<ArrayList<Double>> startList = mountainMetaData.getStartList(name);
        model.addAttribute("startList",startList);

        return new ModelAndView("startmap");
    }

    @GetMapping("/endmap")
    public ModelAndView mountainEndCount(@RequestParam("MNTN_NM") String name, Model model){
        log.info("mountainEndCount");
        name = mountainMetaData.getMainMountainName(name);

        ArrayList<ArrayList<Double>> startList = mountainMetaData.getStartList(name);
        model.addAttribute("startList",startList);

        return new ModelAndView("endmap");
    }

    @PostMapping("/startcount") @ResponseBody
    public MountainStartCount startCount(@RequestBody HashMap<String,Object> result){
        log.info("startCount");


        MountainStartCount mountainStartCount = new MountainStartCount();
        String name = (String) result.get("MNTN_NM");

        name = mountainMetaData.getMainMountainName(name);

        ArrayList<ArrayList<Double>> startList = mountainMetaData.getStartList(name);
        mountainStartCount.setCount(startList.size());
        System.out.println("mountainStartCount = " + mountainStartCount);


        return mountainStartCount;
    }

    @PostMapping("/endcount") @ResponseBody
    public MountainEndCount endCount(@RequestBody HashMap<String,Object> result){
        log.info("endCount");


        MountainEndCount mountainEndCount = new MountainEndCount();
        String name = (String) result.get("MNTN_NM");

        name = mountainMetaData.getMainMountainName(name);

        ArrayList<ArrayList<Double>> endList = mountainMetaData.getStartList(name);
        mountainEndCount.setCount(endList.size());
        System.out.println("mountainEndCount = " + mountainEndCount);


        return mountainEndCount;
    }



    @PostMapping("/mountain")
    @ResponseBody
    public MountainNameResDto searchMountain(@RequestBody HashMap<String,Object> result) throws IOException, ParseException {
        log.info("searchMountain controller");

        System.out.println("result = " + result);

        String name = (String)result.get("MNTN_NM");
        name = mountainMetaData.getMainMountainName(name);
        System.out.println("name = " + name);

        MountainNameResDto mountainNameResDto = new MountainNameResDto();

        if(mountainMetaData.isExist(name)){
            mountainNameResDto.setMNTN_NM(name);
        }
        else{
            mountainNameResDto.setMNTN_NM("ERROR");
        }

        return mountainNameResDto;
    }


    @GetMapping("/map")
    public ModelAndView map(Model model, @RequestParam String MNTN_NM) throws IOException, ParseException {
        log.info("map controller");

        String fileName;
        MNTN_NM = mountainMetaData.getMainMountainName(MNTN_NM);
        fileName = MNTN_NM + ".json";

        TranJsonToMap tranJsonToMap = new TranJsonToMap(fileName);

        System.out.println("tranJsonToMap = " + tranJsonToMap);


        Mountain mountain = tranJsonToMap.getMountain();
        ArrayList<Feature> features = mountain.getFeatures();
        int count=0;

        ArrayList<ArrayList<ArrayList<Double>>> lineString = new ArrayList<>();

        for (Feature feature : features) {
            ArrayList<ArrayList<Double>> posList = feature.getGeometry().getPaths().get(0);

//            //[첫번째 경로][두번째경로] 이런식으로 저장
            lineString.add(posList);
       }
        model.addAttribute("lineString",lineString);


        return new ModelAndView("maptest");
    }


    @GetMapping("/login")
    public String welcome(){
        log.info("login controller");
        return "login";
    }



}
