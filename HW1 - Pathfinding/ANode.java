public class ANode{
    int costRemaining;
    int costSoFar;
    int priority;
    int wCoord;
    int hCoord;
    int parentWCoord;
    int parentHCoord;
    int elevation;
    int pElevation;
    int momentum;
    int pmomentum;
    String coord;
    String parent;
    String gparent;

    ANode next;

    public ANode newNode(int costSoFar, int costRemaining, int wCoord, int hCoord, int pwCoord, int phCoord, int pmomentum, String gparent, int elevation, int pElevation){
        ANode temp = new ANode();
        temp.costSoFar = costSoFar;
        temp.costRemaining = costRemaining;
        temp.priority = costSoFar + costRemaining;
        temp.wCoord = wCoord;
        temp.hCoord = hCoord;
        temp.parentWCoord = pwCoord;
        temp.parentHCoord = phCoord;
        temp.elevation = elevation;
        temp.pElevation = pElevation;
        temp.next = null;
        temp.coord = wCoord+ ","+hCoord;
        temp.parent = pwCoord+","+phCoord;
        temp.momentum = Math.abs(pElevation) - Math.abs(elevation);
        temp.pmomentum = pmomentum;
        temp.gparent = gparent;
        if(temp.momentum <0){temp.momentum = 0;}

        return temp;
    }

    public ANode push(ANode head, int costSF, int costLeft, int wCoord, int hCoord, int pwCoord, int phCoord, int pmomentum, String gparent, int elevation, int pElevation){
        ANode newNode = newNode(costSF, costLeft, wCoord, hCoord, pwCoord, phCoord, pmomentum, gparent, elevation, pElevation);
        ANode temANode = head;
        boolean dup = false;
        int priority = newNode.priority;
        //System.out.println("w: " + wCoord+ " h: "+hCoord+ " priority: "+priority + " soFar: "+costSF+ " h: "+costLeft);
        //System.out.println("newnode attempt");
        //add check coordinates so no repeats w/ higher priority
        if(head == null){
            return newNode;
        }
        if(priority < head.priority){
            newNode.next = head;
            //System.out.println(newNode.priority+" "+temANode.priority);
            return newNode;
        }else{
            while(temANode.next != null && priority >= temANode.next.priority){
                if(newNode.coord.equals(temANode.coord)){
                    dup = true;
                    break;
                }
                temANode = temANode.next;
            }
            if(newNode.coord.equals(temANode.coord)){
                dup=true;
            }
            if(!dup){
                if(temANode.next == null){
                    temANode.next = newNode;
                }else{
                    newNode.next = temANode.next;
                    temANode.next = newNode;
                }
            }
        }
        //System.out.println(newNode.priority+" "+temANode.priority);
        return head;
    }

    public ANode pop(ANode head){
        head = head.next;
        return head;
    }

    public int size(ANode head){
        int size = 0;
        ANode temp = head;
        while(temp != null){
            temp = temp.next;
            size++;
        }
        return size;
    }

}