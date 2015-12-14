# Blackjack Server

This server program acts as both a dealer and the server for hosting Blackjack games with up to a maximum of 6 clients per room, with dynamic allocation of players to new game rooms once the current room is full.

The protocol of this program is laid out in this [Google Doc](https://docs.google.com/document/d/1TitWhC7pa1LwOa1-9aaW1HGAJzgxTehDEKdn49hRspE/edit#).

### Instructions:
 - Run the `Server.jar`.
 - Follow the rest of the instructions in the GUI.

### Notes:
To enable/disable the debug messages or change other constants used in the program (i.e. the delay between messages being sent), change the values of the constants at the top of `Server.java` (lines 27-29). Please note that we cannot guarantee that the server will run as smoothly with some of these constants at non-original values due to dependancy of delays for various synchronized elements.
