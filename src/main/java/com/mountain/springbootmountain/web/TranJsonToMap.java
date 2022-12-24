package com.mountain.springbootmountain.web;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mountain.springbootmountain.web.mountainDTO.Mountain;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TranJsonToMap {
    final private String dir = "C:/Users/PC/OneDrive/프로젝트/springboot-mountain/src/main/resources/data/";
    private String fileName;

    public TranJsonToMap(String fileName) {
        this.fileName = fileName;
    }

    public Mountain getMountain() throws IOException, ParseException {
        //경로 + 파일이름으로 절대경로 만들기
        String fName = dir + fileName;

        FileReader fileReader = new FileReader(fName);

        //절대 경로로 파일 읽어오기
        Object o = new JSONParser().parse(fileReader);
        JSONObject jsonObject = (JSONObject) o;
        ObjectMapper objectMapper = new ObjectMapper();

        //toString을 이용한 변환
        Mountain mountain = objectMapper.readValue(jsonObject.toString(), Mountain.class);

        //파일을 닫기
        fileReader.close();

        return mountain;
    }
}
