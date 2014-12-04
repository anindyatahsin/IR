package cs.virginia.edu.ir.news.article.mapper.io;

//import java.io.BufferedWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class Output {
	Writer writer = null;
	
	public void writeToFile(String fileName, String text){
		try {
			writer = new BufferedWriter(new FileWriter(new File(fileName), true));
			writer.append(text);
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Exception occured while trying to create file");
		}
	}
	
	public void delete(String fileName){
		try {
			writer = new BufferedWriter(new FileWriter(new File(fileName)));
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //PrintWriter pw = new PrintWriter(s);
        
	}
}
