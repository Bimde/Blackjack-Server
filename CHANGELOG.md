# Change Log
## [2.1.1](https://github.com/Bimde/Blackjack-Server/compare/v2.1...v2.1.1) - 2015-12-09
### Changes:
 - Organized code.
	- Removed the Leaderboard object (unused).
	- Applied formatting to make all files consistent.
	- Added more comments and documentation.

### Fixes:
 - Fixed the timer from not starting when a player disconnects and makes it so that the entire lobby is ready.

## [2.1](https://github.com/Bimde/Blackjack-Server/compare/v2.0.1...v2.1) - 2015-12-09
### Changes:
 - All output, including error messages are sent to JFrame.
 - JScrollPane auto scrolls.

### Fixes:
 - Prevents user from using invalid port (re-asks if not valid).
 - Doesn't ask for user input from console.

## [2.0.1](https://github.com/Bimde/Blackjack-Server/compare/v2.0...v2.0.1) - 2015-12-09
### Changes:
 - Changed it so that everything uses the same delay.

### Fixes:
 - Fixed spectators from not receiving messages.
 - Fixed exception from entering an invalid bet.

## [2.0](https://github.com/Bimde/Blackjack-Server/compare/v1.2...v2.0) - 2015-12-09
### Features:
 - Added multiple servers per IP/port.
 	- Run the CentralServer program in order to set up a new server that hosts multiple servers/game rooms.

### Changes:
 - Added debugging message flag.
	- Use constant 'DEBUG' in server class to enable / disable debugging messages.
	- All 'System.out.println' calls transferred to Server#println calls, which allow for significantly less 'if(DEBUG)' statements.
 - Organized code.
	- Removed Spectator class as it wasn't being used.
	- Improved documentation.
	- Made CentralServer class instantiable and removed static methods / variables.
	- Moved server assignment from Client to CentralServer.
	- Removed unused methods.

### Fixes:
 - Fixed issue where server remains in memory after game ends.
	- Now the server is removed from the main server list.
	- The message timer stops.
	- Server-related threads all close
 - Fixed bug where server ends game before finishing sending all outstanding messages.
	- Now the server prevents new messages from being added to the message queue after the game ends.
	- Guarantees all messages in queue are sent before ending game by preventing timer being stopped until the size of the message queue is 0.
 - Fixed potential bet overflow.
 - Fixed messages synchronization errors.
 - Hotfix for client seed guessing (see [#40](https://github.com/Bimde/Blackjack-Server/issues/40)).
 	- Will now shuffle a random number of times whenever the dealer decides to shuffle.
 - Fixed double down not updating player's coins properly.
 	- Previously updated the player's coins twice.
 - Fixed disconnecting players not broadcasting when a player loses.

## [1.2](https://github.com/Bimde/Blackjack-Server/compare/v1.1...v1.2) - 2015-12-08
### Changes:
 - Added a dialog box for choosing IP address/port for the server tester.

### Fixes:
 - Fixed disconnecting players with less coins than the minimum bet.
 - Fixed the 15-second lobby timer.
 - Hotfix for resetting player numbers in the lobby.

## [1.1](https://github.com/Bimde/Blackjack-Server/compare/v1.02...v1.1) - 2015-12-08
### Features:
 - Added a 15-second timer when everybody is ready in order to wait on more people to join the game.

### Fixes:
 - Fixed client disconnection causing server to go on infinite loop.
 - Fixed dealer blackjack on first two cards preventing other player's turns.
 - Fixed the server preventing clients from becoming players if 6 or more clients where connected (even if they weren't players yet).
 - Fixed kicking players once they run out of coins.

## 1.0.2 - 2015-12-07
### Fixes:
 - Hotfix for clients disconnecting before joining the lobby.

## 1.0.1 - 2015-12-07
### Fixes:
 - Hotfix for instant blackjacks.

## 1.0 - 2015-12-07
Initial Release.