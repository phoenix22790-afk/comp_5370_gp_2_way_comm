import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

//import UI.MessageBox;
//import UI.UIHandler;

import java.io.*;
import java.net.*;


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
        if (t.equals("M")||t.equals("m")){
            known_type=true;
            listening=false;
        }

        else if (t.equals("L")||t.equals("l")){
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
            //SETUP SECTION
            Socket client_sock = new Socket(ip,port);
            System.out.println("Socket made");
            BufferedReader user_in = new BufferedReader(new InputStreamReader(System.in));
            
            DataInputStream in = new DataInputStream(client_sock.getInputStream());
            DataOutputStream out = new DataOutputStream(client_sock.getOutputStream());
            

            //ENTER MAIN LOOP
            while(continueConnection){
                if(listening==true){//CLIENT LISTENING
                    String server_msg=in.readUTF();
                    System.out.println(server_msg);
                }

                else{//CLIENT WRITING
                    String str_send=user_in.readLine(); //NEED TO FIGURE OUT EXIT METHOD
                    out.writeUTF(str_send);


                }
            }



        }



        else if (listening==true){//SERVER METHOD
            //SETUP
            ServerSocket server_sock = new ServerSocket(port); //note: may need to make this ip, 1, port
            System.out.println("Server Socket made, attempt to connect");
            Socket client_sock = server_sock.accept();
            System.out.println("Connected!");
            
            DataInputStream in = new DataInputStream(client_sock.getInputStream());
            DataOutputStream out = new DataOutputStream(client_sock.getOutputStream());
            BufferedReader user_in = new BufferedReader(new InputStreamReader(System.in));

            //ENTER MAIN LOOP
            while(continueConnection){
                if(listening==true){//SERVER LISTENING
                    String msg = in.readUTF(); // Read message from client
                    System.out.println(msg);
                }

                else{//SERVER WRITING
                    String str_send=user_in.readLine(); //NEED TO FIGURE OUT EXIT METHOD
                    out.writeUTF(str_send);
                }

            }
            server_sock.close();
            in.close();



        }

        // COMPLETE ANY ENDING PROCEDURES

        scan.close();
        //if this point is reached,the program has ended

        //System.out.println("Hello, World!");
    }


}
