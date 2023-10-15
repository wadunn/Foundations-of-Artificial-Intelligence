import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;



public class homework {
    static String searchType; // line1
    int[] dimensions; //dimensions of MT. ski area (W,H)  - line2
    int[] start; // line3
    int stamina; // line4
    int lodgeCount; // line5
    int[][] lodgeCoords; 
    int[][] elevation;

    private void printArray(int[] array){
        for(int i =0; i< array.length; i++){
            System.out.print(array[i] + " ");
        }
        System.out.println();
    }
    
    public void printInput(){
        System.out.println(searchType);
        printArray(dimensions);
        printArray(start);
        System.out.println(stamina);
        System.out.println(lodgeCount);
        for(int i = 0; i< lodgeCoords.length; i++){ 
            printArray(lodgeCoords[i]);
        }
        for(int i =0; i<elevation.length; i++){
            printArray(elevation[i]);
        }
    }

    public int[] lineParse(int size, String line){
        int[] nums =  new int[size];
        String[] split = line.split(" +");
        for(int i =0; i<size; i++){
            nums[i] = Integer.valueOf(split[i]); 
        }

        return nums;
    }

    public void readInput(){
        try{
            File inputFile = new File("./input.txt");
            Scanner sc = new Scanner(inputFile);
            //First Line
            searchType = sc.nextLine();
            dimensions = lineParse(2, sc.nextLine());
            start = lineParse(2, sc.nextLine());
            stamina = lineParse(1, sc.nextLine())[0];
            lodgeCount = lineParse(1, sc.nextLine())[0];
            lodgeCoords = new int[lodgeCount][];
            for(int i =0; i <lodgeCount; i++){
                lodgeCoords[i] = lineParse(2, sc.nextLine());
            }
            elevation = new int[dimensions[1]][];
            for(int i=0; i< dimensions[1]; i++){
                elevation[i] =lineParse(dimensions[0], sc.nextLine());
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

    public void writeOutput(String[] results){
        try{
        File output = new File("output.txt");
        output.createNewFile();
        FileWriter writer = new FileWriter("output.txt");
        for(int i =0; i<results.length; i++){
            writer.write(results[i]);
            writer.write("\n");
        }
        writer.close();
        }catch(IOException e){
            System.out.println("output.txt not created");
        }
    }

    public void BFSSearch(){
        BFS bfsSearch = new BFS();
        //System.out.println("Pre BFS: start: "+ start[0] + " " +start[1]);
        bfsSearch.runBFS(start, stamina, lodgeCount, lodgeCoords, elevation);
        writeOutput(bfsSearch.results());
    }

    public void UCSSearch(){
        UCS ucsSearch = new UCS();
        ucsSearch.runUCS(start, stamina, lodgeCount, lodgeCoords, elevation);
        writeOutput(ucsSearch.results());
    }

    public void ASearch(){
        A aSearch = new A();
        writeOutput(aSearch.runA(start, stamina, lodgeCount, lodgeCoords, elevation));
    }

    public void implementSearch(){
        //System.out.println("start: "+ start[0] + " " +start[1]);
        readInput();
        //printInput();
        if(searchType.equals("BFS")){
            BFSSearch();
        }else if(searchType.equals("UCS")){
            UCSSearch();
        }else if(searchType.equals("A*")){
            ASearch();
        }
        //writeOutput("FAIL");
    }


    public static void main(String args[]){
        homework hw = new homework();
        hw.implementSearch();
    }
}
