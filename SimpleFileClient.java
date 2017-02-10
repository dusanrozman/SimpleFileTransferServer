import java.io.*;
import java.net.*;
import java.util.Scanner;

public class SimpleFileClient {
	
	public final static int SOCKET_PORT = 20000; //whatever port
	public final static String SERVER = "192.168.2.10";
	
	
	public final static int FILE_SIZE = 60223860; //?
	
        
    public static void main(String[] args) throws IOException 
    {
        int bytesRead;
        int current = 0;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        Socket sock = null;
        
        //used in put
		FileInputStream fis = null;
	    BufferedInputStream bis = null;
	    OutputStream os = null;
	        
        
        while(true)
        {
	        try
	        {
	        	sock = new Socket(SERVER, SOCKET_PORT);
	        	InputStreamReader IR = new InputStreamReader(sock.getInputStream());
	        	BufferedReader BR = new BufferedReader(IR);
	        	String line1 = BR.readLine();
	        	
	        	System.out.print(line1 + ": ");
	        	Scanner scan = new Scanner(System.in);
	     		String stringCommand = scan.nextLine();
	        	
	        	PrintStream PS = new PrintStream(sock.getOutputStream());
	        	PS.println(stringCommand);
	        	
	        	//if we're transfering a file.. NOTE it takes file and sends from c:/ directory
	        	if(stringCommand.startsWith("get"))
	        	{
	        		try{
		        		byte [] mybytearray  = new byte [FILE_SIZE];
						InputStream is = sock.getInputStream();
						
						//can specify directory here ie. FILE_TO_RECIEVE =
						//"c:\\Documents and Settings" + stringCommand.substring(4);
						//but it just goes go default directory if only name is provided
						String FILE_TO_RECEIVE = stringCommand.substring(4);
						fos = new FileOutputStream(FILE_TO_RECEIVE);
						bos = new BufferedOutputStream(fos);
						bytesRead = is.read(mybytearray,0,mybytearray.length);
						current = bytesRead;
						
						do {
						 bytesRead =
						    is.read(mybytearray, current, (mybytearray.length-current));
						 if(bytesRead >= 0) current += bytesRead;
						} while(bytesRead > -1);
						
						bos.write(mybytearray, 0 , current);
						bos.flush();
						System.out.println("File " + FILE_TO_RECEIVE
						  + " downloaded (" + current + " bytes read)");
	        		}
	        		catch(Exception e)
	        		{
	        			System.out.println("Invalid file. Try again.");
	        		}
	        	}
	        	else if(stringCommand.startsWith("put"))
	        	{
	        		try
					{
    					//need to add check if file to send exists.. 
    					String FILE_TO_SEND = stringCommand.substring(4);
    					File myFile = new File(FILE_TO_SEND);
    					
	    				byte [] byteArray = new byte[(int)myFile.length()];
	    				fis = new FileInputStream(myFile);
	    				bis = new BufferedInputStream(fis);
	    				bis.read(byteArray,0,byteArray.length);
	    				os = sock.getOutputStream();
	    				System.out.println("Sending " + FILE_TO_SEND + "(" + byteArray.length + " bytes)");
	    				os.write(byteArray,0,byteArray.length);
	    				os.flush();
					}
					catch(FileNotFoundException e)
					{
						System.out.println("That file does not exist in that directory. ");
					}
	        		
	        		
	        		
	        		
	        		
	        		
	        		
	        		
	        	}
	        	//if we're just writing something to console
	        	else
	        	{	
		        	while(true)
		        	{
			        	String line = BR.readLine();
			        	if(line == null || line.equals("exit")) break;
			        	System.out.println(line);
			        }
	        	}
	        }
	      finally
	      {
	      		if(fos != null) fos.close();
	      		if(bos != null) bos.close();
	      		if(sock != null) sock.close();
	      		if(os != null) os.close();
	      		if(fis != null) fis.close();
	      		if(bis != null) bis.close();
	      }
       }
    }
}
