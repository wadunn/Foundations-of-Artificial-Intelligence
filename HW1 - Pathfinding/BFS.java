import java.util.*;
import java.lang.Math;

public class BFS {
    Queue<HashMap<String, Integer>> bfsQueue;
    HashMap<Integer,HashMap<String, Integer>> parents;
    HashSet<List<Integer>> coordsVisited;
    //Stack<Integer[]> prevQueue;
    int[] start; // line3
    int stamina; // line4
    int lodgeCount; // line5
    int[][] lodgeCoords; 
    int[][] elevation;
    int goal=0;
    Queue<HashMap<String, Integer>> lodgesFound;
    int nodeCount = 0;
    



    
    private HashMap<String, Integer> node(int nodeNum, int parent, int wCoord, int hCoord, int depth){
        HashMap<String, Integer> node = new HashMap<String,Integer>();
        node.put("nodeNum", nodeNum);
        node.put("parent", parent);
        node.put("wCoord", wCoord);
        node.put("hCoord", hCoord);
        node.put("depth", depth);
        return node; 
    }

    private List<Integer> coord(int wCoord, int hCoord){
        List<Integer> coord = new ArrayList<Integer>();
        coord.add(wCoord);
        coord.add(hCoord);
        return coord;
    }

    private void initQ(){
        nodeCount =1;
        HashMap<String,Integer> nodeInit = node(nodeCount, 0, start[0], start[1], 1);
        bfsQueue.add(nodeInit);
        parents.put(1, nodeInit);
        while(bfsQueue.size() > 0){
            areaSearch();
        }
    }

    private void areaSearch(){
        int searchElev;
        HashMap<String,Integer> nextNode;
        List<Integer> coords;
        HashMap<String,Integer> curNode = bfsQueue.remove();
        int wCoord = curNode.get("wCoord");
        int hCoord = curNode.get("hCoord");
        int nodeNum = curNode.get("nodeNum");
        int depth = curNode.get("depth");
        if(wCoord == 5 && hCoord == 3){
        //System.out.println("W "+ wCoord+ " H "+ hCoord);
        }
        for(int i=-1; i <=1; i++){
            for(int j=-1; j<=1; j++){
                if(i!=0 || j!=0){
                    //ensure next coord is in bounds of search matrix
                    if(wCoord+i >=0 && wCoord+i <elevation[0].length && hCoord+j >= 0 && hCoord+j <elevation.length){
                        searchElev = elevation[hCoord+j][wCoord+i];
                        //System.out.println("coords " +(wCoord+i) + " "+ (hCoord+j) + " : elevation: " + searchElev);

                        //Check to see if new search location is goal location
                        boolean goalCheck = false;

                        //Checks for trees and makes sure the tree value is less than the current elevation
                        if(searchElev <0 && searchElev*(-1) <= Math.abs(elevation[hCoord][wCoord])){
                            for(int k=0; k<lodgeCount; k++){
                                if(wCoord+i == lodgeCoords[k][0] && hCoord+j == lodgeCoords[k][1]){
                                    goalCheck = true;
                                }
                            }
                            nodeCount++;
                            nextNode = node(nodeCount, nodeNum, wCoord+i, hCoord+j, depth+1);
                            coords = coord(wCoord+i,hCoord+j);
                            if(coordsVisited.add(coords)){
                                parents.put(nodeCount,nextNode);
                                if(!goalCheck){
                                    bfsQueue.add(nextNode);
                                }else{
                                    lodgesFound.add(nextNode);
                                    goal++;
                                    if(goal == lodgeCount){
                                        //System.out.println("Lodges Found");
                                        bfsQueue.removeAll(bfsQueue);
                                    }
                                }
                                //System.out.println("Search Case 1");
                            }

                        }
                        //Checks if next location elevation is less than current elevation + stamina
                        else if(searchElev>=0 && searchElev - stamina <= Math.abs(elevation[hCoord][wCoord])){
                            for(int k=0; k<lodgeCount; k++){
                                if(wCoord+i == lodgeCoords[k][0] && hCoord+j == lodgeCoords[k][1]){
                                    goalCheck = true;
                                }
                            }
                            nodeCount++;
                            nextNode = node(nodeCount, nodeNum, wCoord+i, hCoord+j, depth+1);
                            coords = coord(wCoord+i, hCoord+j);
                            if(coordsVisited.add(coords)){
                                parents.put(nodeCount, nextNode);
                                if(!goalCheck){
                                    bfsQueue.add(nextNode);
                                }else{
                                    lodgesFound.add(nextNode);
                                    goal++;
                                    if(goal == lodgeCount){
                                        //System.out.println("Lodges Found");
                                        bfsQueue.removeAll(bfsQueue);
                                    }
                                }
                            }
                        }else{
                            //System.out.println("Case Fail Check: coords: "+ wCoord + " " + hCoord);
                        }
                       // System.out.println("nodeCount "+nodeCount);
                    }
                }
            }
        }
    }

    private String getPathData(HashMap<String,Integer> node){
        String path = "";
        HashMap<String,Integer> curNode = node;
        path = curNode.get("wCoord")+","+curNode.get("hCoord");
        //System.out.println("DeptH: "+node.get("depth"));
        for(int i = 1; i< node.get("depth"); i++){
            if(i <= node.get("depth")){
                path = " "+path;
            }
            //System.out.println(curNode.get("parent"));
            curNode = parents.get(curNode.get("parent"));
            String coords = curNode.get("wCoord")+","+curNode.get("hCoord");
            path = coords+path;
        }
        return path;
    }

    public String[] results(){
        String[] result = new String[lodgeCount];
        for(int i =0; i<lodgeCount; i++){
            result[i] = "FAIL";
        }
        HashMap<String,Integer> lodgeNode;
        while(lodgesFound.size()>0){
            //System.out.println("lodges Found: "+lodgesFound.size());
            lodgeNode = lodgesFound.remove();
            boolean match = false;
            int lodgeNum = 0;
            while(!match && lodgeNum < lodgeCount){
            
                if(lodgeNode.get("wCoord") == lodgeCoords[lodgeNum][0] && lodgeNode.get("hCoord") == lodgeCoords[lodgeNum][1]){
                    match = true;
                    result[lodgeNum] = getPathData(lodgeNode);
                }else{
                    lodgeNum++;
                }
            }

        }
        return result;
    }

    public void runBFS(int[] start, int stamina, int lodgeCount, int[][] lodgeCoords, int[][] elevation){
        this.start = start;
        this.stamina = stamina;
        this.lodgeCount = lodgeCount;
        this.lodgeCoords = lodgeCoords;
        this.elevation = elevation;
        
        bfsQueue = new LinkedList<>();
        lodgesFound = new LinkedList<>();
        parents = new HashMap<>();
        coordsVisited = new HashSet<>();
        initQ();
        

    }

}
