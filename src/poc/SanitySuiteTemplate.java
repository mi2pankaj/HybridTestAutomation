package poc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SanitySuiteTemplate {

	public static void main(String[] args) {
		String filePath = System.getProperty("user.dir")+"/sanitySuiteFile";
		String newContent = "25";
		String oldContent = "[Total no. of cases]";
		SanitySuiteTemplate obj = new SanitySuiteTemplate();
		obj.modifyFile(filePath, newContent, oldContent);
	}
	
	public  void modifyFile(String filePath, String newString, String oldString){
		
		String oldContent ="";
		
		BufferedReader reader = null;
        
        FileWriter writer = null;
        
        try{
        	
        	reader = new BufferedReader(new FileReader(new File(filePath)));
        	String line = reader.readLine();
        	
        	while(line !=null){
        		oldContent = oldContent + line+ System.lineSeparator();
        		line = reader.readLine();
        	}
        	
        	String newContent = oldContent.replace(oldString, newString);
        	
        	//Rewriting the input text file with newContent
            
            writer = new FileWriter(new File(filePath));
             
            writer.write(newContent);
  
        }catch(Exception e){
        	e.printStackTrace();
        }
        finally{
        	try
            {
                //Closing the resources    
                reader.close();
                writer.close();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
		
		
		
	}
}
