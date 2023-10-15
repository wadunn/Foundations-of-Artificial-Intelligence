public class PNode{
    int priority;
    int wCoord;
    int hCoord;
    int parentWCoord;
    int parentHCoord;
    int elevation;
    String coord;
    String parent;

    PNode next;

    public PNode newNode(int priority, int wCoord, int hCoord, int pwCoord, int phCoord, int elevation){
        PNode temp = new PNode();
        temp.priority = priority;
        temp.wCoord = wCoord;
        temp.hCoord = hCoord;
        temp.parentWCoord = pwCoord;
        temp.parentHCoord = phCoord;
        temp.elevation = elevation;
        temp.next = null;
        temp.coord = wCoord+ ","+hCoord;
        temp.parent = pwCoord+","+phCoord;

        return temp;
    }

    public PNode push(PNode head, int priority, int wCoord, int hCoord, int pwCoord, int phCoord, int elevation){
        PNode newNode = newNode(priority, wCoord, hCoord, pwCoord, phCoord, elevation);
        PNode tempNode = head;
        boolean dup = false;
        //System.out.println("newnode attempt");
        //add check coordinates so no repeats w/ higher priority
        if(priority < head.priority){
            newNode.next = head;
            //System.out.println(newNode.priority+" "+tempNode.priority);
            return newNode;
        }else{
            while(tempNode.next != null && priority >= tempNode.next.priority){
                if(newNode.coord.equals(tempNode.coord)){
                    dup = true;
                    break;
                }
                tempNode = tempNode.next;
            }
            if(newNode.coord.equals(tempNode.coord)){
                dup=true;
            }
            if(!dup){
                if(tempNode.next == null){
                    tempNode.next = newNode;
                }else{
                    newNode.next = tempNode.next;
                    tempNode.next = newNode;
                }
            }
        }
        //System.out.println(newNode.priority+" "+tempNode.priority);
        return head;
    }

    public PNode pop(PNode head){
        head = head.next;
        return head;
    }

    public int size(PNode head){
        int size = 0;
        PNode temp = head;
        while(temp != null){
            temp = temp.next;
            size++;
        }
        return size;
    }

}