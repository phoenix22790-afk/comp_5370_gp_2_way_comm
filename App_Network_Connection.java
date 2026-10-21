//Single App FOR TWO WAY CONNECTION SECURITY GROUP PROJECT

import java.io.*;
import java.net.*;


//Note 1: Need to have an overarching "listening" variable that user input can update at any time by clicking button in ui
//Note 2: Need to have a way to exit application. implement exiting via continueConnection boolean. 
//Note 3: Buffers also probably need to be global for this to work


public static void main(String[]args){

    //take in inputs ip and port, which are the ip address and port address respectively 
    //also take in if starting as server/listening side, saved as Listening

    boolean continueConnection=True; // if false, this is basically the signal to stop and end the program
    
    if (listening==True) //Acting as Server Side
        ServerSide(ip, port);
    
    else  //Acting as Client Side
        ClientSide(ip, port);  
    
    //if this point is reached,the program has ended
}



public void ServerSide(String ip, int port){ //For if app acting as Server
    ServerSocket server_sock = new ServerSocket(port); //note: may need to make this ip, 1, port
    Socket client_sock = server_sock.accept(); 

    while(continueConnection):
        if(listening==True)
            ServerListen();
        else
            ServerSend();
    

    server_sock.close();
}


public void ClientSide(String ip, int port){ //For if app acting as Client
    socket client_sock = new Socket(ip,port);

    in = new BufferedReader(new InputStreamReader(System.in));// Read input from terminal
    out = new DataOutputStream(client_sock.getOutputStream());// Send data to server
    
    while(continueConnection):
        if(listening==True)
            ClientListen();
        else
            ClientSend();


    client_sock.close();
}




//planning on using .readUTF() with input stream reader to read the information,

// was  planning on readLine() for user string inputs, but I need to either figure out a timeout or way to exit this
//  for when user doesnt want to send a message or is trying to switch to listen



public void ClientListen(){}
public void ClientSend(){}
public void ServerListen(){}
public void ServerSend(){}
