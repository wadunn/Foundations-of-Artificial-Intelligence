import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;



public class homework {
    
    //Priority Moves
    String bwin1 = ".bbbb";
    String bwin2 = "bbbb.";
    String bwin3 = "b.bbb";
    String bwin4 = "bb.bb";
    String bwin5 = "bbb.b";
    String wwin1 = ".wwww";
    String wwin2 = "wwww.";
    String wwin3 = "w.www";
    String wwin4 = "ww.ww";
    String wwin5 = "www.w";
    String b3 = ".bbb.";
    String b3l = ".b.bb.";
    String b3r = ".bb.b.";
    String b3c = "b.b.b";
    String w3 = ".www.";
    String w3l = ".w.ww.";
    String w3r = ".ww.w.";
    String w3c = "w.w.w";
    String wcl = ".bbw";
    String wcr = "wbb.";
    String bcl = ".wwb";
    String bcr = "bww.";
    String b4l = ".bbb";
    String b4r = "bbb.";
    String w4l = ".www";
    String w4r = "www.";
    String w2 = ".ww.";
    String b2 = ".bb.";

    //Variables
    
    int minMaxDepth = 2;
    static String color; // line1
    int colorNum;
    int opponentColor;
    float timeRemaining;
    int[] captures;
    String[][] board;
    String myToken;
    String opponentToken;
    String[][] finalBoard;
    String[] path;
    int pruneCount = 0;

    String[][] testBoard;
    key testKey;

    int searchCheck = 0;
    Map<key, Integer> gameRecords;

    private class key{
        String[][] gb;

        public key(String[][] gb){
            this.gb = gb;
        }

        @Override
        public boolean equals(Object obj){
            if(this == obj){
                return true;
            }
            if(obj == null || getClass() != obj.getClass()){
                return false;
            }
            key hkey = (key) obj;
            return Arrays.deepEquals(gb, hkey.gb);
        }

        @Override
        public int hashCode(){
            return Arrays.deepHashCode(gb);
        }
    }
    //**** Input/Output Functions *** */

    private void printStringArray(String[][] array){
        for(int j=0; j<array.length; j++){
            for(int i =0; i< array.length; i++){
                System.out.print(array[j][i] + " ");
            }
            System.out.println();
        }
    }
    
    public void printInput(){
        System.out.println(color);
        System.out.println(timeRemaining);
        printStringArray(board);
    }

    public int[] lineParse(int size, String line){
        int[] nums =  new int[size];
        String[] split = line.split(",");
        for(int i =0; i<size; i++){
            nums[i] = Integer.valueOf(split[i]); 
        }

        return nums;
    }

    private String[] stringParse(String line){
        String[] split =  new String[line.length()];
        for(int i=0; i<line.length(); i++){
            split[i] = line.substring(i,i+1);
        }
        return split;
    }

    public void readInput(){
        try{
            File inputFile = new File("./input.txt");
            Scanner sc = new Scanner(inputFile);
            //First Line
            color = sc.nextLine();
            if( color.equals("WHITE")){ 
                colorNum=0;
                myToken = "w";
                opponentToken = "b";
            }else if(color.equals("BLACK")){
                colorNum=1;
                myToken = "b";
                opponentToken = "w";
            }
            opponentColor = 1-colorNum;
            timeRemaining = Float.parseFloat(sc.nextLine());
            captures = lineParse(2, sc.nextLine());
            board = new String[19][19];
            for(int i=0; i<19; i++){
                board[i] = stringParse(sc.nextLine());
            }
            //Should be no lines remaining
            if(sc.hasNextLine()){
                System.out.println(sc.nextLine());
                //sc.nextLine();
            }
            sc.close();
        }catch (Exception e){
            //System.out.println("File not Found: readInput");
        }
    }

    public void writeOutput(String results){
        try{
        File output = new File("output.txt");
        output.createNewFile();
        FileWriter writer = new FileWriter("output.txt");
        writer.write(results);
        writer.close();
        }catch(IOException e){
            System.out.println("output.txt not created");
        }
    }

    public void printFinalMove(int[] move, String[][] newBoard){
        finalBoard = copyBoard(board, newBoard);
        finalBoard[move[0]][move[1]] = myToken;
        int[] finalCap = captures;
        captures = checkCapture(finalBoard, move, captures, finalCap);
        printStringArray(finalBoard);
    }
    
    //****** AI Components */

    private String moveToString(int[] move){
        String result;
        String columns = "ABCDEFGHJKLMNOPQRST";
        result = String.valueOf(19-move[0])+ columns.substring(move[1],move[1]+1);
        return result;
    }
    
    private boolean areaCheck(String[][] gboard, int[] move){
        for(int i = -2; i<=2; i++){
            for(int j = -2; j<=2; j++){
                if(i!=0 || j!=0){
                    if(move[0]+i >=0 && move[0]+i <19 && move[1]+j >=0 && move[1]+j <19){
                        if(checkToken(gboard,move[0]+i, move[1]+j) != 0){
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public int checkFive(String[][] gBoard, int[] move){
        int row = 1;
        String token = (gBoard[move[0]][move[1]]);
        //System.out.println(token);
        for(int i = -1; i<=1; i++){
            for(int j =-1; j<=1; j++){
                if(move[0]+i >=0 && move[0]+i <19 && move[1]+j >=0 && move[1]+j <19 && !(j==0 && i==0)){
                    if(token.equals(gBoard[move[0]+i][move[1]+j])){
                        boolean fwd = false;
                        boolean bwd = false;
                        for(int k =1; k<=4; k++){
                            if(fwd && bwd){
                                break;
                            }
                            if(move[0]+i*k >=0 && move[0]+i*k <19 && move[1]+j*k >=0 && move[1]+j*k <19){
                                if(token.equals(gBoard[move[0]+i*k][move[1]+j*k]) &&!fwd){
                                    row++;
                                }else{
                                    fwd = true;
                                }
                            }
                            if(move[0]+i*-k >=0 && move[0]+i*-k <19 && move[1]+j*-k >=0 && move[1]+j*-k <19){
                                if(token.equals(gBoard[move[0]+i*-k][move[1]+j*-k]) && !bwd){
                                    row++;
                                }else{
                                    bwd = true;
                                }
                            }
                        }
                    }
                    if(row>= 5){
                        return row;
                    }else{
                        row=1;
                    }
                }
            }
        }
        return row;
    }

    private int checkWinLose(String[][] gBoard, int[] gcap, int[] move){
        if(checkFive(gBoard, move)>=5){
            return 1;
        }
        if(gcap[0] >=5 || gcap[1]>= 5){
            return 1;
        }else{
            return 0;
        }
    }

    private int[] checkCapture(String[][]gBoard, int[] move, int[] gcap, int[] tempCap){
        tempCap = Arrays.copyOf(gcap,2);
        int token = checkToken(gBoard, move[0], move[1]);
        int index;
        if(gBoard[move[0]][move[1]].equals("w")){
            index = 0;
        }else{
            index = 1;
        }
        for(int i =-1; i<= 1; i++){
            for(int j=-1; j<=1; j++){
                if(!(i==0 && j==0)){
                    if(move[0]+i*3 >=0 && move[0] +i* 3 <19 && move[1] +j*3 >=0 && move[1] +j*3 <19){
                        if(checkToken(gBoard, move[0]+ i, move[1]+j) == (-1)*token){
                            if(checkToken(gBoard, move[0]+ 2*i, move[1]+2*j) == (-1)*token){
                                if(checkToken(gBoard, move[0]+ 3*i, move[1]+3*j) == token){
                                    tempCap[index]++;
                                    gBoard[move[0]+i][move[1]+j] = ".";
                                    gBoard[move[0]+2*i][move[1]+2*j]=".";
                                }
                            }
                        }
                    }
                }
            }
        }
        //System.out.println(tempCap[0]+" "+tempCap[1]);
        return tempCap;
    }

    private int checkToken(String[][] gBoard, int y, int x){
        if(gBoard[y][x].equals(myToken)){
            return 1;
        }else if(gBoard[y][x].equals(opponentToken)){
            return -1;
        }else{
            return 0;
        }
    }
    
    public int CalcValue(String[][] gBoard, int[] gcap, int[] move){
        //int token = checkToken(gBoard,move[0],move[1]);
        int score = 0;  
        if(gcap[colorNum] >= 5){
            score+= 1000;
        }else if(gcap[opponentColor] >= 5){
            score+= -1000;
        }
        if(checkFive(gBoard, move)>=5){
            
            score+=  1000*checkToken(gBoard, move[0], move[1]);
        }

        //Points for captures
        score+= gcap[colorNum]*10;
        score-= gcap[1-colorNum]*10;
        
        for(int i=0; i<19; i++ ){
            for(int j=0; j<15; j++){
                //Check horizontal for potential 5 in a row
                //1 point for each contributing token
                int rowScore = 0;
                int rowCount = 0;
                int empties = 0;
                while((rowCount <=5 || rowScore ==0) && j<19 ){
                    int val = checkToken(gBoard,i, j);
                    if(val==0 && rowScore ==0){
                        j++;
                        rowCount++;
                        empties++;
                    }else if((val>=0 && rowScore >= 0)||(val<=0 && rowScore <=0)){
                        rowScore += val;
                        j++;
                        rowCount++;
                        if(val == 0){
                            empties++;
                            //System.out.println(i+" "+j);
                        }else{
                            empties = 0;
                        }
                    }else if((val<0 && rowScore>0)||(val>0 && rowScore<0)){
                        rowScore = 0;
                        rowCount = 0; 
                        empties = 0;                       
                    }
                    if(rowCount >= 5 && rowScore !=0){
                        score += rowScore*rowScore;
                        rowCount =0;
                        rowScore = 0;
                        if(empties>0){
                            j -= empties;
                        }else{
                            j--;
                        }
                        empties = 0;
                        //System.out.println(i+","+(j-1) + " : "+ score);
                    }
                    //if(rowScore !=0){System.out.println(rowScore + " "+ i+","+j);}
                }

            }
        }

        //Check Vertical for potential 5 in a row
        //1 point for each contributing token
        for(int i=0; i<19; i++ ){
            for(int j=0; j<15; j++){
                int rowScore = 0;
                int rowCount = 0;
                int empties = 0;
                while((rowCount <=5 || rowScore ==0) && j<19 ){
                    int val = checkToken(gBoard, i, j);
                    if(val==0 && rowScore ==0){
                        j++;
                        rowCount++;
                        empties++;
                    }else if((val>=0 && rowScore >= 0)||(val<=0 && rowScore <=0)){
                        rowScore += val;
                        j++;
                        rowCount++;
                        if(val == 0){
                            empties++;
                        }else{
                            empties = 0;
                        }
                    }else if((val<0 && rowScore>0)||(val>0 && rowScore<0)){
                        rowScore = 0;
                        rowCount = 0; 
                        empties = 0;                       
                    }
                    if(rowCount >= 5 && rowScore !=0){
                        score += rowScore*rowScore;
                        rowCount =0;
                        rowScore = 0;
                        if(empties>0){
                            j -= empties;
                        }else{
                            j--;
                        }
                        empties = 0;
                    }
                }

            }
        }

        return score;
    }

    public String[][] copyBoard(String[][] gBoard, String[][] newBoard){
        //String[][] newBoard;// = new String[19][19];
        String[][] oldBoard = gBoard;
        for(int i =0; i<19; i++){
            newBoard[i] = Arrays.copyOf(oldBoard[i],oldBoard[i].length );
        }
        return newBoard;
    }

    private int maxValue(String[][] gBoard, int[] cap, int depth, int alpha, int beta){
        //Search Params are [y coordinate, x coordinate, alpha value, beta value]
        int center = 9;
        int maxValue = alpha;
        int tempValue =0;
        int radius=0;
        String[][] tempBoard = new String[19][19];
        int[] tempCap = new int[2];
        String[][] keyBoard = new String[19][19];
        int keyValue = 0;
        //String[][] tempBoard = new String[19][19];
        boolean prune = false;
        int[] move = new int[2];
        while(radius <= center){
            //i is row # or y-direction
            for(int i=-radius; i<=radius; i++){
                // j is col# or x-direction
                if(prune){break;}
                for(int j=-radius; j<=radius; j++){
                    if(prune){break;}
                    if(radius==Math.abs(i) || radius==Math.abs(j)){
                        if(checkToken(gBoard,center+i,center+j) == 0){
                            tempBoard = copyBoard(gBoard, tempBoard);                            
                            //System.out.println("y: "+(center+i)+" x: "+(center+j));
                            tempBoard[center+i][center+j] = myToken;
                            //printStringArray(tempBoard);
                            move[0] = center+i;
                            move[1] = center+j;
                            tempCap = checkCapture(tempBoard, move, cap, tempCap);
                            key tempKey = new key(tempBoard);
                            if(gameRecords.containsKey(tempKey)){
                                tempValue =  gameRecords.get(tempKey);//.gameScore;
                            }else if(!areaCheck(tempBoard, move)){
                                
                                if(checkWinLose(tempBoard, tempCap, move) == 1){
                                    tempValue = /*CalcValue(tempBoard, tempCap, move);*/1000 + minMaxDepth - depth+1;
                                    //return tempValue;
                                } else if(depth >= minMaxDepth){
                                    tempValue = CalcValue(tempBoard, tempCap, move);
                                }else{
                                    tempValue = minValue(tempBoard,tempCap, depth+1, alpha, beta);
                                    searchCheck++;
                                    //if(checkToken(gBoard.getBoard(), 9,8 ) ==1 && tempValue>=1000){System.out.println("depth: "+depth+ " val: " +tempValue + " Max: "+ move[0]+" "+move[1]);}
                                }
                                //tempGame.updateScore(tempValue);
                                //if(tempKey.equals(testKey)){System.out.println("max match");}
                                //gameRecords.put(tempKey, tempValue);//tempGame);
                                //gBoard.addChild(tempGame);
                                
                            }
                            keyBoard = copyBoard(tempBoard, keyBoard);
                            keyValue = Integer.valueOf(tempValue);
                            gameRecords.put(new key(keyBoard), keyValue);

                            if(tempValue >= beta){
                                maxValue = tempValue;
                                prune = true;
                                //pruneCount++;
                                //System.out.println("prune max: "+ pruneCount );
                            }
                            if(tempValue >=1000){
                                prune = true;
                                break;
                            }
                            if(tempValue > alpha){
                                alpha = tempValue;
                            }
                            if(tempValue > maxValue){
                                maxValue = tempValue;
                            }

            
                        }
                        
                    }
                }
            }
            radius++;
        }
        //System.out.println("max "+maxValue);
        //if(checkToken(gBoard, 7,9)==1){System.out.println("depth: "+depth+" max: "+maxValue);}
        return maxValue;
    }
    
    private int minValue(String[][] gBoard, int[] cap, int depth, int alpha, int beta){
        int center = 9;
        int tempValue =0;
        int minValue = beta;
        String[][] tempBoard = new String[19][19];
        String[][] keyBoard = new String[19][19];
        int keyValue = 0;
        int[] tempCap = new int[2];
        //Search Params are [y coordinate, x coordinate, alpha value, beta value]
        int radius=0;
        boolean prune = false;
        int[] move = new int[2];
        while(radius <=center){    
            //i is row # or y-direction
            for(int i=-radius; i<=radius; i++){
                if(prune){break;}
                // j is col# or x-direction
                for(int j=-radius; j<=radius; j++){
                    if(prune){break;}
    
                    if(radius==Math.abs(i) || radius==Math.abs(j)){
                        if(checkToken(gBoard,center+i,center+j) == 0){
                            
                            tempBoard = copyBoard(gBoard, tempBoard);
                            tempBoard[center+i][center+j] = opponentToken;
                            move[0] = center+i;
                            move[1] = center+j;
                            tempCap = checkCapture(tempBoard, move, cap, tempCap);
                            key tempKey = new key(tempBoard);
                            if(tempCap[opponentColor]>captures[opponentColor]){
                                tempValue = -50;
                            }else if(gameRecords.get(tempKey)!= null){
                                tempValue = gameRecords.get(tempKey);
                                if(tempKey.equals(testKey)){System.out.println("here gamerecord "+ new key(tempBoard) + "value: "+tempValue);}
                            }else if(checkWinLose(tempBoard, tempCap, move) == 1){
                                    tempValue = -1000 -minMaxDepth + depth-1;
                                    //printStringArray(tempBoard);
                                    //gameRecords.put(tempKey, tempValue);
                                    //return tempValue;
                            }else if(!areaCheck(tempBoard, move)){
                                if(depth >= minMaxDepth){
                                    tempValue = CalcValue(tempBoard, tempCap, move);
                                }else{
                                    tempValue = maxValue(tempBoard,tempCap, depth+1, alpha, beta);
                                    searchCheck++;
                                }
                                //gameRecords.put(tempKey, tempValue);//tempGame);
                            }
                            keyBoard = copyBoard(tempBoard, keyBoard);
                            keyValue = Integer.valueOf(tempValue);
                            gameRecords.put(new key(keyBoard), keyValue);
                            if(tempValue <= alpha){
                                minValue = tempValue;
                                prune = true;
                                //pruneCount++;
                                //System.out.println("prune" + pruneCount);
                            }
                            if(tempValue <= -1000){
                                prune =true;
                                break;
                            }
                            if(tempValue < beta){
                                beta = tempValue;
                            }
                            if(tempValue < minValue){
                                minValue = tempValue;
                            }
                        }
                    }
                }
            }
            radius++;
        }
        return minValue;
    }


    public int[] AlphaBetaSearch(){
        //char[][] gBoard;
        //int[] gcap;
        int maxValue = -100000;
        int[] maxMove = new int[2];
        int beta = 100000;
        //begin search in middle of board
        int radius=0;
        int center =9;
        int[] move = new int[2];
        String[][] tempBoard = new String[19][19];
        int[] tempCap = new int[2];
    
        while(radius <= center){
            //i is row # or y-direction
            for(int i=-radius; i<=radius; i++){
                // j is col# or x-direction
                for(int j=-radius; j<=radius; j++){
                    if(radius==Math.abs(i) || radius==Math.abs(j)){
                        if(checkToken(board,center+i,center+j) == 0){
                            tempBoard = copyBoard(board, tempBoard);
                            tempBoard[center+i][center+j] = myToken;
                            move[0] = center+i;
                            move[1] = center+j;
                            if(!areaCheck(tempBoard, move)){
                                tempCap = checkCapture(tempBoard, move, captures, tempCap);

                                if(checkWinLose(tempBoard, tempCap, move) == 1){
                                    System.out.println("win: " +move[0]+ " "+move[1] + " caps: "+ tempCap[0] +" "+tempCap[1]);
                                    return move;
                                }else{
                                    //printStringArray(tempBoard);
                                    int tempValue = minValue(tempBoard,tempCap, 1, maxValue, beta);
                                    if(tempValue > maxValue){
                                        maxValue = tempValue;
                                        maxMove[0] = center+i;
                                        maxMove[1] = center+j;
                                        //path[0] = moveToString(maxMove);
                                        System.out.println(maxMove[0]+" "+maxMove[1]+ ": "+tempValue);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            radius++;
        }

        return maxMove;
    }


    public int[] findMove(int priority, int row, int col, String dir, String check){
        int[] move =  new int[2];
        move[0] = row;
        move[1] = col;
        int index;
        int position = 0;
        if(priority == 8 || priority ==7){
            index = check.indexOf(bwin1);
            if(check.indexOf(bwin1) >=0){
                index = check.indexOf(bwin1);
                position = index;
            }else if(check.indexOf(wwin1) >=0){
                index = check.indexOf(wwin1);
                position = index;
            }else if(check.indexOf(bwin2) >=0){
                index = check.indexOf(bwin2);
                position = index+4;
            }else if(check.indexOf(wwin2) >=0){
                index = check.indexOf(wwin2);
                position = index+4;
            }else if(check.indexOf(bwin3) >=0){
                index = check.indexOf(bwin3);
                position = index+1;
            }else if(check.indexOf(wwin3)>=0){
                index = check.indexOf(wwin3);
                position = index+1;
            }else if(check.indexOf(bwin4) >=0){
                index = check.indexOf(bwin4);
                position = index+2;
            }else if(check.indexOf(wwin4) >=0){
                index = check.indexOf(wwin4);
                position = index+2;
            }else if(check.indexOf(bwin5)>=0){
                position = check.indexOf(bwin5)+3;
            }else if(check.indexOf(wwin5) >=0){
                position = check.indexOf(wwin5)+3;
            }else if(check.indexOf(wcl) >=0){
                position = check.indexOf(wcl);
            }else if(check.indexOf(bcl) >=0){
                position = check.indexOf(bcl);
            }else if(check.indexOf(wcr) >=0){
                position = check.indexOf(wcr)+3;
            }else if(check.indexOf(bcr) >=0){
                position = check.indexOf(bcr)+3;
            }
        }else if(priority >4){
            if(check.indexOf(b3)>=0){
                position = check.indexOf(b3);
            }else if(check.indexOf(b3l)>=0){
                position = check.indexOf(b3l)+2;
            }else if(check.indexOf(b3r)>=0){
                position = check.indexOf(b3r)+3;
            }else if(check.indexOf(b3c)>=0){
                position = check.indexOf(b3c)+1;
            }else if(check.indexOf(w3)>=0){
                position = check.indexOf(w3);
            }else if(check.indexOf(w3l)>=0){
                position = check.indexOf(w3l)+2;
            }else if(check.indexOf(w3r)>=0){
                position = check.indexOf(w3r)+3;
            }else if(check.indexOf(w3c)>=0){
                position = check.indexOf(w3c)+1;
            }
        }else if(priority >2){
            if(check.indexOf(wcl)>=0){
                position = check.indexOf(wcl);
            }else if(check.indexOf(bcl)>=0){
                position = check.indexOf(bcl);
            }else if(check.indexOf(wcr)>=0){
                position = check.indexOf(wcr)+3;
            }else if(check.indexOf(bcr)>=0){
                position = check.indexOf(bcr)+3;
            }
        }else if(priority ==2){
            if(check.indexOf(b4l)>=0){
                position = check.indexOf(b4l);
            }else if(check.indexOf(b4r)>=0){
                position = check.indexOf(b4r)+3;
            }else if(check.indexOf(w4l)>=0){
                position = check.indexOf(w4l);
            }else if(check.indexOf(w4r)>=0){
                position = check.indexOf(w4r)+3;
            }
        }else if(priority ==1){
            if(check.indexOf(b2)>=0){
                position = check.indexOf(b2);
            }else if(check.indexOf(w2)>=0){
                position = check.indexOf(w2);
            }
        }


        if(dir.equals("ROW")){
            move[1] = position;
        }else if(dir.equals("COL")){
            move[0] = position;
        }else if(dir.equals("UPRIGHT")){
            move[0] = move[0] - position;
            move[1] = move[1] +position;
        }else if(dir.equals("DOWNRIGHT")){
            move[0] = move[0] + position;
            move[1] = move[1] +position;
        }
        return move;
    }

    public boolean checkFirstMove(){
        for(int i =0;i<19; i++){
            for(int j=0; j<19; j++){
                if(!board[i][j].equals(".")){
                    return false;
                }
            }
        }
        return true;
    }

    public boolean checkSecondMove(){
        int count = 0;
        for(int i =0;i<19; i++){
            for(int j=0; j<19; j++){
                if(!board[i][j].equals(".")){
                    count++;
                }
            }
        }
        if(count ==2){return true;}
        return false;
    }

    public int[] prioChecks(){
        int[] move =new int[2];
        int priority = 0;
        move[0] =-1; move[1] = -1;
        if(checkFirstMove()){
            move[0]=9;
            move[1] = 9;
            return move;
        }
        if(checkSecondMove()){
            if(board[9][6].equals(".")){
                move[0]=9;
                move[1]=6;
                return move;
            }else{
                move[0] = 9;
                move[1] = 12;
            }
        }
        //Check Rows
        String dir = "ROW";
        for(int i = 0; i<19; i++){
            String check = "";
            for(int j=0; j<19; j++){
                check = check + board[i][j];
            }
            if(check.contains(wwin1) || check.contains(wwin2) || check.contains(wwin3) || check.contains(wwin4) || check.contains(wwin5) || ((check.contains(wcl)||check.contains(wcr)) && captures[0] == 4)){
                if(myToken.equals("w")){
                    priority = 8;
                    move = findMove(8, i, -1, dir, check);
                    return move;
                }else if(myToken.equals("b") && priority <7){
                    priority = 7;
                    move = findMove(7,i,-1,dir, check);
                }
            }
            if(check.contains(bwin1) || check.contains(bwin2) || check.contains(bwin3) || check.contains(bwin4) || check.contains(bwin5) ||((check.contains(bcl)||check.contains(bcr)) && captures[1] ==4)){
                if(myToken.equals("b")){
                    priority = 8;
                    move = findMove(8, i, -1, dir, check);
                    return move;
                }else if(myToken.equals("w") && priority<7){
                    priority = 7;
                    move = findMove(7,i,-1,dir, check);
                }
            }
            if((check.contains(b3) || check.contains(b3l) || check.contains(b3r) || check.contains(b3c)) && priority<7){
                if(myToken.equals("b")){
                    priority = 6;
                    move = findMove(6, i, -1, dir, check);
                }else if(myToken.equals("w")){
                    priority = 5;
                    move = findMove(priority, i, -1,dir, check);
                }
            }
            if((check.contains(w3) || check.contains(w3l) || check.contains(w3r) || check.contains(w3c)) && priority<7){
                if(myToken.equals("w")){
                    priority = 6;
                    move = findMove(6, i, -1, dir, check);
                }else if(myToken.equals("b")){
                    priority = 5;
                    move = findMove(priority, i, -1,dir, check);
                }
            }
            if((check.contains(bcl)|| check.contains(bcr)) && priority <5){
                if(myToken.equals("b")){
                    priority = 4;
                    move = findMove(priority, i, -1, dir, check);
                }else if(myToken.equals("w")){
                    priority = 3;
                    move = findMove(priority, i, -1, dir, check);
                }
            }
            if((check.contains(wcl)|| check.contains(wcr)) && priority <5){
                if(myToken.equals("w")){
                    priority = 4;
                    move = findMove(priority, i, -1, dir, check);
                }else if(myToken.equals("b")){
                    priority = 3;
                    move = findMove(priority, i, -1, dir, check);
                }
            }
            if((check.contains(b4l)||check.contains(b4r))&& priority <3){
                if(myToken.equals("b")){
                    priority =2;
                    move = findMove(priority,i ,-1, dir, check);
                }
            }
            if((check.contains(w4l)||check.contains(w4r))&& priority <3){
                if(myToken.equals("w")){
                    priority =2;
                    move = findMove(priority,i ,-1, dir, check);
                }
            }
            if((check.contains(b2))&& priority <2){
                if(myToken.equals("w")){
                    priority =1;
                    move = findMove(priority,i ,-1, dir, check);
                }
            }
            if((check.contains(w2))&& priority <2){
                if(myToken.equals("b")){
                    priority =1;
                    move = findMove(priority,i ,-1, dir, check);
                }
            }
        }

        //Check Columns
        dir = "COL";
        for(int i = 0; i<19; i++){
            String check = "";
            for(int j=0; j<19; j++){
                check = check + board[j][i];
            }
            if(check.contains(wwin1) || check.contains(wwin2) || check.contains(wwin3) || check.contains(wwin4) || check.contains(wwin5) || ((check.contains(wcl)||check.contains(wcr)) && captures[0] == 4)){
                if(myToken.equals("w")){
                    priority = 8;
                    move = findMove(8, -1, i, dir, check);
                    return move;
                }else if(myToken.equals("b") && priority <7){
                    priority = 7;
                    move = findMove(7,-1,i,dir, check);
                }
            }
            if(check.contains(bwin1) || check.contains(bwin2) || check.contains(bwin3) || check.contains(bwin4) || check.contains(bwin5) ||((check.contains(bcl)||check.contains(bcr)) && captures[1] ==4)){
                if(myToken.equals("b")){
                    priority = 8;
                    move = findMove(8,  -1,i, dir, check);
                    return move;
                }else if(myToken.equals("w") && priority<7){
                    priority = 7;
                    move = findMove(7,-1,i,dir, check);
                }
            }
            if((check.contains(b3) || check.contains(b3l) || check.contains(b3r) || check.contains(b3c)) && priority<7){
                if(myToken.equals("b")){
                    priority = 6;
                    move = findMove(6,  -1,i, dir, check);
                }else if(myToken.equals("w")){
                    priority = 5;
                    move = findMove(priority,  -1,i,dir, check);
                }
            }
            if((check.contains(w3) || check.contains(w3l) || check.contains(w3r) || check.contains(w3c)) && priority<7){
                if(myToken.equals("w")){
                    priority = 6;
                    move = findMove(6,  -1,i, dir, check);
                }else if(myToken.equals("b")){
                    priority = 5;
                    move = findMove(priority,  -1,i,dir, check);
                }
            }
            if((check.contains(bcl)|| check.contains(bcr)) && priority <5){
                if(myToken.equals("b")){
                    priority = 4;
                    move = findMove(priority,  -1,i, dir, check);
                }else if(myToken.equals("w")){
                    priority = 3;
                    move = findMove(priority,  -1,i, dir, check);
                }
            }
            if((check.contains(wcl)|| check.contains(wcr)) && priority <5){
                if(myToken.equals("w")){
                    priority = 4;
                    move = findMove(priority, -1, i, dir, check);
                }else if(myToken.equals("b")){
                    priority = 3;
                    move = findMove(priority,  -1,i, dir, check);
                }
            }
            if((check.contains(b4l)||check.contains(b4r))&& priority <3){
                if(myToken.equals("b")){
                    priority =2;
                    move = findMove(priority,-1 ,i, dir, check);
                }
            }
            if((check.contains(w4l)||check.contains(w4r))&& priority <3){
                if(myToken.equals("w")){
                    priority =2;
                    move = findMove(priority,-1 ,i, dir, check);
                }
            }
            if((check.contains(b2))&& priority <2){
                if(myToken.equals("w")){
                    priority =1;
                    move = findMove(priority,-1,i, dir, check);
                }
            }
            if((check.contains(w2))&& priority <2){
                if(myToken.equals("b")){
                    priority =1;
                    move = findMove(priority,-1 ,i, dir, check);
                }
            }
        }
        
        //Up Right Diagonal
        
        dir = "UPRIGHT";
        int i = 4;
        int j = 0;
        String check;
        while(i <19 && j<19){
            check = "";
            int adder = 0;
            while((i-adder)>=0 && (i-adder)<19 && (j+adder)>=0 && (j+adder)<19){
                check = check +board[i-adder][j+adder];
                adder++;
            }
            if(check.contains(wwin1) || check.contains(wwin2) || check.contains(wwin3) || check.contains(wwin4) || check.contains(wwin5) || ((check.contains(wcl)||check.contains(wcr)) && captures[0] == 4)){
                if(myToken.equals("w")){
                    priority = 8;
                    move = findMove(8, i, j, dir, check);
                    return move;
                }else if(myToken.equals("b") && priority <7){
                    priority = 7;
                    move = findMove(7,i,j,dir, check);
                }
            }
            if(check.contains(bwin1) || check.contains(bwin2) || check.contains(bwin3) || check.contains(bwin4) || check.contains(bwin5) ||((check.contains(bcl)||check.contains(bcr)) && captures[1] ==4)){
                if(myToken.equals("b")){
                    priority = 8;
                    move = findMove(8, i, j, dir, check);
                    return move;
                }else if(myToken.equals("w") && priority<7){
                    priority = 7;
                    move = findMove(7,i,j,dir, check);
                }
            }
            if((check.contains(b3) || check.contains(b3l) || check.contains(b3r) || check.contains(b3c)) && priority<7){
                if(myToken.equals("b")){
                    priority = 6;
                    move = findMove(6, i, j, dir, check);
                }else if(myToken.equals("w")){
                    priority = 5;
                    move = findMove(priority, i, j,dir, check);
                }
            }
            if((check.contains(w3) || check.contains(w3l) || check.contains(w3r) || check.contains(w3c)) && priority<7){
                if(myToken.equals("w")){
                    priority = 6;
                    move = findMove(6, i, j, dir, check);
                }else if(myToken.equals("b")){
                    priority = 5;
                    move = findMove(priority, i, j,dir, check);
                }
            }
            if((check.contains(bcl)|| check.contains(bcr)) && priority <5){
                if(myToken.equals("b")){
                    priority = 4;
                    move = findMove(priority, i, j, dir, check);
                }else if(myToken.equals("w")){
                    priority = 3;
                    move = findMove(priority, i, j, dir, check);
                }
            }
            if((check.contains(wcl)|| check.contains(wcr)) && priority <5){
                if(myToken.equals("w")){
                    priority = 4;
                    move = findMove(priority, i, j, dir, check);
                }else if(myToken.equals("b")){
                    priority = 3;
                    move = findMove(priority, i, j, dir, check);
                }
            }
            if((check.contains(b4l)||check.contains(b4r))&& priority <3){
                if(myToken.equals("b")){
                    priority =2;
                    move = findMove(priority,i ,j, dir, check);
                }
            }
            if((check.contains(w4l)||check.contains(w4r))&& priority <3){
                if(myToken.equals("w")){
                    priority =2;
                    move = findMove(priority,i ,j, dir, check);
                }
            }
            if((check.contains(b2))&& priority <2){
                if(myToken.equals("w")){
                    priority =1;
                    move = findMove(priority,i ,j, dir, check);
                }
            }
            if((check.contains(w2))&& priority <2){
                if(myToken.equals("b")){
                    priority =1;
                    move = findMove(priority,i ,j, dir, check);
                }
            }

            if(i<18){
                i++;
            }else{
                j++;
            }
        }

        //Up Right Diagonal

        dir = "DOWNRIGHT";
        i = 18-3;
        j = 0;
        while(i <19 && j<19){
            check = "";
            int adder = 0;
            while((i+adder)>=0 && (i+adder)<19 && (j+adder)>=0 && (j+adder)<19){
                check = check +board[i+adder][j+adder];
                adder++;
            }
            if(check.contains(wwin1) || check.contains(wwin2) || check.contains(wwin3) || check.contains(wwin4) || check.contains(wwin5) || ((check.contains(wcl)||check.contains(wcr)) && captures[0] == 4)){
                if(myToken.equals("w")){
                    priority = 8;
                    move = findMove(8, i, j, dir, check);
                    return move;
                }else if(myToken.equals("b") && priority <7){
                    priority = 7;
                    move = findMove(7,i,j,dir, check);
                }
            }
            if(check.contains(bwin1) || check.contains(bwin2) || check.contains(bwin3) || check.contains(bwin4) || check.contains(bwin5) ||((check.contains(bcl)||check.contains(bcr)) && captures[1] ==4)){
                if(myToken.equals("b")){
                    priority = 8;
                    move = findMove(8, i, j, dir, check);
                    return move;
                }else if(myToken.equals("w") && priority<7){
                    priority = 7;
                    move = findMove(7,i,j,dir, check);
                }
            }
            if((check.contains(b3) || check.contains(b3l) || check.contains(b3r) || check.contains(b3c)) && priority<7){
                if(myToken.equals("b")){
                    priority = 6;
                    move = findMove(6, i, j, dir, check);
                }else if(myToken.equals("w")){
                    priority = 5;
                    move = findMove(priority, i, j,dir, check);
                }
            }
            if((check.contains(w3) || check.contains(w3l) || check.contains(w3r)|| check.contains(w3c)) && priority<7){
                if(myToken.equals("w")){
                    priority = 6;
                    move = findMove(6, i, j, dir, check);
                }else if(myToken.equals("b")){
                    priority = 5;
                    move = findMove(priority, i, j,dir, check);
                }
            }
            if((check.contains(bcl)|| check.contains(bcr)) && priority <5){
                if(myToken.equals("b")){
                    priority = 4;
                    move = findMove(priority, i, j, dir, check);
                }else if(myToken.equals("w")){
                    priority = 3;
                    move = findMove(priority, i, j, dir, check);
                }
            }
            if((check.contains(wcl)|| check.contains(wcr)) && priority <5){
                if(myToken.equals("w")){
                    priority = 4;
                    move = findMove(priority, i, j, dir, check);
                }else if(myToken.equals("b")){
                    priority = 3;
                    move = findMove(priority, i, j, dir, check);
                }
            }
            if((check.contains(b4l)||check.contains(b4r))&& priority <3){
                if(myToken.equals("b")){
                    priority =2;
                    move = findMove(priority,i ,j, dir, check);
                }
            }
            if((check.contains(w4l)||check.contains(w4r))&& priority <3){
                if(myToken.equals("w")){
                    priority =2;
                    move = findMove(priority,i ,j, dir, check);
                }
            }
            if((check.contains(b2))&& priority <2){
                if(myToken.equals("w")){
                    priority =1;
                    move = findMove(priority,i ,j, dir, check);
                }
            }
            if((check.contains(w2))&& priority <2){
                if(myToken.equals("b")){
                    priority =1;
                    move = findMove(priority,i ,j, dir, check);
                }
            }

            if(i>0){
                i--;
            }else{
                j++;
            }
        }
        //Check Win
        return move;
    }

    /* 
        Heurisitc Hierarchy:
        8: score 5
        8: make 5th capture
        7: block lose
        6: place unblocked 4
        5: cap/block 4 (if uncapped: cap 3, if capped space block 5th)
        4: capture 2
        3: set 3 (prevent capture)
        1: create 2 w/ gap
    */ 

    public void updateInput(String[][] finalBoard){
        try{
            File inputFile = new File("./input.txt");
            FileWriter writer = new FileWriter(inputFile);
            if(myToken.equals("w")){
                writer.write("BLACK");
            }else{
                writer.write("WHITE");
            }
            writer.write("\n100.0\n");
            writer.write(captures[0]+","+captures[1]+"\n");
            for(int i=0; i<19; i++){
                for(int j=0; j<19; j++){
                    writer.write(finalBoard[i][j]);
                    if(j==18){
                        writer.write("\n");
                    }
                }
            }
            writer.close();
        }catch(IOException e){

        }
    }
    
    public void playMove(){
        readInput();
        //printInput();
        gameRecords = new HashMap<>();
        //path = new String[minMaxDepth];
        //printInput();
        System.out.println();
        //testBoard = new String[19][19];
        //testBoard = copyBoard(board, testBoard);
        //testBoard[10][0]="b";
        //testBoard[10][8] = "w";
        //testKey = new key(testBoard);
        //System.out.println("testBoard key: "+testKey);
        int[] finalMove = prioChecks();
        if(finalMove[0] == -1){
            finalMove = AlphaBetaSearch();
        }
        String[][] finalBoard = new String[19][19];
        printFinalMove(finalMove, finalBoard);
        String output = moveToString(finalMove);
        //System.out.println();
        System.out.println(output);
        writeOutput(output);
        //updateInput(finalBoard);
        //System.out.println(gameRecords.get(testKey));
        //System.out.println(searchCheck);
        //System.out.println(CalcValue(finalBoard, captures, finalMove));

        
        //
        //System.out.println(CalcValue(board, captures, move));
    }

    public static void main(String args[]){
        homework hw = new homework();
        //hw.readInput();
        //hw.printInput();
        //hw.init();
        
        //hw.checkPlayText();
        hw.playMove();
        //hw.implementSearch();
    }
}
