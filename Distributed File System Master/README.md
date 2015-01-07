{\rtf1\ansi\ansicpg1252\cocoartf1265\cocoasubrtf210
{\fonttbl\f0\fswiss\fcharset0 Helvetica;}
{\colortbl;\red255\green255\blue255;}
\margl1440\margr1440\vieww10800\viewh8400\viewkind0
\pard\tx720\tx1440\tx2160\tx2880\tx3600\tx4320\tx5040\tx5760\tx6480\tx7200\tx7920\tx8640\pardirnatural

\f0\fs24 \cf0 Ghost Server Read Me\
\
This requires json-simple-1.1.1.jar to run\
\
Start before the master.\
This will behave exactly as the master but will not do anything until the actual master fails. \
The IP address of the master will need to be specified. \
\
COMPILE\
javac -cp /path/to/json.jar *.java\
\
RUN\
jjava -cp /path/to/json.jar:. masterServerMain\
\
COMMANDS\
enter the corresponding number to use the command\
\
1 get list of commands \
	-displays all the commands available on the master\
2 get list of all files\
	-lists all the files available in the system\
3 get list of all chunk servers\
	-lists all the chunk servers connected to the master\
4 get list of all files in a chunk server\
	-input: chunk server ID\
	-lists all the files for the specified chunk server\
5 get IP address for a certain chunk server\
	-input: chunk server ID\
	-retrieves the IP address for the specified chunk server\
6 get the time when a chunk server last contacted the master server\
	-input: chunk server ID\
	- retrieves the time stamp when the chunk server last made contact\
7 get delete files for a certain chunk server\
	-input: chunk server ID\
	-lists all the files for that chunk server that are to be deleted\
8 get move files for a certain chunk server\
	-input: chunk server ID\
	-lists all the files for that chunk server that need to be replicated to another chunk server\
9 get chunk servers which have a certain file\
	-input: file name\
	-lists all the chunk servers that contain that file}