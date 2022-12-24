package com.mountain.springbootmountain.web;

import com.mountain.springbootmountain.web.mountainDTO.Feature;
import com.mountain.springbootmountain.web.mountainDTO.Mountain;
import com.mountain.springbootmountain.web.pathFindDTO.Edge;
import com.mountain.springbootmountain.web.pathFindDTO.MountainMetaData;
import com.mountain.springbootmountain.web.pathFindDTO.Pos;
import com.mountain.springbootmountain.web.pathFindDTO.PqEle;
import net.minidev.json.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import static java.lang.Double.compare;

/*
* 분기점 ~ 분기점 edge를 얻을 수 있다.
* 시종점, 정상, 조망점 등의 좌표가 edge의 start~ end에 있다고 가정(오차가 있다고 생각)
* edge의 start, end를 중복되지 않도록 정점으로 등록 시킨다.
*
* x,y좌표를 넣었을 때 오차를 고려하여 해당 정점의 idx를 나오게 하는 func필요
* 오차 x좌표가 비슷해도 y좌표가 일정수준이상 차이나면 다른 정점임
*
* 다익스트라 -> 경로 추천 시작 정점을 visited한다. 해당 정점에서 갈 수 있는 다른 정점들의 길이를 갱신하고 우선순위 큐에 넣는다.
* 우선순위 큐에서 가장 짧은 정점을 선택한 뒤 과정을 반복한다.
*
* 현재 정점이 포함된 edge의 끝으로 이동하는 graph
* edge끝~ 목표 정점이 포함된 edge로 이동하는 graph(데이크스트라)
* */

public class SearchPath {

    public Long idx=0L;
    private String name;
    public Mountain mountain;
    public int startIdx,endIdx;
    public ArrayList<Edge> edges = new ArrayList<>();
    public ArrayList<ArrayList<Double>> routes;

    //pos 넣으면 Long나옴
    public HashMap<Pos, Long> nodes = new HashMap<>();
    //index 넣으면 Pos나옴
    public ArrayList<Pos> inverseNodes = new ArrayList();

    //정점 -> 그 정점과 연결된 간선  ----> 인접 리스트 형성
    public HashMap<Pos, ArrayList<Edge>> adj = new HashMap<>();

    public ArrayList<Boolean> visited;
    public ArrayList<Double> dist;
    public ArrayList<Integer> prev;

    public ArrayList<ArrayList<ArrayList<Double>>> lineString;



    public SearchPath(String name, int startIdx, int endIdx) throws IOException, ParseException {
        this.name = name;
        this.startIdx = startIdx;
        this.endIdx = endIdx;
        TranJsonToMap tranJsonToMap = new TranJsonToMap(name + ".json");
        this.mountain = tranJsonToMap.getMountain();

        //makeStartEdge();
        //makeEndEdge();
        makeAnotherEdge();
        makeEdge();
        makeNodes();
        makeAdj();
        dijkstra();
        this.lineString = changeRoutesToPaths();

    }



    ArrayList<ArrayList<ArrayList<Double>>> changeRoutesToPaths(){

        ArrayList<ArrayList<ArrayList<Double>>> lineString = new ArrayList<>();

        ArrayList<Double> start = null;

        for (ArrayList<Double> end : routes) {

            if(start!=null){

                int findFlag = 0;

                ArrayList<Feature> features = mountain.getFeatures();


                for (Feature feature : features) {
                    ArrayList<ArrayList<Double>> arrayLists = feature.geometry.paths.get(0);
                    ArrayList<Double> startPath = arrayLists.get(0);
                    ArrayList<Double> endPath = arrayLists.get(arrayLists.size() - 1);

                    if(compare(start.get(0),startPath.get(0))==0 && compare(start.get(1),startPath.get(1))==0){
                        if(compare(end.get(0),endPath.get(0))==0 && compare(end.get(1),endPath.get(1))==0){
                            findFlag=1;

                            lineString.add(arrayLists);
                            break;
                        }
                    }
                    if(compare(end.get(0),startPath.get(0))==0 && compare(end.get(1),startPath.get(1))==0){
                        if(compare(start.get(0),endPath.get(0))==0 && compare(start.get(1),endPath.get(1))==0){
                            findFlag=1;

                            lineString.add(arrayLists);
                            break;
                        }
                    }


                }

                if(findFlag==1){
                    start = new ArrayList<>();
                    start.add(end.get(0));
                    start.add(end.get(1));

                    continue;
                }
                //혹시 path중간에 위치한 것인지 확인
                else{

                    //start 가 list의 양끝중 하나일 때
                    ArrayList<Double> now;
                    ArrayList<Double> mid;


                    double minErr = Double.MAX_VALUE;
                    ArrayList<ArrayList<Double>> minPath = null;

                    //edge들을 돌며 확인한다.
                    for (Feature feature : features) {
                        ArrayList<ArrayList<Double>> arrayLists = feature.geometry.paths.get(0);
                        ArrayList<Double> startList = arrayLists.get(0);
                        ArrayList<Double> endList = arrayLists.get(arrayLists.size() - 1);


                        now = start;
                        mid = end;
                        //start가  list의 제일 앞인 경우
                        //end는 중간에 있는 경우
                        if(compare(startList.get(0),now.get(0))==0 &&
                           compare(startList.get(1),now.get(1))==0){

                            int sliceIdx = 0;
                            for (ArrayList<Double> arrayList : arrayLists) {

                                double dist = getNodeDist(arrayList.get(0), mid.get(0), arrayList.get(1), mid.get(1));
                                //탐색 완료
                                //이전까지의 유망한 노드보다 더 유망한경우
                                if( compare(minErr,dist)>0){
                                    minErr = dist;
                                    //list의 제일 앞 ~ end까지의 리스트를 lineString에 저장
                                    List<ArrayList<Double>> slice = arrayLists.subList(0, sliceIdx);
                                    ArrayList<ArrayList<Double>> temp = new ArrayList<>();
                                    for (ArrayList<Double> doubles : slice) {
                                        temp.add(doubles);
                                    }
                                    minPath = temp;
                                }
                                sliceIdx++;
                            }

                        }
                        //start(now)가  list의 제일 뒤인 경우
                        //end는 중간에 있는 경우
                        //endList -> 제일 뒤인경우
                        //now -> 현재 start넣어둠
                        if(compare(endList.get(0),now.get(0))==0 &&
                                compare(endList.get(1),now.get(1))==0){

                            int sliceIdx = 0;
                            for (ArrayList<Double> arrayList : arrayLists) {

                                double dist = getNodeDist(arrayList.get(0), mid.get(0), arrayList.get(1), mid.get(1));
                                if( compare(minErr,dist)>0){
                                    minErr = dist;
                                    //list의 중간(mid발견 지점) ~ end까지의 리스트를 lineString에 저장
                                    List<ArrayList<Double>> slice = arrayLists.subList(sliceIdx, arrayLists.size()-1);
                                    ArrayList<ArrayList<Double>> temp = new ArrayList<>();
                                    for (ArrayList<Double> doubles : slice) {
                                        temp.add(doubles);
                                    }
                                    minPath = temp;
                                }
                                sliceIdx++;
                            }

                        }



                        // end가 list의 앞 혹은 뒤에 있고
                        // start가 list의 중간에 위치한 경우 탐색



                        now = end;
                        mid = start;
                        //start가  list의 제일 앞인 경우
                        //end는 중간에 있는 경우
                        if(compare(startList.get(0),now.get(0))==0 &&
                                compare(startList.get(1),now.get(1))==0){

                            int sliceIdx = 0;
                            for (ArrayList<Double> arrayList : arrayLists) {

                                double dist = getNodeDist(arrayList.get(0), mid.get(0), arrayList.get(1), mid.get(1));
                                if( compare(minErr,dist)>0){
                                    minErr = dist;
                                    //list의 제일 앞 ~ end까지의 리스트를 lineString에 저장
                                    List<ArrayList<Double>> slice = arrayLists.subList(0, sliceIdx);
                                    ArrayList<ArrayList<Double>> temp = new ArrayList<>();
                                    for (ArrayList<Double> doubles : slice) {
                                        temp.add(doubles);
                                    }
                                    minPath = temp;
                                }
                                sliceIdx++;
                            }

                        }
                        //start(now)가  list의 제일 뒤인 경우
                        //end는 중간에 있는 경우
                        //endList -> 제일 뒤인경우
                        //now -> 현재 start넣어둠
                        if(compare(endList.get(0),now.get(0))==0 &&
                                compare(endList.get(1),now.get(1))==0){

                            int sliceIdx = 0;
                            for (ArrayList<Double> arrayList : arrayLists) {

                                double dist = getNodeDist(arrayList.get(0), mid.get(0), arrayList.get(1), mid.get(1));
                                if( compare(minErr,dist)>0){
                                    minErr = dist;
                                    //list의 중간(mid발견 지점) ~ end까지의 리스트를 lineString에 저장
                                    List<ArrayList<Double>> slice = arrayLists.subList(sliceIdx, arrayLists.size()-1);
                                    ArrayList<ArrayList<Double>> temp = new ArrayList<>();
                                    for (ArrayList<Double> doubles : slice) {
                                        temp.add(doubles);
                                    }
                                    minPath = temp;
                                }
                                sliceIdx++;
                            }

                        }

                    }

                    if(minPath != null){
                        lineString.add(minPath);
                    }


//                    System.out.println("cant find start to end pos");
//                    System.out.println("start = " + start);
//                    System.out.println("end = " + end);
//                    ArrayList<ArrayList<Double>> arrayLists = new ArrayList<>();
//                    ArrayList<Double> temp = new ArrayList<>();
//                    temp.add(start.get(0)); temp.add(start.get(1));
//                    arrayLists.add(temp);
//
//                    ArrayList<Double> temp2 = new ArrayList<>();
//                    temp2.add(end.get(0)); temp2.add(end.get(1));
//                    arrayLists.add(temp2);
//
//                    lineString.add(arrayLists);
//
//


                    start = new ArrayList<>();
                    start.add(end.get(0));
                    start.add(end.get(1));
                }




            }
            else{
                start = new ArrayList<>();
                start.add(end.get(0));
                start.add(end.get(1));
                continue;
            }
        }

        return lineString;
    }

    void makeAdj(){
        //edge 의 start와 end에 대해 무방향 그래프로 만들어 주기 위해 양쪽으로 연결한다.
        //adj[start]
        //adj[end]에 각각 넣어준다.
        for (Edge edge : edges) {
            //System.out.println("edge = " + edge);

            //edge의 start 에서 end로

            //값이 없다면 빈 배열을 넣어준다.
            if(adj.containsKey(edge.start)==false){
                ArrayList<Edge> temp = new ArrayList<>();
                adj.put(edge.start,temp);
            }

            //start로 시작하는 리스트 얻어오기
            ArrayList<Edge> startAdj = adj.get(edge.start);

            if(startAdj.contains(edge)==false){
                startAdj.add(edge);
            }

            //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

            //edge의 end 에서 start로

            //값이 없다면 빈 배열을 넣어준다.
            if(adj.containsKey(edge.end)==false){
                ArrayList<Edge> temp = new ArrayList<>();
                adj.put(edge.end,temp);
            }

            ArrayList<Edge> endAdj = adj.get(edge.end);

            Edge inverseEdge = new Edge();
            inverseEdge.start = edge.end;
            inverseEdge.end = edge.start;
            inverseEdge.length = edge.length;

            if(endAdj.contains(inverseEdge)==false){
                endAdj.add(inverseEdge);
            }

        }


    }


    void dijkstra(){

        visited = new ArrayList<>();
        dist = new ArrayList<Double>();
        prev = new ArrayList<>();

        System.out.println("nodes.size() = " + nodes.size());

        for(int i=0;i<nodes.size();i++){
            dist.add(Double.MAX_VALUE);
            visited.add(false);
            prev.add(-1);
        }

        System.out.println("dist = " + dist.size());
        System.out.println("visited = " + visited.size());


        ArrayList<ArrayList<Double>> startList = MountainMetaData.getStartList(name);
        ArrayList<Double> start = startList.get(startIdx);
        int startNodeIdx = nodes.get(new Pos(start.get(0), start.get(1))).intValue();

        ArrayList<Double> end = startList.get(endIdx);
        int endNodeIdx = nodes.get(new Pos(end.get(0), end.get(1))).intValue();



        PriorityQueue<PqEle> pq = new PriorityQueue<>();

        System.out.println("startNodeIdx = " + startNodeIdx);

        //시작 정점 초기화
        //visited.set(startNodeIdx,true);
        dist.set(startNodeIdx,0.0);
        System.out.println("dist.get(startNodeIdx) = " + dist.get(startNodeIdx));

        pq.add(new PqEle(startNodeIdx,0.0));

        while(!pq.isEmpty()){
            int curr;
            do{
                curr = pq.poll().idx;
            }while(!pq.isEmpty() && (visited.get(curr)==true));//방문한 정점이면 계속 다음 pq.poll 뽑음
            
            if(visited.get(curr)==true){
                break;
            }

            if(curr==196){
                int debug = 3;
            }
            
            visited.set(curr,true);
            ArrayList<Edge> edges = adj.get(idxToPos(curr));
            for (Edge edge : edges) {
                Pos next = edge.end;
                double d = edge.length;
                
                if(compare(dist.get(posToIdx(next)), dist.get(curr) + d)>0){
                    dist.set(posToIdx(next),dist.get(curr)+d);
                    //next로 갈 때 curr을 방문한다.
                    prev.set(posToIdx(next), curr);

//                    System.out.print("d = " + d);
//                    System.out.println("dist.get(curr) = " + dist.get(curr));
//                    System.out.println("dist.get(posToIdx(next)) = " + dist.get(posToIdx(next)));

                    pq.add(new PqEle(posToIdx(next),dist.get(posToIdx(next))));
                }
            }
        }

        routes = new ArrayList<>();

        Double aDouble = dist.get(endNodeIdx);


        System.out.println(" aDouble = " + aDouble);


        int route = endNodeIdx;
        while(route != startNodeIdx && route != -1){
            ArrayList<Double> pos = new ArrayList<>();
            Pos pos1 = idxToPos(route);
            pos.add(pos1.x); pos.add(pos1.y);

            System.out.print(route);
//            System.out.println("  " + idxToPos(route).x + "," + idxToPos(route).y);

            routes.add(pos);
            route = prev.get(route);
        }

        if(route==startNodeIdx){
            ArrayList<Double> pos = new ArrayList<>();
            Pos pos1 = idxToPos(route);
            pos.add(pos1.x); pos.add(pos1.y);
            routes.add(pos);
        }


       System.out.print(route);
//        System.out.println("  " + idxToPos(route));




        for (ArrayList<Double> doubles : startList) {

            for(int i=0;i<startList.size();i++){
                if(compare(doubles.get(0),startList.get(i).get(0))==0 && compare(doubles.get(1),startList.get(i).get(1))==0){
                    //System.out.println("시작지점 번호 :" + i);
                    break;
                }
            }
            Long aLong = nodes.get(new Pos(doubles.get(0), doubles.get(1)));
            int aLongint = aLong.intValue();

            Double dist = this.dist.get(aLongint);
            //System.out.println("거리" + dist);
        }

        System.out.println("endNodeIdx = " + endNodeIdx);

        Double findDist = dist.get(endNodeIdx);
        System.out.println("findDist = " + findDist);

        Pos pos = idxToPos(startNodeIdx);
        System.out.println("pos = " + pos);
        Pos pos1 = idxToPos(endNodeIdx);
        System.out.println("pos1 = " + pos1);

    }

    int posToIdx(Pos a){
        Long aLong = nodes.get(a);
        return aLong.intValue();
    }

    Pos idxToPos(int idx){
        Pos pos = inverseNodes.get(idx);
        return pos;
    }

    void makeNodes() throws IOException, ParseException {
        //Path는 오차가 없는 정점들로 구성되기 때문에 완벽히 일치하는 경우로 검사가 가능

        ArrayList<Feature> features = mountain.getFeatures();
        for (Feature feature : features) {
            ArrayList<ArrayList<Double>> arrayLists = feature.geometry.getPaths().get(0);
            ArrayList<Double> start = arrayLists.get(0);
            ArrayList<Double> end = arrayLists.get(arrayLists.size() - 1);

            //값이 없는 경우 nodes의 키와 val과 inverseNodes의 키와 밸류가 일치하도록 작성됨
            if(nodes.containsKey(new Pos(start.get(0),start.get(1)))== false){
                inverseNodes.add(new Pos(start.get(0),start.get(1)));
                nodes.put(new Pos(start.get(0),start.get(1)),idx++);
            }
            if(nodes.containsKey(new Pos(end.get(0),end.get(1)))== false){
                inverseNodes.add(new Pos(end.get(0),end.get(1)));
                nodes.put(new Pos(end.get(0),end.get(1)),idx++);
            }
        }

//        //start와 end는 넣어야 하나?
//        ArrayList<ArrayList<Double>> startList = MountainMetaData.getStartList(name);
//        ArrayList<Double> doubles = startList.get(startIdx);
//        if(nodes.containsKey(new Pos(doubles.get(0),doubles.get(1)))==false){
//            inverseNodes.add(new Pos(doubles.get(0),doubles.get(1)));
//            nodes.put(new Pos(doubles.get(0),doubles.get(1)),idx++);
//        }
//
//        doubles = startList.get(endIdx);
//        if(nodes.containsKey(new Pos(doubles.get(0),doubles.get(1)))==false){
//            inverseNodes.add(new Pos(doubles.get(0),doubles.get(1)));
//            nodes.put(new Pos(doubles.get(0),doubles.get(1)),idx++);
//        }

        ArrayList<ArrayList<Double>> startList = MountainMetaData.getStartList(name);
        for (ArrayList<Double> doubles : startList) {
            //모든 시종점, 정상에 대해 node로 등록
            if(nodes.containsKey(new Pos(doubles.get(0),doubles.get(1)))==false) {
                inverseNodes.add(new Pos(doubles.get(0), doubles.get(1)));
                nodes.put(new Pos(doubles.get(0), doubles.get(1)), idx++);
            }
        }
        



    }

    //paths 뒤져가며 가장 적합한 지점을 찾고 그 edge를 찾는다.

    void makeStartEdge(){

        ArrayList<ArrayList<Double>> startList = MountainMetaData.getStartList(name);
        System.out.println("startList = " + startList);
        ArrayList<Double> startPos = startList.get(startIdx);

        

        double minDist=Double.MAX_VALUE;
        //내가 찾고있는 정점이 포함된 edge
        ArrayList<ArrayList<Double>> minPaths = null;
        //실제 Paths의 거리
        double realDist=0;

        ArrayList<Feature> features = mountain.getFeatures();
        for (Feature feature : features) {
            ArrayList<ArrayList<Double>> arrayLists = feature.geometry.paths.get(0);
            //좌표리스트들 iter
            for (ArrayList<Double> arrayList : arrayLists) {
                //startPos와 가장 근접한(거리가 작은) 것을 찾는다.
                double ans = getNodeDist(startPos.get(0),arrayList.get(0),startPos.get(1),arrayList.get(1));
                if(minDist > ans){
                    minDist = ans;
                    minPaths = arrayLists;
                    realDist = feature.attributes.PMNTN_LT;
                }
            }
        }

        if(minPaths != null){
            //시작지점 ~ 한쪽 지점까지의 edge생성
            Edge edge1 = new Edge();
            ArrayList<Double> firstPosInEdge = minPaths.get(0);
            double firstToStartDist = getNodeDist(firstPosInEdge.get(0), startPos.get(0), firstPosInEdge.get(1), startPos.get(1));
            double pathDist = getNodeDist(minPaths.get(0).get(0),minPaths.get(1).get(0),minPaths.get(0).get(1),minPaths.get(1).get(1));
            edge1.length = (firstToStartDist/pathDist)*realDist;
            edge1.length = Math.ceil(edge1.length * 100) / 100.0;

            edge1.start = new Pos(startPos.get(0),startPos.get(1));
            edge1.end = new Pos(firstPosInEdge.get(0),firstPosInEdge.get(1));
            edges.add(edge1);

            //시작지점 ~ 다른 한쪽 지점까지의 edge생성
            Edge edge2 = new Edge();
            ArrayList<Double> secondPosInEdge = minPaths.get(minPaths.size() - 1);
            double secondToStartDist = getNodeDist(secondPosInEdge.get(0), startPos.get(0), secondPosInEdge.get(1), secondPosInEdge.get(1));
            edge2.length = (secondToStartDist/pathDist)*realDist;
            edge2.length = Math.ceil(edge2.length * 100) / 100.0;
            edge2.start = new Pos(startPos.get(0),startPos.get(1));
            edge2.end = new Pos(secondPosInEdge.get(0),secondPosInEdge.get(1));
        }
        else{          System.out.println("시작 정점이 포함된 Edge를 찾지 못했습니다.");        }

    }

    void makeEndEdge(){
        ArrayList<ArrayList<Double>> startList = MountainMetaData.getStartList(name);
        ArrayList<Double> endPos = startList.get(endIdx);



        double minDist=Double.MAX_VALUE;
        //내가 찾고있는 정점이 포함된 edge
        ArrayList<ArrayList<Double>> minPaths = null;
        //실제 Paths의 거리
        double realDist=0;

        ArrayList<Feature> features = mountain.getFeatures();
        for (Feature feature : features) {
            ArrayList<ArrayList<Double>> arrayLists = feature.geometry.paths.get(0);
            //좌표리스트들 iter
            for (ArrayList<Double> arrayList : arrayLists) {
                //startPos와 가장 근접한(거리가 작은) 것을 찾는다.
                double ans = getNodeDist(endPos.get(0),arrayList.get(0),endPos.get(1),arrayList.get(1));
                if(minDist > ans){
                    minDist = ans;
                    minPaths = arrayLists;
                    realDist = feature.attributes.PMNTN_LT;
                }
            }
        }

        if(minPaths != null){
            //시작지점 ~ 한쪽 지점까지의 edge생성
            Edge edge1 = new Edge();
            ArrayList<Double> firstPosInEdge = minPaths.get(0);
            double firstToStartDist = getNodeDist(firstPosInEdge.get(0), endPos.get(0), firstPosInEdge.get(1), endPos.get(1));
            double pathDist = getNodeDist(minPaths.get(0).get(0),minPaths.get(1).get(0),minPaths.get(0).get(1),minPaths.get(1).get(1));
            edge1.length = (firstToStartDist/pathDist)*realDist;
            edge1.length = Math.ceil(edge1.length * 100) / 100.0;
            edge1.start = new Pos(endPos.get(0),endPos.get(1));
            edge1.end = new Pos(firstPosInEdge.get(0),firstPosInEdge.get(1));
            edges.add(edge1);

            //시작지점 ~ 다른 한쪽 지점까지의 edge생성
            Edge edge2 = new Edge();
            ArrayList<Double> secondPosInEdge = minPaths.get(minPaths.size() - 1);
            double secondToStartDist = getNodeDist(secondPosInEdge.get(0), endPos.get(0), secondPosInEdge.get(1), secondPosInEdge.get(1));
            edge2.length = (secondToStartDist/pathDist)*realDist;
            edge2.length = Math.ceil(edge2.length * 100) / 100.0;
            edge2.start = new Pos(endPos.get(0),endPos.get(1));
            edge2.end = new Pos(secondPosInEdge.get(0),secondPosInEdge.get(1));
        }
        else{          System.out.println("종료 정점이 포함된 Edge를 찾지 못했습니다.");        }



    }

    //시종점 , 정상등에 대해 추가 edge 생성
    void makeAnotherEdge(){
    
        ArrayList<ArrayList<Double>> startList = MountainMetaData.getStartList(name);
        for (ArrayList<Double> endPos : startList) {

            int samePosFlag = 0;
    
            double minDist=Double.MAX_VALUE;
            //내가 찾고있는 정점이 포함된 edge
            ArrayList<ArrayList<Double>> minPaths = null;
            //실제 Paths의 거리
            double realDist=0;
    
            ArrayList<Feature> features = mountain.getFeatures();
            for (Feature feature : features) {
                ArrayList<ArrayList<Double>> arrayLists = feature.geometry.paths.get(0);
                //좌표리스트들 iter


                for (ArrayList<Double> arrayList : arrayLists) {
                    //startPos와 가장 근접한(거리가 작은) 것을 찾는다.
                    double ans = getNodeDist(endPos.get(0),arrayList.get(0),endPos.get(1),arrayList.get(1));
                    if(compare(minDist,ans)>0){
                        minDist = ans;
                        minPaths = arrayLists;
                        realDist = feature.attributes.PMNTN_LT;
                    }
                }
            }
    
            if(minPaths != null){
                //시작지점 ~ 한쪽 지점까지의 edge생성
                Edge edge1 = new Edge();
                ArrayList<Double> firstPosInEdge = minPaths.get(0);
                double firstToStartDist = getNodeDist(firstPosInEdge.get(0), endPos.get(0), firstPosInEdge.get(1), endPos.get(1));
                double pathDist = getNodeDist(minPaths.get(0).get(0),minPaths.get(minPaths.size()-1).get(0),minPaths.get(0).get(1),minPaths.get(minPaths.size()-1).get(1));
                edge1.length = (firstToStartDist/pathDist)*realDist;
                edge1.length = Math.ceil(edge1.length * 100) / 100.0;
                edge1.start = new Pos(endPos.get(0),endPos.get(1));
                edge1.end = new Pos(firstPosInEdge.get(0),firstPosInEdge.get(1));
                edges.add(edge1);
    
                //시작지점 ~ 다른 한쪽 지점까지의 edge생성
                Edge edge2 = new Edge();
                ArrayList<Double> secondPosInEdge = minPaths.get(minPaths.size() - 1);
                double secondToStartDist = getNodeDist(secondPosInEdge.get(0), endPos.get(0), secondPosInEdge.get(1), endPos.get(1));
                edge2.length = (secondToStartDist/pathDist)*realDist;
                edge2.length = Math.ceil(edge2.length * 100) / 100.0;
                edge2.start = new Pos(endPos.get(0),endPos.get(1));
                edge2.end = new Pos(secondPosInEdge.get(0),secondPosInEdge.get(1));
                edges.add(edge2);
            }
            else{          System.out.println("종료 정점이 포함된 Edge를 찾지 못했습니다.");        }


        }
    }
    
    

    void makeEdge(){
        ArrayList<Feature> features = mountain.getFeatures();
        for (Feature feature : features) {
            ArrayList<ArrayList<Double>> arrayLists = feature.getGeometry().getPaths().get(0);
            ArrayList<Double> startNode = arrayLists.get(0);
            ArrayList<Double> endNode = arrayLists.get(arrayLists.size() - 1);

            Edge edge = new Edge();
            edge.length = feature.getAttributes().PMNTN_LT;
            edge.start = new Pos(startNode.get(0),startNode.get(1));
            edge.end = new Pos(endNode.get(0),endNode.get(1));

            edges.add(edge);
        }
    }

    //데이터 확인용 함수
    void findLength(){

        ArrayList<Feature> features = mountain.getFeatures();

        for (Feature feature : features) {
            System.out.println("경로의 길이" + feature.attributes.PMNTN_LT);

            ArrayList<ArrayList<ArrayList<Double>>> paths = feature.geometry.paths;
            ArrayList<Double> start = paths.get(0).get(0);

            ArrayList<Double> end = paths.get(0).get(paths.get(0).size() - 1);

            double x = start.get(0)-end.get(0);
            double y = start.get(1)-end.get(1);
            double ans = Math.sqrt(x*x+y*y);

            System.out.println("ans = " + ans);

        }
    }


    int findPosition(Pos p){

        ArrayList<ArrayList<Double>> startList = MountainMetaData.getStartList(name);
        int i = 0;
        for (ArrayList<Double> doubles : startList) {
            if(doubles.get(0)== p.x && doubles.get(1)==p.y){
                return i;
            }
            i++;
        }

        return -1;
    }

    //두 점 사이의 거리
    double getNodeDist(double x1, double x2, double y1, double y2){
        double x = x1-x2;
        double y = y1-y2;
        double ans = Math.sqrt(x*x + y*y);
        return ans;
    }

}
