import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Network_App {

    public static void main(String[] args) throws Exception {
        boolean listening=false;
        boolean known_type=false;
        boolean continueConnection=true; // if false, this is basically the signal to stop and end the program
        Scanner scan=new Scanner(System.in);
        
        //receive ip
        System.out.println("Enter ip address: ");
        String ip=scan.nextLine();
        
        //receive port
        System.out.println("Enter port number: ");
        int port=scan.nextInt();
    

        //determine if Server or Client
        while(known_type==false){
        System.out.println("Do you want to start as Messenger or Listener? (type M or L respectively)");
        String t=scan.nextLine();
        if (t=="M"||t=="m"){
            known_type=true;
            listening=false;
        }

        else if (t=="L"||t=="l"){
            known_type=true;
            listening=true;
        }
        else
            System.out.println("Invalid response, please try again. ");
        }
        
    
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        //ENTER ACTUAL SERVER/CLIENT STUFF NOW
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


        if (listening==false){//CLIENT METHOD
            
        
        
        
        } 






        else if (listening==true){//SERVER METHOD
        
            ServerSocket server_sock = new ServerSocket(port); //note: may need to make this ip, 1, port
            Socket client_sock = server_sock.accept(); 
            DataInputStream in = new DataInputStream(new BufferedInputStream(client_sock.getInputStream()));
            
            while(continueConnection){
                if(listening==true){//SERVER LISTENING

                }

                else{//SERVER WRITING*

                }
            
            }
            server_sock.close();
            in.close();



        }










            //if this point is reached,the program has ended
        System.out.println("Hello, World!");
    }


}


