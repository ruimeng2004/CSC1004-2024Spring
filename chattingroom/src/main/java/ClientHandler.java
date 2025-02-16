import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//The ClientHandler class is responsible for communicating with the client
public class ClientHandler implements Runnable{
    //keep track of client object
     public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
//establish a connection between client and server
    private Socket socket;
//read message that has been sent by the client
    private BufferedReader bufferedReader;
//send message to clients
    private BufferedWriter bufferedWriter;
//client username to represent each client
    private String clientUsername;
    //mainly use to count the number of message sent by each client
    private int messageCount = 0;
    //To record the chat history into a local file
    private BufferedWriter fileWriter;


    public ClientHandler(Socket socket, BufferedWriter fileWriter){
        try{
             this.socket = socket;
             //wrap the byte stream into a character stream
             this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             this.clientUsername = bufferedReader.readLine();
             //add the client user name
             clientHandlers.add(this);
             broadcastMessage("SERVER " + clientUsername + " has entered the chat!" );
             //call displayCurrentTime method here
             displayCurrentTime();
        } catch(IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
        //Assign fileWriter
        this.fileWriter = fileWriter;
    }


    //run on a separate thread
    @Override
    public void run() {
        String messageFromClient;

        while(socket.isConnected()){
            try{
                //a blocking operation
                messageFromClient = bufferedReader.readLine();
                if(messageFromClient == null){
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break;
                }
                messageCount++;
                broadcastMessage(messageFromClient);
                //write each message to the file
                fileWriter.write(messageFromClient);
                fileWriter.newLine();
                fileWriter.flush();
            }catch(IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }

    }


    public void broadcastMessage(String messageTosend){
        //loop through our arraylist
        for(ClientHandler clientHandler : clientHandlers){
            try{
                if(!clientHandler.clientUsername.equals(clientUsername)){
                    clientHandler.bufferedWriter.write(messageTosend);
                    clientHandler.bufferedWriter.newLine();
                    //manually flush it to fill it over
                    clientHandler.bufferedWriter.flush();
                }
            }catch(IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    //To signal that a user has left the chat
    public void removeClientHandler(){
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUsername + "has left the chat!");
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeClientHandler();
        try{
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket !=null){
                socket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void displayCurrentTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String timestamp = sdf.format(new Date());
        System.out.println("[" + timestamp+ "]" +"User " + clientUsername + " entered the chatroom. Message sent: " + messageCount);
    }


}
