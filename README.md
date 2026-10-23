# Report

Our project is a secure, encrypted two-way messaging app between two machines using AES-256-GCM encryption over TCP using a GUI coded within Java 22. 

## Details 
### Language Choice
There were several different factors that led to our decision to build this using java as our language. For starters, it’s easy to get running on multiple platforms and it was a language some of us already had some knowledge in. Another reason was that one of our team members felt confident in the language choice, and the rest of the group agreed, having some prior knowledge and wanting to improve their skills. We unfortunately didn’t end up porting it to other platforms, but it was a consideration.

## Application Details

### How it Works
Our app works by taking three initial inputs: the ip address, the port number, and the ability to enter as either a "Messenger" or "Listener". Based on this third input, the app will enter one of two modes, acting as either the client or server respectively. 


### Part 1: UI
We utilized a combination of Java’s Swing and AWT libraries to build a framework that we then used for an interactable interface including text input and buttons related to messaging and connection setup.

### Part 2: Networking
We utilized TCP in its most basic form to exchange information between machines. A direct LAN connection between machines on the same network is enough for us to demonstrate the security of the encryption method we utilized.

### Part 3: Encryption
We utilized techniques taught in class combined with resources from the Internet to complete this, mainly Oracle’s official Javadocs combined with a few tutorial sites.

