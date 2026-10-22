//Single App FOR TWO WAY CONNECTION SECURITY GROUP PROJECT

import java.io.*;
import java.net.*;

//Note 1: Need to have an overarching "listening" variable that user input can update at any time by clicking button in ui
//Note 2: Need to have a way to exit application. implement exiting via continueConnection boolean. 
//Note 3: Buffers also probably need to be global for this to work

public boolean continueConnection=True; // if false, this is basically the signal to stop and end the program


public static void main(String[]args){

    //take in inputs ip and port, which are the ip address and port address respectively 
    //also take in if starting as server/listening side, saved as Listening
    boolean listening;
    Scanner scan=new Scanner(System.in)
    System.out.println("Enter ip address: ");
    String ip=scan.nextLine();
    
    System.out.println("Enter port number: ");
    String port_str=scan.nextLine();
    int port=int(port_str);
    
    boolean known_type=False;
    while(known_type==False){
        System.out.println("Do you want to start as Messenger or Listener? (type M or L respectively)");
        String t=scan.nextLine();
        if (t=="M"||t=="m"){
            known_type=True;
            listening=False;
        }

        elif (t=="L"||t=="l"){
            known_type=True;
            listening=True;
        }
        else:
            System.out.println("Invalid response, please try again. ")
    }
    
    if (listening==True) //Acting as Server Side
        ServerSide(ip, port);
    
    else  //Acting as Client Side
        ClientSide(ip, port);  
    
    //if this point is reached,the program has ended
}



public void ServerSide(String ip, int port){ //For if app acting as Server
    ServerSocket server_sock = new ServerSocket(port); //note: may need to make this ip, 1, port
    Socket client_sock = server_sock.accept(); 
    in = new DataInputStream(new BufferedInputStream(client_sock.getInputStream()));
    
    while(continueConnection):
        if(listening==True)
            ServerListen();
        else
            ServerSend();
    

    server_sock.close();
    in.close();
}



public void ClientSide(String ip, int port){ //For if app acting as Client
    socket client_sock = new Socket(ip,port);

    in = new BufferedReader(new InputStreamReader(server_sock.getInputStream()));// Read input 
    out = new DataOutputStream(client_sock.getOutputStream());// Send data to server
    
    
    while(continueConnection):
        if(listening==True)
            ClientListen();
        else
            ClientSend(in,out);


    client_sock.close();
    in.close();
    out.close();
}


//planning on using .readUTF() with input stream reader to read the information,

// was planning on readLine() for user string inputs, but I need to either figure out a timeout or way to exit this
// for when user doesnt want to send a message or is trying to switch to listen

public void ClientListen(){
}

public void ClientSend(BufferedReader in,DataOutputStream out){
    String str_send=in.readLine(); //NEED TO FIGURE OUT EXIT METHOD
    out.writeUTF(str_send);
}

public void ServerListen(){
    try{
        String str_listen=in.readUTF();
        System.out.println(str_listen);
    }
    catch (Exception e){
        System.out.println("error! ",e); // No new messages, or some sort of error. 
    }
    
}

public void ServerSend(){}

