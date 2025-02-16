import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//The server class mainly responsible for listening to clients who want to connect and create new thread
public class Server {

    //listening for incomming connections for clients and create a socket object
    private ServerSocket serverSocket;
    //BufferedWriter to write chat records to a file
    private BufferedWriter fileWriter;

    public Server(ServerSocket serverSocket, BufferedWriter fileWriter){
        this.serverSocket = serverSocket;
        this.fileWriter = fileWriter;
    }

//responsible for keeping a server running
    public void startServer(){

        try{
            while(!serverSocket.isClosed()){
                //a blcoking method meaing the program will be hold here until the client connect
                Socket socket = serverSocket.accept() ;
                //after typing the username , broadcast that a new client has connected
                System.out.println("A new cilent has connected!");
                //To implement the multiple client function
                ClientHandler clientHandler = new ClientHandler(socket, fileWriter);
                //To keep the thread running
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }catch(IOException e){
            e.printStackTrace();

        }
    }

    //if an error occurs , we can shut down our socket
    public void closeServerSocket(){
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        //open a file for reading chat records
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter("chat_records.txt"));
        //create a server socket and start the server, our server will listening to clients who own the port number "1234"
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket, fileWriter);
        server.startServer();
    }
}
