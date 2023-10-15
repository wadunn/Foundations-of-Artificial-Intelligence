import java.io.*;
import java.util.Scanner;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;



public class homework {
    
    int varstandard = 1;
    int intmax = 100;
    String query;
    int k;
    HashMap<String, List<kb>> library;
    List<String> sentences;
    HashMap<String, Boolean> sentenceLib;
    String answer;
    HashMap<String, Integer> resCount;
    HashMap<String, Boolean> unresolvable;
    HashMap<String, Boolean> unresolvableTerms;


    public class kb{
        String[] predicates;
        boolean[] negation;
        int[] predicateVars;
        //String[] variables;
        String[] operators;
        HashMap<String, String[]> predVars;

        public kb(int a){
            predicates = new String[a];
            negation = new boolean[a];
            predicateVars = new int[a];
            //variables = new String[b];
            operators = new String[a -1];
            predVars = new HashMap<>();
        }

        public kb(String temp){
            String split[] = temp.split(" "); 
            int a = ((split.length+1)/2);
            predicates = new String[a];
            negation = new boolean[a];
            predicateVars = new int[a];
            //variables = new String[b];
            operators = new String[a -1];
            predVars = new HashMap<>();
            //System.out.println("TELL:"+temp);
            for(int j=0; j<split.length; j+=2){
                //System.out.println(j+" "+split[j]);
                String pred = split[j].substring(0,split[j].indexOf("("));
                String vars = split[j].substring(split[j].indexOf("(")+1, split[j].indexOf(")"));
                String varSplit[] = vars.split(",");
                if(pred.indexOf("~")==0){
                    pred = pred.substring(1);
                    addPred(pred, varSplit.length, true);
                }else{
                    addPred(pred, varSplit.length, false );
                }
                addVars(pred+""+j, varSplit);
                if(j != 0){
                    addOperators(split[j-1]);
                }
            }
        }

        public void addPred(String predicate, int predVars, boolean predNegation){
            for(int i=0; i<predicates.length; i++){
                if(predicates[i] == null){
                    predicates[i] = predicate; 
                    predicateVars[i] = predVars;
                    negation[i] = predNegation;
                    break;
                }
            }
        }

        public void addVars(String pred, String[] var){
            if(predVars.get(pred)!=null){
                String[] oldVars = predVars.get(pred);
                for(int i=0; i<var.length; i++){
                    if(!Character.isUpperCase(oldVars[i].charAt(0))){
                        oldVars[i] = var[i];
                    }
                }
            }else{
              predVars.put(pred, var);  
            }
            
        }

        public void addOperators(String operator){
            for(int i=0; i<predicates.length -1; i++){
                if(operators[i] == null){
                    operators[i] = operator; 
                    break;
                }
            }
        }

        public String[] getPreds(){
            return predicates;
        }

        public boolean[] getNegation(){
            return negation;
        }

        public String[] getOperators(){
            return operators;
        }

        public String[] getPredVars(String pred){
            return predVars.get(pred);
        }

        public String[] getPredKeys(){
            String[] keys = new String[predicates.length];
            Set key = predVars.keySet();
            Iterator i = key.iterator();
            int j =0;
            while(i.hasNext()){
                keys[j] = (String)i.next();
                j++;
            }
            return keys;
        }


    }

    //**** Input/Output Functions *** */

    public void readInput(){
        try{
            File inputFile = new File("./input.txt");
            Scanner sc = new Scanner(inputFile);
            //First Line
            query = sc.nextLine();
            k = Integer.valueOf(sc.nextLine());
            String nextLine;
            for(int i = 0; i<k; i++ ){
                nextLine = sc.nextLine();
                splitSentences(nextLine);
            }
            //Should be no lines remaining
            if(sc.hasNextLine()){
                System.out.println("Should be no more lines: " +sc.nextLine());
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

    public void cnfConvert(String input){
        String[] split = input.split("\\|");
        int[] argCount = new int[split.length];
        int args =1;
        String[][] sent = new String[split.length][];
        for(int i=0; i<split.length; i++){
            argCount[i] = split[i].split("&").length;
            args = args * argCount[i];
            sent[i] = new String[split[i].split("&").length];
            String[] andSplit = split[i].split("&");
            for(int j=0; j<andSplit.length; j++){
                sent[i][j] = andSplit[j];
            }
             
        }
        String[] cnf = new String[args];
        for(int i=0; i<args; i++){
            cnf[i] = "";
            for(int j=0; j<split.length; j++ ){
                int k = i%sent[j].length;
                cnf[i] += sent[j][k].replaceAll(" ","");
                if(j<split.length-1){
                    cnf[i] += " | ";
                }
            }
            sentences.add(cnf[i]);
        }

    }

    public void distribute(String input, String imply){
        String[] junctions = input.split("&");
        for(int i=0; i<junctions.length; i++){
            if(Character.valueOf(junctions[i].charAt(0)).equals(' ')){
                junctions[i] = junctions[i].substring(1);
            }
            junctions[i] += "|" + imply;
            //System.out.println(junctions[i]);
            sentences.add(junctions[i]);
        }
        
    }


    public void splitSentences(String sentence){
        if(sentence.indexOf("=>") >=0){
            String[] split = sentence.split(" ");
            for(int i=0; i<split.length; i++){
                if(split[i].indexOf("~")>=0){
                    split[i] = split[i].substring(1);
                }else if(Character.isUpperCase(split[i].charAt(0))){
                    split[i] = "~"+split[i];
                }else if(split[i].equals("&")){
                    split[i] = "|";
                }else if(split[i].equals("|")){
                    split[i] = "&";
                }else if(split[i].equals("=>")){
                    //split[i] = "|";
                    break;
                }
            }

            String ponen = "";
            for(int i=0; i<split.length; i++){
                if(i==0){
                    ponen = split[i];
                }else{
                    ponen = ponen+" "+split[i];
                }
            }
            //System.out.println(ponen);
            String[] dist = ponen.split("=>");
            distribute(dist[0],dist[1] );
        }else{
            cnfConvert(sentence);
        }
    }

    //Breaks sentences down into smaller sentences and records in KB

    public void tellKB(String temp){
        if(sentenceLib.containsKey(temp)){
            return;
        }else{
            sentenceLib.put(temp, true);
        }
        kb data = new kb(temp);
        String[] predList = data.getPreds();
        String n = "";
        //System.out.println(temp);
        for(int j=0; j<predList.length; j++){
            if(data.getNegation()[j]){
                n = "~";
            }else{
                n ="";
            }
            if(library.get(n+predList[j])!= null){
                List<kb> db = library.get(n+predList[j]);
                db.add(data);
                //System.out.println(n+predList[j]);
            }else{
                List<kb> db = new LinkedList<>();
                db.add(data);
                if(data.getNegation()[j]){
                    library.put("~"+predList[j],db);
                    //System.out.println("~"+predList[j]);
                }else{
                    library.put(predList[j],db);
                    //System.out.println(predList[j]);
                }
                
                
            }
        }
    }

    public void tellKB(kb data){
        if(sentenceLib.containsKey(reconstruct(data))){
            return;
        }else{
            sentenceLib.put(reconstruct(data), true);
        }
        String[] predList = data.getPreds();
        String n = "";
        for(int j=0; j<predList.length; j++){
            if(data.getNegation()[j]){
                n = "~";
            }else{
                n ="";
            }
            if(library.get(n+predList[j])!= null){
                List<kb> db = library.get(n+predList[j]);
                db.add(data);
            }else{
                List<kb> db = new LinkedList<>();
                db.add(data);
                if(data.getNegation()[j]){
                    library.put("~"+predList[j],db);
                }else{
                    library.put(predList[j],db);
                }
                
            }
        }
    }

    public void tell(){
        for(int i=0; i<sentences.size(); i++){
            String temp = sentences.get(i);
            tellKB(temp); 
        //store in hashmap for each predicate (append to list)

        }
    }

    public void printKB(){
        System.out.println("KB: ");
        for(int i=0; i<sentences.size(); i++){
            System.out.println(sentences.get(i));
        }
        System.out.println();
    }

    public String reconstruct(kb data){
        String reconstruction = "";
        if(data == null){
            return reconstruction;
        }
        String[] preds = data.getPreds();
        String[] opers = data.getOperators();
        boolean[] negate = data.getNegation();
        String[] vars;
        String[] keys = data.getPredKeys();
        for(int i =0; i<preds.length; i++){
            //System.out.println(i+" "+preds[i]+ " "+data.getPredVars(preds[i]+2*i).length);
            //System.out.println("vars; "+ keys[i]);
            if(data.getPredVars(preds[i]+2*i)!=null){
                if(i > 0){
                    reconstruction += " "+ opers[i-1]+ " "; 
                }
                vars = data.getPredVars(preds[i]+""+2*i);
                if(negate[i]){
                    reconstruction += "~";
                }
                reconstruction += preds[i] +"(";
                for(int j=0; j<vars.length; j++){
                    reconstruction+=vars[j];
                    if(j < vars.length -1){
                        reconstruction+= ",";
                    }
                }
                reconstruction += ")";
            }
        }
        //System.out.println("recon: "+reconstruction);
        return reconstruction;
    }

    public String queryToPred(String q){
        String val;
        val = q.substring(0,q.indexOf("("));
        return val;
    }

    public boolean contradictCheck(String[] varsOne, String[] varsTwo){
        HashMap<String,String> log = new HashMap<>();
        for(int i=0; i<varsOne.length; i++){
            if(log.containsKey(varsOne[i]) || log.containsKey(varsTwo[i])){
                if(log.get(varsOne[i]) !=varsTwo[i] || log.get(varsTwo[i]) != varsOne[i]){
                    return true;
                }
            }else{
                log.put(varsOne[i],varsTwo[i]);
                log.put(varsTwo[i],varsOne[i]);
                //System.out.println(varsOne[i]+ " "+varsTwo[i]);
            }
        }
        return false;
    }

    public String searchPred(String input, kb data){
        //System.out.println("input: "+input);
        String resolve = reconstruct(data);
        String[] preds = data.getPreds();
        boolean[] negate = data.getNegation();
        String[] newPreds = input.split("\\|");
        String[][] newPredVars = new String[newPreds.length][];
        boolean[] newNegate = new boolean[newPreds.length];
        String[] keys = data.getPredKeys();
        for(int i=0; i< newPreds.length; i++){
            newPredVars[i] = newPreds[i].substring(newPreds[i].indexOf("(")+1, newPreds[i].indexOf(")")).split(",");
            newPreds[i] = newPreds[i].substring(0, newPreds[i].indexOf("("));
            newPreds[i] = newPreds[i].replace(" ","");
            if(newPreds[i].contains("~")){
                newNegate[i]=true;
                newPreds[i] = newPreds[i].substring(newPreds[i].indexOf("~")+1);
            }else{
                newNegate[i] = false;
            }
        }

        kb newData = new kb(preds.length + newPreds.length);

        String[] allPreds = new String[preds.length+newPreds.length];
        String[][] pvReplace = new String[preds.length+newPreds.length][];
        boolean[] allNeg = new boolean[preds.length+newPreds.length];
        List<String> changeVars = new LinkedList<>();
        List<String> keepVars = new LinkedList<>();
        for(int i=0; i<allPreds.length; i++){
            if(i<preds.length){
                allPreds[i]=preds[i];
                pvReplace[i] = data.getPredVars(preds[i]+2*i);
                allNeg[i] = negate[i];
                //System.out.println(allPreds[i]+"("+pvReplace[i][0]+")");
            }else{
                allPreds[i] = newPreds[i-preds.length];
                pvReplace[i] = newPredVars[i-preds.length];
                allNeg[i] = newNegate[i-preds.length];
                //System.out.println(allPreds[i]+"("+pvReplace[i][0]+")");
            }
        }

        

        for(int i=0; i<preds.length; i++){
            for(int j=preds.length; j<allPreds.length; j++){
                if(allPreds[i].equals(allPreds[j])){
                    for(int k=0; k<pvReplace[i].length; k++){
                        if(!keepVars.contains(pvReplace[i][k])){
                            keepVars.add(pvReplace[i][k]);
                        }
                    }
                    if(contradictCheck(pvReplace[i],pvReplace[j])){
                        return "N/A";
                    }
                }
                

            }
        }
        //Random r = new Random();
        //char c = (char)(r.nextInt(26) + 'a');
        varstandard++;
        for(int i=0; i<allPreds.length; i++){
            for(int j=0; j<pvReplace[i].length; j++){
                if(!keepVars.contains(pvReplace[i][j]) && !changeVars.contains(pvReplace[i][j]) && i<preds.length){
                    changeVars.add(pvReplace[i][j]);
                }else if(i>= preds.length){
                    if(changeVars.contains(pvReplace[i][j]) && Character.isLowerCase(pvReplace[i][j].charAt(0))){
                        pvReplace[i][j] += ""+String.valueOf(varstandard);
                    }
                }
            }
        }


        /*
        for(int i=0; i<preds.length; i++){
            for(int j=preds.length; j<newPreds.length+preds.length; j++){
                if(allPreds[i].equals(allPreds[j])){
                    continue;
                }else{
                    for(int k=0; k<pvReplace[i].length; k++){
                        for(int l=0; l<pvReplace[j].length; l++){
                            if(pvReplace[i][k].equals(pvReplace[j][l]) && Character.isLowerCase(pvReplace[i][k].charAt(0)) && Character.isLowerCase(pvReplace[j][l].charAt(0))){
                                for(int m=0; m<pvReplace[j].length; m++){
                                    if(pvReplace[j][m].equals(pvReplace[j][l])){
                                        pvReplace[j][m] = pvReplace[j][m] + "a";
                                        //System.out.println("var replace: "+ pvReplace[j][m]);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //*/

        for(int n=0; n<2; n++){
            for(int i=0; i<allPreds.length; i++){
                for(int j=0; j<allPreds.length; j++){
                    if(allPreds[i].equals(allPreds[j]) && i!=j){
                        if((n==0 && allNeg[i]!=allNeg[j]) || (n==1 && allNeg[i] == allNeg[j])){
                            for(int k=0; k<pvReplace[i].length; k++){
                                if(Character.isUpperCase(pvReplace[i][k].charAt(0)) && !Character.isUpperCase(pvReplace[j][k].charAt(0))){
                                    for(int l=0; l<allPreds.length; l++){
                                        for(int m=0; m<pvReplace[l].length; m++){
                                            //System.out.println(allPreds[l]+": "+pvReplace[i][k]+ " "+pvReplace[l][m]);
                                            if(pvReplace[j][k].equals(pvReplace[l][m]) && l!=j){
                                                pvReplace[l][m] = pvReplace[i][k];

                                                //System.out.println(pvReplace[l][m]);
                                            }
                                        }
                                    }
                                    pvReplace[j][k] = pvReplace[i][k];
                                }
                            }
                        }
                        
                    }
                }
            }
        }
        boolean match = false;
        for(int i=0; i<allPreds.length; i++){
            for(int j=0; j<allPreds.length; j++){
                match = false;
                if(allPreds[i].equals(allPreds[j]) && j!=i){
                    for(int k=0; k<pvReplace[i].length; k++){
                        if(pvReplace[i][k].equals(pvReplace[j][k])){
                            match = true;
                        }else if(Character.isLowerCase(pvReplace[i][k].charAt(0)) && Character.isLowerCase(pvReplace[j][k].charAt(0))){
                            if(match == true){
                                match = true;
                            }
                            if(match == false){
                                match = false;
                            }
                        }else{
                            match = false;
                            break;
                        } 
                    }
                    if(match){
                        for(int l=0; l<allPreds.length; l++){
                            for(int q=0; q<pvReplace[j].length; q++){
                                for(int m=0; m<pvReplace[l].length; m++){
                                    if(pvReplace[j][q].equals(pvReplace[l][m]) && j!=l && j!= i){
                                        pvReplace[l][m] = pvReplace[i][q];
                                    }
                                }
                            }
                        }
                        pvReplace[j] = pvReplace[i];
                        //System.out.println(allPreds[i]);
                    }
                    
                }
            }
        }

        for(int i=0; i<allPreds.length; i++){
            newData.addPred(allPreds[i],pvReplace[i].length,allNeg[i]);
            newData.addOperators("|");
            newData.addVars(allPreds[i]+2*i,pvReplace[i]);
        }

        kb trimData = cancelPreds(newData, new kb(input));
        String output;
        if(reconstruct(trimData).equals(input)){
            output = "N/A";
        }else{
            output = reconstruct(trimData);
            if(!output.equals("")){
                tellKB(output);
            }
        }
        return output;
    }

    public kb cancelPreds(kb data, kb orig){
        String[] terms = data.getPreds();
        boolean[] negate = data.getNegation();
        String[] varsOne;
        String[] varsTwo;
        boolean[] cancel = new boolean[terms.length];
        int cancelCount =0;
        for(int i=0; i<terms.length; i++){
            cancel[i] = false;
            varsOne = data.getPredVars(terms[i]+(2*i));
            for(int j=0; j<terms.length; j++){
                varsTwo = data.getPredVars(terms[j]+2*j);
                if(terms[i].equals(terms[j])&& negate[i] != negate[j] && !cancel[i]){
                    for(int k=0; k<varsOne.length; k++){
                        if(varsOne[k].equals(varsTwo[k])){
                            cancel[i] = true;
                        }else{
                            cancel[i] = false;
                            break;
                            
                        }
                    }
                    if(cancel[i]){
                        cancelCount++;
                    }
                }else if(terms[i].equals(terms[j]) && negate[i] == negate[j] && !cancel[i] && !cancel[j] && i!=j){
                    for(int k=0; k<varsOne.length; k++){
                        if(Character.isUpperCase(varsOne[k].charAt(0)) && Character.isUpperCase(varsTwo[k].charAt(0))){
                            if(varsOne[k].equals(varsTwo[k])){
                                cancel[j] = true;
                            }else{
                                cancel[j] = false;
                                break;
                            }
                        }else{
                            cancel[j] = true;
                        }

                    }
                }
            }
        }
        int kbSize = 0;
        int cancelled = 0;
        for(int i=0; i<cancel.length; i++){
            if(!cancel[i]){
                kbSize++;
            }else{
                cancelled++;
            }
        }
        
        //System.out.println("cancel preds " +reconstruct(data));
        kb newData = null;
        if(cancelCount == 0){
            return orig;
        }
        if(kbSize>0){
            newData = new kb(kbSize);
        
            int count =0;
            //System.out.println(cancel.length);
            for(int i=0; i<cancel.length; i++){
                if(!cancel[i]){
                    //System.out.println(data.getPredVars(data.getPreds()[i]+2*i)[0]);
                    newData.addPred(data.getPreds()[i], data.getPredVars(data.getPreds()[i]+""+2*i).length,negate[i]);
                    newData.addOperators("|");
                    newData.addVars(data.getPreds()[i]+count,data.getPredVars(data.getPreds()[i]+""+2*i));
                    count+= 2;
                }
            }
        }
        return newData;
    }

    public String comparatorRecursive(String input, int iter/* , HashMap<String,Boolean> unresVar*/){
        //HashMap<String,Boolean> unres = new HashMap<>(unresVar);
        //unres = unresVar;
        String resolution ="";

        if(unresolvable.containsKey(input)){
            //System.out.println("Previously Unresolved: "+input);
            return "N/A";
        }
        if(iter > intmax){
            return "N/A";
        }
        kb data = new kb(input);
        List<kb> kbList;
        for(int i=0; i<data.getPreds().length; i++){
            if(!data.getNegation()[i]){
                kbList = library.get("~"+data.getPreds()[i]);
            }else{
                kbList = library.get(data.getPreds()[i]);
                //System.out.println(kbList.get(0).getPreds()[0]);
            }
            if(kbList == null){
                continue;
            }
            /*
            String term = data.getPreds()[i];
            term+= "(";
            for(int k=0; k<data.getPredVars(data.getPreds()[i]+2*i).length; k++){
                if(k!=0){
                    term+=",";
                }
                term+= data.getPredVars(data.getPreds()[i]+2*i)[k];
            }
            term+=")";
            if(unres.containsKey(term)){
                if(unres.get(term).equals(data.getNegation()[i])){
                    continue;
                }
            }
            int resolves = 0;
            */
            for(int j =0; j<kbList.size(); j++){
                System.out.println();
                System.out.println(iter);
                System.out.println("pre res: "+input);
                System.out.println("Add: "+ reconstruct(kbList.get(j)));
                resolution = searchPred(input, kbList.get(j));
                System.out.println("Post Res: "+ resolution);
                if(resolution.equals("")){
                    return resolution;
                }else if(resolution.equals("N/A")){
                    continue;
                }else{
                    //resolves++;
                    String tempRes = comparatorRecursive(resolution, iter+1/*, unres*/);
                    if(tempRes.equals("")){
                        return tempRes;
                    }else if(tempRes.equals("N/A")){
                        if(iter <intmax){
                            unresolvable.put(resolution, true);
                        }
                    }else if(tempRes.equals("P/A")){
                        //resolves++;
                    }else{
                        resolution = tempRes;
                        //resolves++;
                    }
                } 
            }
            /*
            if(resolves == 0){
                String unresTerm ="";
                //if(data.getNegation()[i]){
                  //  unresTerm = "~";
                //}
                unresTerm += data.getPreds()[i] +"(";
                for(int k=0; k<data.getPredVars(data.getPreds()[i]+2*i).length; k++){
                    unresTerm+= data.getPredVars(data.getPreds()[i]+2*i)[k];
                    if(k<data.getPredVars(data.getPreds()[i]+2*i).length-1){
                        unresTerm+=",";
                    }
                }
                unresTerm+=")";
                if(unresTerm.equals("Has(Pasta,Bestia)")){
                    System.out.println("Here");
                }
                unres.put(unresTerm, data.getNegation()[i]);
                //System.out.println("unresTerm: "+unresTerm);
            }
            // */
        }

        return "N/A";


    }

    public boolean comparator(String input, kb data ){
        String querySearch = input;
        String resolution = "";
        resolution = reconstruct(data);
        boolean neg;
        if(input.contains("~")){
            neg = true;
            //input = input.substring(1);
            querySearch = input.substring(1);
        }else{
            neg = false;
            querySearch = "~"+input;
        }
        //String[] vars = input.substring(input.indexOf("(")+1,input.indexOf(")")).split(",");
        input = input.substring(0,input.indexOf("("));
        List<kb> kbList = library.get(input);
        if(kbList == null){return false;}
        for(int i =0; i<kbList.size(); i++){
            resolution = searchPred(querySearch, kbList.get(i));
            //System.out.println("resolution: "+resolution);
            if(resolution.equals("")){
                return true;
            }else if(resolution.equals("N/A")){
                continue;
            }else{
                //System.out.println(i+" recursive "+resolution );
                //HashMap<String, Boolean> unres = new HashMap<>(); 
                resolution = comparatorRecursive(resolution,1 /*,unres*/);
                if(resolution.equals("")){
                    return true;
                }
            }
        }
        return false;
    }

    //Utilizes KB to find answer for query
    public boolean compute(){
        boolean result = false;
        kb data;
        //List<kb> kbList = library.get(queryToPred(query));
        //for(int i =0; i< 1; i++){
            data = null;
            result = comparator(query, data);
        //}

        return result;
    }


    public void findAnswer(){
        library = new HashMap<>();
        sentenceLib = new HashMap<>();
        answer = "FALSE";
        sentences = new LinkedList<String>();
        resCount = new HashMap<>();
        unresolvable = new HashMap<>();
        unresolvableTerms = new HashMap<>();
        readInput();
        //printKB();        
        tell();
        if(compute()){
            answer = "TRUE";
        }
        writeOutput(answer);
        System.out.println(answer);
        
    }

    public static void main(String args[]){
        homework hw = new homework();
        hw.findAnswer();

    }
}
