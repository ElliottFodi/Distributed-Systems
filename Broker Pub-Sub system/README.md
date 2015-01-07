Pub/Sub System READ ME

Any questions or concerns please feel free to contact me at elf3957@rit.edu


How to use pub sub system
When using the broker system create users on the server before trying to connect with a client.
Create stocks on the server before trying to subscribe to a stock.
subscribe to stocks before trying to add shares to a stock. 

Create users
Create stocks
Subscribe to stocks
Add shares to each user

Now buy, sell, add money...etc.

NOTE IMPORTANT: since this is a broker system the unsubscribe and remove Stock commands have changed slightly.
When the user unsubscribe from a stock they will no longer receive updates pertaining to that stock BUT when 
they issue the listMyStocks command, the stock will still appear. This is done because if a user in the real 
world if you no longer want to receive updates to a stock ... that doesn't mean all your stocks get erased.

When the remove stock command is given ... stock is then removed from all users and from the server.
 


Issues
If you try to break the system ... it will break. This system was designed for inputs to be generally correct.
Only simple error checking has been done. 

Case on the inputs does matter. If all upper case is used the command will be invalid.

If a stock DOES NOT EXIST and it is used in a command, it might crash.
If a user DOES NOT EXIST and it is used in a command, it might crash the system.
Use unsubscribe only after you have subscribed to a topic.
Use remove user only after you have added a user.
Use addShare only after the user has subscribed to a stock.
The server send out events based on IP address, so if you have multiple clients running on the same machine via local host.
Each client will receive up dates regardless of the user. 
Use common sense. 

How to compile and start the program
This program uses a 3rd party JSON library that is necessary for the program.
Navigate into the src directory where all the .java files are located. 
compile server
javac -cp <path to the library> *.java
example- javac -cp C:\Users\Elliott\Libraries\JSON\json.jar *.java

run server
java -cp <path to the library>;. main_thread
example- java -cp C:\Users\Elliott\Libraries\JSON\json.jar;. main_thread

compile admin client
javac -cp <path to the library> *.java
example- javac -cp C:\Users\Elliott\Libraries\JSON\json.jar *.java

run admin client
when running the admin client it takes the IP address of the server as an argument
java -cp <path to the library>;. main_thread
example- java -cp C:\Users\Elliott\Libraries\JSON\json.jar;. main_thread 127.0.0.1

compile client
javac -cp <path to the library> *.java
example- javac -cp C:\Users\Elliott\Libraries\JSON\json.jar *.java

run client
when running the client it takes the IP address of the server as an argument
java -cp <path to the library>;. main_thread
example- java -cp C:\Users\Elliott\Libraries\JSON\json.jar;. main_thread 127.0.0.1

NOTE if you are using Linux to compile and run, a ":" should be used instead of a ";"
when linking the external jar.

NOTE if not testing on local host Make sure you disable you fire walls and type in the 
IP address of the machine you are connecting to.  

Commands

How commands work:

Type the command, then you will be prompted to enter data.
If the command requires several things from the user, it will keep prompting the user 
until it has gathered all information.
For example the addTopic command will ask for a topic name.
Then it will ask the user to enter key words.
Once the user is done entering key words the command will be sent to the server.

commands available on the server and admin client
addUser- add user to on the server
	-takes a user name
	-takes initial money added to account

addStock- add stock on the server
	-takes topic name

removeUser- removes user from the server
	-takes user name

removeStock- removes stock from the server
	-takes topic name

listUsers- displays a list of the users 
	-no input needed

listStocks- lists all the stocks available
	-no input needed

sell- sells a stock 
	-takes stock name
	-takes selling price
	-takes seller name

buy- buys a stock that has been advertised
	- stock name
	- share ID 

listMyStocks- lists the stocks the user is subscribed to 
	-takes user name

subscribe- subscribes the user to a stock on the server
	-takes user name
	-takes stock name

unsubscribe- unsubscribe the user to a stock that the user is subscribed to 
	-takes user name
	-takes a stock name

unsubscribeAll- unsubscribe the user to all stocks the user is subscribed to 
	-takes user name

myMoney- lists the users current money
	-no input needed

addMoney- adds money to the users account
	-takes user name
	-takes amount of money to be added

addShares- adds shares to the users account 
	-takes user name
	-takes stock name
	-takes amount of shares to be added

removeShares- removes shares to the users account 
	-takes usder name
	-takes stock name
	-takes amount of shares to be added

myShares- lists the users stocks and how many shares of each stock
	-no input needed

start- starts the server
	-no input needed

stop system- stops the server
	-no input needed

Commands available on the client
sell- publishes an stock for sale
	-takes name of a stock on the server
	-takes selling price

subscribe- subscribes the user to a stock on the server
	-takes stock

unsubscribe- unsubscribe the user to a stock that the user is subscribed to 
	-takes a stock

unsubscribeAll- unsubscribe the user to all stocks the user is subscribed to 
	-no input needed

buy- buys a stock that was advertised
	- stock name
	- share ID

listStocks- lists all the stocks available
	-no input needed

listMyStocks- lists the stocks the user is subscribed to 
	-no input needed

newUserID- changes the user on the client, this can be used if you miss spelled 
	   the user name or just want to change to a different user
	-takes a new user name

myMoney- lists the amount of money the user has
	-no input needed

myShares- lists the users stocks and share for each stock
	-no input needed 

close - closes the client
	-no input needed


Features
Uses JSON to communicate between the server and the client.
Multi threading to deal with the bottle neck of accepting client connections.
Multi threading to deal with processing the queue.
Synchronization so data does not become corrupt due to the multiple threads.
Some simple error checking to verify users inputs. 
(this is not implemented on every command)



Explanation of program 
The system is based on a client server architecture. 
The server.
The server starts by spawning a listener thread to listen for clients, a event queue to 
store events, and a process events thread to remove events from the queue. When a client 
connects to the thread listening for connections it replies with a port and spawns a new 
thread to communicate with the client, thus leaving the listener thread open to accept new 
connections. The process events thread waits on the event queue until a notify is given.
When a notify is given the process events thread wakes up and starts removing events from 
the queue. For each event that is removed from the queue a send event thread is spawned. 
The send event thread sends events to the clients. If a client is not online the event is 
logged on the users profile and the next time the client connects the event is sent to the 
client. 

The client starts by spawning a thread to listen for any incoming events, it then waits for
user commands. When a user issues a command it makes a connection with the server, checks for 
any missed events, sends the command and its data, and waits for a reply.

The model is asynchronous, meaning once a command is issued the client disconnects from the 
server.


