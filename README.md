# SimpleFileTransferServer
This is a working client-server file transfer system using Socket API. The application replicates the command line environment of the system hosting the server. Allowing a client to navigate the server machines directory, use common command line commands ('ls','cd','pwd' 'mkdir' etc) as well as send and pull files to the server. The file transfer handles files of any type including binary, as well as handles network byte ordering heterogeneity.  The application was coded in Java.

#Compiling and Running
- Download the client and server files on the two machines you wish
- The client must set up and know the IP Address of the server they wish to connect to
- On the server machine compile the java code and then start the application
- Then do the same for client.
- You should be connected; The client should see the directory path of server machine

#Commands
Along with the simple commands such as pwd, cd, ls, etc. A use can do the following:

'get [file-name]': Downloads the specific file from the server and stores it on the client machine. The client file is created with the same name as it had on the server machine. You can specify where the file is saved on the client machine but by default it's saved in the same folder as where the client is ran from.

'put [file-name]': Uploads the file from the client machine and stores it in the current directory of the server machine. On the server, it is given the same name it has on the client machine.

The get/put commands convert the file to be transferred into binary and then using the TCP protocol transfer them to their destination. Tested formats that can be transferred include: .pdf, .mp3, .txt, .jpg, .exe

# Bugs
If the user tries to 'put' a file that does not exist in the client side current directory, an empty file with the that name will still be created in the server side directory. For example, if client tries to do: 'put aFile.txt' but 'aFile.txt' does not exist in the client's current directory, 'aFile.txt' will still be created on the server side but will just be a blank file.  
