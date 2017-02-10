import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleFileServer {
	
	public final static int SOCKET_PORT = 20000;
	public static ServerSocket servsock = null;
    public static Socket sock = null;
    public static InputStreamReader isr = null;
    public static BufferedReader BR = null;
    public static PrintStream PS = null;
    	
    //used in 'get'
    public static FileInputStream fis = null;
    public static BufferedInputStream bis = null;
    public static OutputStream os = null;
    	    	
    //used in put        
    public static InputStream input = null; 
	public static BufferedReader inReader = null;  
	public static BufferedWriter outReader = null;
	public static FileOutputStream wr = null;
	
    public static void main(String[] args) throws Exception 
    {
    	startServer();	
        
    }
    public static void startServer() throws Exception
    {
    	try
    	{
    		servsock = new ServerSocket(SOCKET_PORT);
    		while(true)
    		{
    			System.out.println("Waiting...");
	    			try
	    			{
	    				sock = servsock.accept();
	    				System.out.println("Accepting connection: " + sock);	    			
	    				
	    				//this block sends over the current directory so it's displayed
	    				//right away like in CMD... might not be necessary
	    				{
	  
					        PS = new PrintStream(sock.getOutputStream());
	    					PS.println( System.getProperty("user.dir"));
	    				}
	    				
	    				//reads client message/input
	    				isr = new InputStreamReader(sock.getInputStream());
	    				BR = new BufferedReader(isr);
	    				String message = BR.readLine();
	    				System.out.println("THE MESSAGE RECIEVED IS: " + message);
	    				
	    				//displays content of current folder
	    				if(message.equals("ls"))
	    				{
							lsCommand();	
	    				}
	    				//make directory commmand.. takes directry name as parameter
	    				else if(message.startsWith("mkdir"))
	    				{
	    					mkdirCommand(message);  
	    				}
	    				//chnages directories
	    				else if(message.startsWith("cd"))
	    				{
	    					cdCommand(message);	    					
	    				}	  					
	    				
	    				
	    				else if(message.startsWith("get"))
	    				{
	    					getCommand(message);
	    				}
	    				else if(message.startsWith("put"))
	    				{
		    				putCommand(message);
	    				}
						
						else if(message.startsWith("pwd"))
						{
							pwdCommand();
						}	
	    				
	    				else
	    				{
	    					System.out.println("ERROR: invalid input.");
	    					PS.println("Invalid input. Try again.");
	    				}
	    				
	    				System.out.println("Finished!");
	    			}
		    		finally
		    		{	
						if(sock != null) sock.close();
						if(os != null) os.close();
						if(fis != null) fis.close();
						if(bis != null) bis.close();
						if(input != null) input.close();
						if(inReader != null) inReader.close();
						if(outReader != null) outReader.close();
						if(wr != null) wr.close();
		    		}
    		
    		}
    
    	}
	    catch(java.net.SocketException e)
	    {
	    	System.out.println("Client has closed connection."); //THE SERVER STILL CRASHES!
	    }
	    finally
	    {
	    	if(servsock != null) servsock.close();
	    }    	
    }
    public static void lsCommand()
    {
		String outputString = "";
		File dir = new File(System.getProperty("user.dir"));
		File[] filesList = dir.listFiles();
		for (File file : filesList)	 
			{
				if (file.isFile())
				{
					outputString += "File: "+file.getName() + "\n";
				}
				else if (file.isDirectory())
				{
					outputString += "Directory: " +file.getName() + "\n";
				}	
			}
		PS.println(outputString);
    }
    public static void mkdirCommand(String message)
    {
    	try
		{
			File dir = new File(System.getProperty("user.dir") + "\\" + message.substring(6));
			dir.mkdir();		    					
		}
		catch(IndexOutOfBoundsException i)
		{
			System.out.println("Invalid command." + i);
		} 
    }
    
    public static void cdCommand(String message)
    {
    	if(message.equals("cd ..") || message.equals("cd.."))
		{	
			String pwdString = System.getProperty("user.dir");
			int flag = 0;
								
			//Looks for last '\' starting from end of string
			for(int i = pwdString.length() - 1; (i >= 3) && (flag == 0); i--){
									
				if(pwdString.substring(0,i).equals("C:\\")){
					System.setProperty("user.dir", "C:\\");
					flag =1;
				}
				else if (pwdString.charAt(i) == '\\'){
					System.setProperty("user.dir",pwdString.substring(0,i));
					flag = 1;
				}
			}
	    }
		//multiple backs like cd ..\..\..
		else if(message.startsWith("cd ..\\") || message.startsWith("cd..\\"))
		{
			String pwdString = System.getProperty("user.dir");
			int flag = 0;
			int countDashes = 0;
			
			//this if takes care of situation where msg is: cd ../..
			//there's only 1 '/' but two sets of .. 
			if (message.charAt(message.length()-1) == '.')
				countDashes = 1;

			//Looks for last '/' starting from end of string
			for(int i = message.length() - 1; (i >= 4); i--){
				if (message.charAt(i) == '\\'){
					countDashes++;
				}	
			}
			//now that we have # dir we want to go back
			int count = 0;
			//Looks for last '\' starting from end of string
			//Looks for last '\' starting from end of string
			for(int i = pwdString.length() - 1; (i >= 3) && (count < countDashes); i--){
									
				if(pwdString.substring(0,i).equals("C:\\")){
					System.setProperty("user.dir", "C:\\");
					count++;
				}
				else if (pwdString.charAt(i) == '\\'){
					System.setProperty("user.dir",pwdString.substring(0,i));
					count++;
				}
			}
			
		}
		//This is where we go forward in directories
		else
		{
			if(message.equals("cd")){
				pwdCommand();
			}
			else
			{
				int flag = 0;
				String outputString ="";
				File dir = new File(System.getProperty("user.dir"));
				File[] filesList = dir.listFiles();
				for (File file : filesList)	 {
					//Check's if directory asked for exists
					if (file.isDirectory() && file.getAbsolutePath().equals(System.getProperty("user.dir")+"\\"+message.substring(3, message.length()))){
						outputString += "Directory: " +file.getName() + "\n";
						System.setProperty("user.dir", System.getProperty("user.dir")+"\\"+message.substring(3, message.length()));
						flag = 1;		//if it exists flag is 1
					}
					//if we are in the C:\ directory
					else if(System.getProperty("user.dir").equals("C:\\")&& file.isDirectory() && file.getAbsolutePath().equals(System.getProperty("user.dir")+message.substring(3, message.length()))){
						outputString += "Directory: " +file.getName() + "\n";
						System.setProperty("user.dir", System.getProperty("user.dir")+message.substring(3, message.length()));
						flag = 1;		//if it exists flag is 1
					}
				}
				if(flag == 0)
				{
					if(message.length() <= 3)
						PS.println("Invalid command"); //handles if user enters 'cd.' or something without a directory name
					else
						PS.println("Error: There is no directory named -- "  + message.substring(3, message.length()));				}
			}
		}
    }
    
   public static void pwdCommand()
   {
   		String outputString = " --- Path --- \n";
		outputString += System.getProperty("user.dir") + "\n";
		PS.println(outputString);	
   }
   
   public static void getCommand(String message)
   {
   		try
		{
			//need to add check if file to send exists.. 
			String FILE_TO_SEND = message.substring(4);
			
			File myFile = new File(System.getProperty("user.dir")+ "\\"+ FILE_TO_SEND);
			
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
		catch(Exception e)
		{
			System.out.println("Something went wrong...");
		}
   }
   public static void putCommand(String message)
   {
   		try
		{
			input = sock.getInputStream();  
	        inReader = new BufferedReader(new InputStreamReader(sock.getInputStream()));  
	        outReader = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())); 
	        	
	        String FILE_TO_RECIEVE = message.substring(4);	
	        	
	        if ( !FILE_TO_RECIEVE.equals("") )
	        {  
	            /* Reply back to client with READY status */  
	            outReader.write("READY\n");  
	            outReader.flush();  
	        }  	
	        	
			wr = new FileOutputStream(new File(System.getProperty("user.dir")+ "\\" + FILE_TO_RECIEVE)); 
				
		    byte[] buffer = new byte[sock.getReceiveBufferSize()];  
		
		    int bytesReceived = 0;  
		
		    while((bytesReceived = input.read(buffer))>0)  
		    {  
		        /* Write to the file */  
		       wr.write(buffer,0,bytesReceived);  
		    }  
	
		}
		catch(Exception e)
		{
			System.out.println("Invalid file. Try again." + e);
		}
   }
}
