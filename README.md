Distributed-Systems
===================

Programs from Distributed Systems course 

Pub-Sub system:
The publish/subscribe system is a basic publish subscribe asynchronous architecture. Clients can publish,  advertise, subscribe, unsubscribe, and get lists of there subscriptions. The purpose of this project was to deal with the multiple connections simultaneously and ensure users do not miss events. If a user is off line when an event is published the user will receive the event next time they log online.    

Broker Pub-Sub system:
This is the publish/subscribe system applied to a stock market setting. Users can buy and sell stocks. There is a remote admin client which allows for remote administration of the system. Each stock has a price associated with it, each buyer and seller has money transferred when a stock is bought. The purpose of this project was to applied the publish subscribe system to a real world setting and add some new features.  

Distributed File System Master:
For the Distributed File system it was my task to design the master. I uploaded only the master since that was the component I worked on. The master is the main server that manages all the connections to the chunk servers. Chunk servers are the servers that actually store the files. The master was reasonable for the following tasks:

chunk servers connecting, 
chunk servers disconnecting,
files being added,
files being renamed,
files being deleted,
files failing to copy,
master failing,
random disconnect of chunk servers, 

client connections
clients adding a file,
clients requesting a file,
clients deleting a file,

If you would like to see the rest of the system please feel free to contact me, and I will up load the rest of the system.
  

