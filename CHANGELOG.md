# Change Log

## [2.0](https://github.com/Bimde/Blackjack-Server/compare/v1.2...v2.0) - 2015-12-09 (UNRELEASED)
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

## [1.02](https://github.com/Bimde/Blackjack-Server/compare/v1.0.1...v1.02) - 2015-12-07

### Fixes:
 - Hotfix for clients disconnecting before joining the lobby.

## [1.01](https://github.com/Bimde/Blackjack-Server/compare/v1.0...v1.01) - 2015-12-07

### Fixes:
 - Hotfix for instant blackjacks.

## 1.0 - 2015/12/07

Initial Release.