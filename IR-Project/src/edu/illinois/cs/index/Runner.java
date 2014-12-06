package edu.illinois.cs.index;

import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import cs.virginia.edu.ir.news.article.mapper.config.DeploymentConfiguration;
import cs.virginia.edu.ir.news.article.mapper.config.RunTimeConfiguration;
import cs.virginia.edu.ir.news.article.mapper.io.FileInput;
import edu.illinois.cs.index.similarities.*;

public class Runner {
	//please keep those constants 
    final static String _dataset = "npl";
    final static String _indexPath = "lucene-npl-index";
    final static String _prefix = "data/";
    final static String _file = "npl.txt";

////This enables you to interact with the program in command line data/yahoo-news/Yahoo/
//    public static void main(String[] args) throws IOException {
//        if (args.length == 1 && args[0].equalsIgnoreCase("--index"))
//            Indexer.index(_indexPath, _prefix, _file);
//        else if (args.length >= 1 && args[0].equalsIgnoreCase("--search"))
//        {
//            String method = null;
//            if (args.length == 2)
//                method = args[1];
//            interactiveSearch(method);
//        }
//        else
//        {
//            System.out.println("Usage: --index to index or --search to search an index");
//            System.out.println("If using \"--search\",");
//            printUsage();
//        }
//    }
    
////This makes it easier for you to run the program in an IDE
    public static void main(String[] args) throws IOException {
    	//To crate the index
    	//NOTE: you need to create the index once, and you cannot call this function twice without removing the existing index files
    	//Indexer.index(_indexPath, _prefix, _file);
        
        //Interactive searching function with your selected ranker
    	//NOTE: you have to create the index before searching!
    	String method = "--ok";//specify the ranker you want to test
    	String path="data/yahoo-news/IndexYahoo/";
        //interactiveSearch(path, method);
    }

    public static ArrayList<ResultDoc> interactiveSearchpost(String index_path, String query) throws IOException {
        String method= "--ok";
    	Searcher searcher = new Searcher(index_path);
        setSimilarity(searcher, method);
        //BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        //System.out.println("Type text to search, blank to quit.");
        //System.out.print("> ");
        String input=query;
        //while ((input = br.readLine()) != null && !input.equals("")) {
        SearchResult result = searcher.search(input);
        
        ArrayList<ResultDoc> results = result.getDocs();
        int rank = 1;
        if (results.size() == 0){
        	RunTimeConfiguration.writer.write("\n"+"No results found!");
        	return null;
        }
        
        return results;
        /*
        for (ResultDoc rdoc : results) {
        	
            System.out.println("\n------------------------------------------------------");
            System.out.println(rank + ". " + rdoc.title());
            System.out.println("------------------------------------------------------");
            // System.out.println(result.getSnippet(rdoc).replaceAll("\n", " "));
            System.out.println(rdoc.content());
            ++rank;
        }
        
        */
        //    System.out.print("> ");
        //}

    }    
    
    /**
     * Feel free to modify this function, if you want different display!
     *
     * @throws IOException
     */
    public static double interactiveSearch(String index_path, String query) throws IOException {
        String method= "--ok";
    	Searcher searcher = new Searcher(index_path);
        setSimilarity(searcher, method);
        //BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        //System.out.println("Type text to search, blank to quit.");
        //System.out.print("> ");
        String input=query;
        //while ((input = br.readLine()) != null && !input.equals("")) {
        SearchResult result = searcher.search(input);
        ArrayList<ResultDoc> results = result.getDocs();
        int rank = 1;
        if (results.size() == 0)
        	try{
        		RunTimeConfiguration.writer.write("\n"+"No results found!");
        	} catch (Exception e){
        		return 0.0;
        	}	
        return calculateMAP(results);
    }
    
    public static double calculateMAP(ArrayList<ResultDoc> results){
    	File folder = new File(DeploymentConfiguration.FEEDBACK_DIRECTORY);
    	File[] listOfFiles = folder.listFiles();
    	FileInput in = new FileInput();
    	String prefix = "";
    	ArrayList<Integer> relevance = new ArrayList<Integer>();
    	int num_rel = 0;
    	if(RunTimeConfiguration.CURRENTARTICLESOURCE.equals("alzajeera")){
    		prefix += RunTimeConfiguration.CURRENTARTICLESOURCE + "-" 
    				+ RunTimeConfiguration.CURRENTARTICLECATEGORY + "-article-"
    				+ RunTimeConfiguration.CURRENTARTICLEID + "-";
    	} else{
    		prefix += RunTimeConfiguration.CURRENTARTICLESOURCE + "-article-"
    				+ RunTimeConfiguration.CURRENTARTICLEID + "-";
    	}
    	for (int i = 0; i < listOfFiles.length; i++) {
    		if(listOfFiles[i].getName().startsWith(prefix)){
    			ArrayList<String> lines = in.readFromFile(listOfFiles[i].getAbsolutePath());
    			for(String line : lines){
    				String val[] = line.split("\\s+");
    				if(val.length == 7){
    					if(val[6].equals("Relevant")){
    						relevance.add(1);
    						num_rel++;
    					} else{
    						relevance.add(0);
    					}
    				}
    			}
    		}
    	}
    	int i = 1;
    	double avgp = 0.0;
		double numRel = 1;
    	for (ResultDoc rdoc : results) {
    		if (relevance.get(Integer.parseInt(rdoc.title()) - 1) == 1) {
				avgp += numRel / i;
				++numRel;
				//System.out.print("\n"+"  ");
			} else {
				//System.out.print("\n"+"X ");
			}
			//try{
				//RunTimeConfiguration.writer.write("\n"+"\n------------------------------------------------------");
				//RunTimeConfiguration.writer.write("\n"+i + ". " + rdoc.title());
				//RunTimeConfiguration.writer.write("\n"+"------------------------------------------------------");
				// System.out.println(result.getSnippet(rdoc).replaceAll("\n", " "));
			//System.out.println(i + ". " + rdoc.title());
				//RunTimeConfiguration.writer.write("\n"+rdoc.content());
			//} catch(IOException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
			//}
			 ++i;
		}
    	/*try{
    	RunTimeConfiguration.writer.write("\n"+"\n*************************************************************\n");
    	RunTimeConfiguration.writer.write("\n"+"Average Precision: " + (avgp / num_rel));
    	RunTimeConfiguration.writer.write("\n"+"\n***************************************************************\n");
    	RunTimeConfiguration.writer.write("\n"+"P@K: " + ((numRel-1) / (i-1)));
    	RunTimeConfiguration.writer.write("\n"+"\n***************************************************************\n");
    	}catch(IOException e){
    		e.printStackTrace();
    	}*/
    	return avgp / num_rel;  // returning AP
    	//int exp_i = Math.min(num_rel, i-1);
    	//return (numRel-1) / exp_i; //returning p@5
    }

    public static void setSimilarity(Searcher searcher, String method) {
        if(method == null)
            return;
        else if(method.equals("--dp"))
            searcher.setSimilarity(new DirichletPrior());
        else if(method.equals("--jm"))
            searcher.setSimilarity(new JelinekMercer());
        else if(method.equals("--ok"))
            searcher.setSimilarity(new OkapiBM25());
        else if(method.equals("--pl"))
            searcher.setSimilarity(new PivotedLength());
        else if(method.equals("--tfidf"))
            searcher.setSimilarity(new TFIDFDotProduct());
        else if(method.equals("--bdp"))
            searcher.setSimilarity(new BooleanDotProduct());
        else
        {
            System.out.println("[Error]Unknown retrieval function specified!");
            printUsage();
            System.exit(1);
        }
    }

    private static void printUsage()
    {
        System.out.println("To specify a ranking function, make your last argument one of the following:");
        System.out.println("\t--dp\tDirichlet Prior");
        System.out.println("\t--jm\tJelinek-Mercer");
        System.out.println("\t--ok\tOkapi BM25");
        System.out.println("\t--pl\tPivoted Length Normalization");
        System.out.println("\t--tfidf\tTFIDF Dot Product");
        System.out.println("\t--bdp\tBoolean Dot Product");
    }
}
