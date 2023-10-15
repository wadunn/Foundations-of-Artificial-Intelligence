import java.util.*;

public class A{
    int[] start;
    int stamina;
    int lodgeCount;
    int[][] lodgeCoords;
    int[][] elevation;

    ANode aqueue;
    ANode[] lodgePath;
    HashMap<String, ANode> pathData;
    //HashMap<String, ANode> noRepeat;

    String[] results;


    private int calcHeuristicDist(int lodgeNum, int wCoord, int hCoord, int momentum){
        int cost;
        int wlodge = lodgeCoords[lodgeNum][0];
        int hlodge = lodgeCoords[lodgeNum][1];
        int vertical = Math.abs(hCoord - hlodge);
        int horizontal = Math.abs(wCoord - wlodge);
        int diagCount = Math.min(vertical, horizontal);
        int straightCount = Math.abs(horizontal - vertical);
        cost = straightCount * 10 + diagCount *14;
        
        if(elevation[hlodge][wlodge] > elevation[hCoord][wCoord]){
           // cost += elevation[hlodge][wlodge] - elevation[hCoord][wCoord] + momentum;
        }
        return cost;
    }


    private void initA(){
        ANode temp;
        for(int i=0; i<lodgeCount; i++){
            aqueue = new ANode();
            pathData = new HashMap<String, ANode>();
            int elev = elevation[start[1]][start[0]];
            aqueue = aqueue.newNode(0, calcHeuristicDist(i, start[0], start[1], 0), start[0], start[1], start[0], start[1],0,"", elev, elev);
            while(aqueue !=null){
                temp = aqueue;
                //System.out.println(aqueue.coord+" : "+aqueue.priority+" : parent "+ aqueue.parent);
                if(!pathData.containsKey(aqueue.coord+" "+aqueue.parent)){
                    pathData.put(aqueue.coord+" "+aqueue.parent, aqueue);
                    temp = aqueue;
                    if(aqueue.wCoord == lodgeCoords[i][0] && aqueue.hCoord == lodgeCoords[i][1]){
                        pathData.put(aqueue.coord, aqueue);
                        break;
                    } 
                    aqueue = findPath(aqueue, i);
                }
                if(temp.coord.equals(aqueue.coord)){
                    aqueue = aqueue.pop(aqueue);
                }
               
            }
            
            results[i] = expandPath(i);
        }
    }

    private int elevationCost(ANode temp, int wCoord, int hCoord, int pw, int ph){
        int eNext = elevation[hCoord][wCoord];
        int eCurr = elevation[ph][pw];
        return Math.max(0, eNext-eCurr-temp.momentum);
    }
    
    public boolean legalMove(ANode temp, int i, int j){
        boolean legal = false;
        int elev = temp.elevation;
        int momentum = temp.momentum;
        int newElev = elevation[temp.hCoord+j][temp.wCoord+i];
        String newCoord = (temp.wCoord+i)+","+(temp.hCoord+j);
        if(!pathData.containsKey(newCoord+" "+temp.wCoord+","+temp.hCoord)){
            if(!newCoord.equals(temp.parent)){    
                if(newElev <= Math.abs(elev)+stamina+momentum && newElev >=0){
                    legal = true;
                }else if(newElev < 0 && Math.abs(newElev) <= Math.abs(elev)){
                    legal = true;
                }
            }
        }
        return legal;
    }

    private ANode findPath(ANode temp, int lodgeNum){
        //System.out.println("add children");
        for(int i =-1; i<=1; i++){
            for(int j=-1; j<=1; j++){
                if(i!=0 || j!=0){
                    if(temp.wCoord + i >= 0 && temp.wCoord + i < elevation[0].length){
                        if(temp.hCoord+j >=0 && temp.hCoord+j <elevation.length){
                            //System.out.println((temp.wCoord+i)+","+(temp.hCoord+j));
                            if(legalMove(temp, i, j)){
                                
                                int elevCost = elevationCost(aqueue, temp.wCoord+i, temp.hCoord+j, temp.wCoord, temp.hCoord);
                                int newMomentum = Math.abs(elevation[temp.hCoord][temp.wCoord]) - Math.abs(elevation[temp.hCoord+j][temp.wCoord+i]);
                                if(newMomentum <0){newMomentum = 0;}
                                if((Math.abs(i) + Math.abs(j)) == 1){
                                    aqueue = aqueue.push(aqueue,(temp.costSoFar+10+elevCost),calcHeuristicDist(lodgeNum, temp.wCoord+i, temp.hCoord+j, newMomentum) ,temp.wCoord+i, temp.hCoord+j, temp.wCoord, temp.hCoord, temp.momentum,temp.parent, elevation[temp.hCoord+j][temp.wCoord+i], elevation[temp.hCoord][temp.wCoord]);
                                    //System.out.println("push: "+(temp.wCoord+i)+","+(temp.hCoord+j)+ "  cost: "+ (temp.priority+10));
                                }else if((Math.abs(i) + Math.abs(j)) == 2){
                                    aqueue = aqueue.push(aqueue, (temp.costSoFar+14+elevCost), calcHeuristicDist(lodgeNum, temp.wCoord+i, temp.hCoord+j, newMomentum), temp.wCoord+i, temp.hCoord+j, temp.wCoord, temp.hCoord, temp.momentum,temp.parent, elevation[temp.hCoord+j][temp.wCoord+i], elevation[temp.hCoord][temp.wCoord]);
                                    //System.out.println("push: "+(temp.wCoord+i)+","+(temp.hCoord+j)+ "  cost: "+ (temp.priority+14));
                                }else{
                                    //System.out.println("Error");
                                }
                            }
                        }
                    }
                }
            }
        }
        //System.out.println(aqueue.coord);
        return aqueue;

    }



    private String expandPath(int i){
        String result = "FAIL";
        String target = lodgeCoords[i][0]+","+lodgeCoords[i][1];
        ANode temp;
        if(pathData.containsKey(target)){
            temp = pathData.get(target);
            result = target;
            while(!temp.coord.equals(temp.parent)){
                temp = pathData.get(temp.parent+" "+temp.gparent);
                result = temp.coord+" "+result;
            }
        }
        return result;
    }


    public String[] runA(int[] start, int stamina, int lodgeCount, int[][] lodgeCoords, int[][] elevation){
        this.start = start;
        this.stamina = stamina;
        this.lodgeCount = lodgeCount;
        this.lodgeCoords = lodgeCoords;
        this.elevation = elevation;
        results = new String[lodgeCount];
        
        lodgePath = new ANode[lodgeCount];

        initA(); 
        return results;
    }

}