package com.mountain.springbootmountain.web.pathFindDTO;

import com.mountain.springbootmountain.web.TranJsonToMap;
import com.mountain.springbootmountain.web.TranJsonToSpotMap;
import com.mountain.springbootmountain.web.mountainDTO.Mountain;
import com.mountain.springbootmountain.web.mountainspotDTO.Feature;
import com.mountain.springbootmountain.web.mountainspotDTO.MountainSpot;
import lombok.Data;
import net.minidev.json.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;

@Data
public class MountainMetaData {

    public static ArrayList<String> mountainName = new ArrayList<>();
    public static ArrayList<ArrayList<ArrayList<Double>>> mountainsStartList = new ArrayList<>();
    public static ArrayList<ArrayList<ArrayList<Double>>> mountainsEndList = new ArrayList<>();
    public static HashMap<String, String> mountainSubName = new HashMap<>();


    final private String DIR = "C:/Users/PC/OneDrive/프로젝트/springboot-mountain/src/main/resources/data/";

    public static void addName(String name){
        mountainName.add(name);
    }

    public static boolean isExist(String name){
        //sub List에서 있는지 확인

        if(mountainSubName.containsKey(name)){
            return true;
        }
        return false;
    }

    public static String getMainMountainName(String name){
        if(mountainSubName.containsKey(name)){
            return mountainSubName.get(name);
        }
        else{
            return "Not Exist";
        }
    }



    public MountainMetaData() throws IOException, ParseException {
        System.out.println("MountainMetaData.MountainMetaData");
        updateNameInitMetaData();
        updateStartList();
        updateMountainSubName();
    }

    private void updateMountainSubName() {
        // 메인 산 이름을 넣는다.
        for (String s : mountainName) {
            mountainSubName.put(s,s);
        }

        //부산지역 sub 산 수작업
        mountainSubName.put("구덕산","구덕산");
        mountainSubName.put("승학산","구덕산");
        mountainSubName.put("시약산","구덕산");
        mountainSubName.put("엄광산","구덕산");
        mountainSubName.put("구봉산","구덕산");

        mountainSubName.put("개좌산","개좌산");
        mountainSubName.put("운봉산","개좌산");

        mountainSubName.put("금정산","금정산");
        mountainSubName.put("계명산","금정산");
        mountainSubName.put("상학산","금정산");


        mountainSubName.put("달음산","달음산");
        mountainSubName.put("천마산","달음산");
        mountainSubName.put("함박산","달음산");
        mountainSubName.put("월음산","달음산");
        mountainSubName.put("갈미산","달음산");

        mountainSubName.put("만남의광장구포도서관","만남의광장구포도서관");
        mountainSubName.put("범방산","만남의광장구포도서관");

    }

    private void updateNameInitMetaData() throws IOException, ParseException {
        String json = ".json";

        //폴더를 지정
        File folder = new File(DIR);

        //폴더안의 파일을 전부 가져온다.
        File[] files = folder.listFiles();

        for (File file : files) {
            //폴더안의 파일들을 다 읽는다.

            //SPOT 파일은 건너 뛴다.
            //SPOT은 별로도 구덕산SPOT.json으로 만들 예정
            if(file.getName().contains("SPOT")){
                System.out.println("file = " + file);

                //파일안의 mntn_nm을 읽어 온다.
                TranJsonToSpotMap tranJsonToSpotMap = new TranJsonToSpotMap(file.getName());
                MountainSpot mountainSpot = tranJsonToSpotMap.getMountainSpot();
                String mntn_nm = mountainSpot.getFeatures().get(0).attributes.MNTN_NM;

                Path path1 = Paths.get(DIR+file.getName());
                Path path2 = Paths.get(DIR+mntn_nm+"SPOT"+json);

                Path newFilePath = Files.move(path1, path2, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("newFilePath = " + newFilePath);

            }
            else{
                System.out.println("file = " + file);

                //파일안의 mntn_nm을 읽어 온다.
                TranJsonToMap tranJsonToMap = new TranJsonToMap(file.getName());
                Mountain mountain = tranJsonToMap.getMountain();
                String mntn_nm = mountain.getFeatures().get(0).getAttributes().getMNTN_NM();

                Path path1 = Paths.get(DIR+file.getName());
                Path path2 = Paths.get(DIR+mntn_nm+json);

                MountainMetaData.addName(mntn_nm);

                Path newFilePath = Files.move(path1, path2, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("newFilePath = " + newFilePath);
            }
        }
    }

    //manage_sp2가 시종점인경우 시작점으로 판단하고 리스트에 저장한다.
    private void updateStartList() throws IOException, ParseException {
        System.out.println("MountainMetaData.updateStartList");
        String json = ".json";

        //폴더를 지정
        File folder = new File(DIR);

        //폴더안의 파일을 전부 가져온다.
        File[] files = folder.listFiles();

        for (File file : files) {
            //폴더안의 파일들을 다 읽는다.

            ArrayList<ArrayList<Double>> starts = new ArrayList<>();
            ArrayList<ArrayList<Double>> ends = new ArrayList<>();

            if(file.getName().contains("SPOT")){

                //파일안의 mntn_nm을 읽어 온다.
                TranJsonToSpotMap tranJsonToSpotMap = new TranJsonToSpotMap(file.getName());
                MountainSpot mountainSpot = tranJsonToSpotMap.getMountainSpot();
                ArrayList<Feature> Features = mountainSpot.getFeatures();

                for (Feature feature : Features) {
                    String manage_sp2 = feature.attributes.MANAGE_SP2;
                    //(manage_sp2.equals("시종점")||manage_sp2.equals("조망점")){
                    //if(manage_sp2.equals("시종점")||manage_sp2.equals("조망점")||manage_sp2.equals("정상")){
                    if(manage_sp2.equals("시종점")||manage_sp2.equals("정상")){
                        ArrayList<Double> pos = new ArrayList<>();
                        pos.add(feature.geometry.x);
                        pos.add(feature.geometry.y);
                        starts.add(pos);
                    }
//                    else if(manage_sp2.equals("정상")){
//                        ArrayList<Double> pos = new ArrayList<>();
//                        pos.add(feature.geometry.x);
//                        pos.add(feature.geometry.y);
//                        ends.add(pos);
//                    }
                }

                mountainsStartList.add(starts);
                mountainsEndList.add(ends);

            }
        }
    }

    public static ArrayList<ArrayList<Double>> getStartList(String name){

        int count = 0;
        for (String s : mountainName) {
            if(s.equals(name)){
                return mountainsStartList.get(count);
            }
            count++;
        }

        return new ArrayList<>();
    }

    public static ArrayList<ArrayList<Double>> getEndList(String name){
        int count = 0;
        for (String s : mountainName) {
            if(s.equals(name)){
                return mountainsEndList.get(count);
            }
            count++;
        }

        return new ArrayList<>();
    }
}
