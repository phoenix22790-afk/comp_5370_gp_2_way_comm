import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import UI.MessageBox;
import UI.UIHandler;

import java.io.*;
import java.net.*;


public class Network_App {

    public static void main(String[] args) throws Exception {
        boolean listening=false;
        boolean known_type=false;
        boolean continueConnection=true; // if false, this is basically the signal to stop and end the program
        Scanner scan=new Scanner(System.in);




        // >>>>>>>>>>> UI EXAMPLE <<<<<<<<<<<<<<

        UIHandler test = new UIHandler(Color.WHITE);
        JFrame frame = new JFrame();
        test.setPreferredSize(new Dimension(1080, 720));
        frame.add(test);
        frame.pack();
        frame.setVisible(true);
        test.addWindow(Color.BLUE, 100, 30, 100, 110);
        test.addText("Hello!", 12, Color.RED, 100, 100);

        // () -> {insert code here} is a lambda expression, you can put whatever you want inside of it and it'll execute when the user clicks the button.
        test.addButton(Color.GREEN, 100, 100, 200, 200, () -> {
            test.addText("The button worked!", 12, Color.RED, 100, 200);
        });


        // To update a message box, you MUST save it beforehand. the addMessageBox method returns the message box object that you can edit later
        MessageBox save = test.addMessageBox(Color.GREEN, 12, 0, 1080);

        // message -> is also a lambda expression, except there is output returning from it when it completes. you can use message within the lambda expression only
        test.addTextBox(12, 100, 30, 100, 110, Color.BLACK, message -> {
            save.setMessage(message);
            /*this line should be called whenever you get a message from the other client to display their message on screen.
            Here, it simply updates whenever you hit enter in the text entry field. Feel free to test it like this tho!
             */
        });



        // this is the nuke button that clears the entire screen and removes everything dont press it lol
        test.addButton(Color.RED, 100, 100, 0, 0, () -> {
            test.clearWindow();
        });


        // Handle close
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // TODO: Handle safe disconnection here
                System.exit(0);
            }
        });






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
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            DataOutputStream out = new DataOutputStream(client_sock.getOutputStream());
            

            //ENTER MAIN LOOP
            while(continueConnection){
                if(listening==true){//CLIENT LISTENING
                
                }

                else{//CLIENT WRITING
                    String str_send=in.readLine(); //NEED TO FIGURE OUT EXIT METHOD
                    out.writeUTF(str_send);


                }
            }



        }



        else if (listening==true){//SERVER METHOD
            //SETUP
            ServerSocket server_sock = new ServerSocket(port); //note: may need to make this ip, 1, port
            Socket client_sock = server_sock.accept();
            
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
