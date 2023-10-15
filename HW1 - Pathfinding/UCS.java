import java.util.*;

public class UCS{
    int[] start;
    int stamina;
    int lodgeCount;
    int[][] lodgeCoords;
    int[][] elevation;
    
    PNode pqueue;
    PNode[] lodgePath;
    boolean[] lodgeFound;
    HashMap<String, PNode> pathData;

    public void initUCS(){
        pqueue = new PNode();
        pqueue = pqueue.newNode(0, start[0], start[1], start[0], start[1], elevation[start[1]][start[0]]);
        boolean allFound = false;
        do{
            pathData.put(pqueue.coord, pqueue);
            for(int i=0; i<lodgeCount; i++){
                if(pqueue.wCoord == lodgeCoords[i][0] && pqueue.hCoord == lodgeCoords[i][1]){
                    //Lodge Found
                    lodgeFound[i] = true;
                    if(foundCount()){
                        allFound = true;
                        break;
                    }
                }
            }
            //System.out.println(pqueue.coord+ ": "+pqueue.priority);
            pqueue = addChildren(pqueue);
            pqueue = pqueue.pop(pqueue); 
            if(allFound){break;}
        }while(pqueue!=null);
    }

    public boolean foundCount(){
        for(boolean b: lodgeFound){
            if(!b){
                return false;
            }
        }
        return true;
    }

    public boolean legalMove(PNode temp, int i, int j){
        boolean legal = false;
        int elev = temp.elevation;
        int newElev = elevation[temp.hCoord+j][temp.wCoord+i];
        String newCoord = (temp.wCoord+i)+","+(temp.hCoord+j);
        if(!pathData.containsKey(newCoord)){
            if(newElev <= Math.abs(elev)+stamina && newElev >=0){
                legal = true;
            }else if(newElev < 0 && Math.abs(newElev) <= Math.abs(elev)){
                legal = true;
            }
        }
        return legal;
    }

    public PNode addChildren(PNode temp){
        //System.out.println("add children");
        for(int i =-1; i<=1; i++){
            for(int j=-1; j<=1; j++){
                if(i!=0 || j!=0){
                    if(temp.wCoord + i >= 0 && temp.wCoord + i < elevation[0].length){
                        if(temp.hCoord+j >=0 && temp.hCoord+j <elevation.length){
                            //System.out.println((temp.wCoord+i)+","+(temp.hCoord+j));
                            if(legalMove(temp, i, j)){
                                
                                if((Math.abs(i) + Math.abs(j)) == 1){
                                    pqueue = pqueue.push(pqueue,(temp.priority+10), temp.wCoord+i, temp.hCoord+j, temp.wCoord, temp.hCoord, elevation[temp.hCoord+j][temp.wCoord+i]);
                                    //System.out.println("push: "+(temp.wCoord+i)+","+(temp.hCoord+j)+ "  cost: "+ (temp.priority+10));
                                }else if((Math.abs(i) + Math.abs(j)) == 2){
                                    pqueue = pqueue.push(pqueue, (temp.priority+14), temp.wCoord+i, temp.hCoord+j, temp.wCoord, temp.hCoord, elevation[temp.hCoord+j][temp.wCoord+i]);
                                    //System.out.println("push: "+(temp.wCoord+i)+","+(temp.hCoord+j)+ "  cost: "+ (temp.priority+14));
                                }else{
                                    System.out.println("Error");
                                }
                            }
                        }
                    }
                }
            }
        }
        return pqueue;
    }


    public String getResultPath(int wCoord, int hCoord){
        String path = wCoord+","+hCoord;
        PNode temp = pathData.get(path);
        
        if(temp == null){
            return "FAIL";
        }
        //System.out.println("get result: "+ temp.parent);
        while(!temp.coord.equals(temp.parent)){
            path = temp.parent+ " "+path;
            temp = pathData.get(temp.parent);
            //System.out.println(temp.parent);
        }

        return path;
    }

    public String[] results(){
        String[] results = new String[lodgeCount];
        for(int i=0; i<lodgeCount; i++){
            results[i] = getResultPath(lodgeCoords[i][0], lodgeCoords[i][1]);
        }
        return results;
    }


    public void runUCS(int[] start, int stamina, int lodgeCount, int[][] lodgeCoords, int[][] elevation){
        this.start = start;
        this.stamina = stamina;
        this.lodgeCount = lodgeCount;
        this.lodgeCoords = lodgeCoords;
        this.elevation = elevation;
        
        pqueue = new PNode();
        lodgePath = new PNode[lodgeCount];
        lodgeFound = new boolean[lodgeCount];
        pathData = new HashMap<String, PNode>();
        initUCS(); 
    }
}