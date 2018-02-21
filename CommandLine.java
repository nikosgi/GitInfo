import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;




public class CommandLine{


        public static String executeCommand(String command,String pathRepo){

                StringBuffer output = new StringBuffer();

                Process p;
                BufferedReader reader = null;

                try{
                        p = Runtime.getRuntime().exec(command,null,new File(pathRepo) );
                       	p.waitFor();		                       
                        reader = new BufferedReader(new InputStreamReader(p.getInputStream()) );

                        String line = " ";
                        while ( ( line = reader.readLine()) != null ){
                                output.append(line + "\n");
                        }
			

                }catch ( Exception e){
                        System.err.println("error in reader");
                        e.printStackTrace();
                }finally{
                	try{reader.close();}catch(Exception e){System.err.println("error in close");}
                }
                return output.toString();
        }
        
        public static String executeCommandAlt(String command,String pathRepo){

        	StringBuffer output = new StringBuffer();
        	Process p;
        	BufferedReader reader = null;
        	BufferedReader readerErr = null;

            	try{
                        p = Runtime.getRuntime().exec(command,null,new File(pathRepo) );
                       	p.waitFor();		                       
                        reader = new BufferedReader(new InputStreamReader(p.getInputStream()) );

                        String line = " ";
                        while ( ( line = reader.readLine()) != null ){
                                output.append(line + "\n");
                        }
	
        		readerErr = new BufferedReader ( new InputStreamReader (p.getErrorStream() ) )  ;
        		line = " " ;
        		while ( ( line = readerErr.readLine() ) != null ) { 
        			output.append(line + "\n" );
        		}

        		
        			

                 }catch ( Exception e){
                        System.err.println("error in reader");
                        e.printStackTrace();
                 }finally{
        		try{
        			reader.close();
        			readerErr.close();
        		}catch(Exception e){System.err.println("error in close");}
        	}



                return output.toString();
        }
        
	


}
