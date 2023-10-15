import java.io.IOException;
import java.io.*;

public class inputClear {
    public static void main(String args[]){
        try{
            File inputFile = new File("./input.txt");
            FileWriter writer = new FileWriter(inputFile);
            writer.write("WHITE");

            writer.write("\n100.0\n");
            writer.write("0,0\n");
            for(int i=0; i<19; i++){
                for(int j=0; j<19; j++){
                    writer.write(".");
                    if(j==18 && i!=18){
                        writer.write("\n");
                    }
                }
            }
            writer.close();
        }catch(IOException e){
        }
    }
}
