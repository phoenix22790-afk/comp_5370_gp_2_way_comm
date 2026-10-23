# Report

Our project is a secure, encrypted two-way messaging app between two machines using AES-256-GCM encryption over TCP using a GUI coded within Java 22. 

## Details 
### Language Choice
There were several different factors that led to our decision to build this using java as our language. For starters, it’s easy to get running on multiple platforms and it was a language some of us already had some knowledge in. Another reason was that one of our team members felt confident in the language choice, and the rest of the group agreed, having some prior knowledge and wanting to improve their skills. We unfortunately didn’t end up porting it to other platforms, but it was a consideration.

## Application Details

### How it Works
Our app works by taking three initial inputs: the ip address, the port number, and the ability to enter as either a "Messenger" or "Listener". Based on this third input, the app will enter one of two modes, acting as either the client or server respectively. The app can then switch between a "listening" and "sending" mode, to either receive and print messages from the connected application or send messages based on user input into the text bar.

### Functionality
We're working on it.

### Part 1: UI
We utilized a combination of Java’s Swing and AWT libraries to build a framework that we then used for an interactable interface including text input and buttons related to messaging and connection setup.
<img width="834" height="564" alt="image" src="https://github.com/user-attachments/assets/fdd6d702-3386-4a36-ab1c-65b71310f8ac" />

At the top of the user interface, there is a button to switch the mode between listening and writing, as well as an indicator that displays the current mode. Alongside this, there is another indicator to show the status of the connection. 
At the bottom of the interface, there is a text box that can be used to send messages to the connected application.

### Part 2: Networking
As mentioned before, any instance of the communication application will be set to a server or client mode when first started. This will then create either a ServerSocket or Socket object based on the parameters given, which can then connect to another instance of this application. 

We utilized TCP sockets in order to form the network connection that allows for information to be exchanged between machines. A direct LAN connection between machines on the same network is enough for us to demonstrate the security of the encryption method we utilized.

### Part 3: Encryption
We utilized techniques taught in class combined with resources from the Internet to complete this, mainly Oracle’s official Javadocs combined with a few tutorial sites.

