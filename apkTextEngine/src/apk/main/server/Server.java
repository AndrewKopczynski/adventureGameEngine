package apk.main.server;

//import static apk.main.engine.Logger.logDebug;
import static apk.reference.EntType.*;
import static apk.reference.MessageType.*;
import apk.reference.ActorIntializationException;
import apk.reference.IDConflictException;
import apk.main.engine.Logger;
import apk.main.engine.Save;
import apk.main.engine.Parse;
import apk.main.engine.Actor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

/*
* A chat server converted for usage in a game engine. Weird, huh?
*/
public class Server
{
	// The server socket.
	private static ServerSocket serverSocket = null;
	// The client socket.
	private static Socket clientSocket = null;
	
	// This chat server can accept up to maxClientsCount clients' connections.
	private static final int maxClientsCount = 4; //z lowered for testing
	private static final clientThread[] threads = new clientThread[maxClientsCount];
	
	public static void main(String args[])
	{
		/**all modified lines are marked with //z for the sake of clarity*/
		
		System.out.println("SERVER IS STARTING UP!"); //z
		Logger.clear(); //z
		
		try
		{	
			System.out.println("LOADING SAVE...");
			Save.load("save.xml");
			System.out.println("SAVE READ WITHOUT ERRORS!");
			//new World("maps/test_map.xml", "gfx/standard_tileset.xml");
		}
		catch (FileNotFoundException e)
		{
			System.out.println("Save couldn't be read! Closing server...");
			e.printStackTrace();
			System.exit(1);
		}
		catch (ActorIntializationException e)
		{
			System.err.println(e.getMessage());
		}
		
		// The default port number.
		int portNumber = 2222;
		if (args.length < 1)
		{
			System.out.println("USAGE: java server [portNumber]\n" + "NOW USING PORT NUMBER: " + portNumber); //z
		}
		else
		{
			portNumber = Integer.valueOf(args[0]).intValue();
		}
	
		/*
		* Open a server socket on the portNumber (default 2222). Note that we can
		* not choose a port less than 1023 if we are not privileged users (root).
		*/
		try 
		{
			System.out.println("OPENING CONNECTION ON PORT " +  portNumber); //z
			serverSocket = new ServerSocket(portNumber);
		}
		catch (IOException e)
		{
			System.out.println("FAILED TO START SERVER! PORT NUMBER IS PROBABLY ALREADY IN USE!");
			e.printStackTrace();
		}
	
		/*
		* Create a client socket for each connection and pass it to a new client
		* thread.
		*/
		while (true)
		{
			try
			{
				clientSocket = serverSocket.accept();
				
				System.out.println("CLIENT CONNECTING"); //z
				System.out.println(clientSocket.toString());
				
				int i = 0;
				for (i = 0; i < maxClientsCount; i++)
				{
					if (threads[i] == null)
					{
						(threads[i] = new clientThread(clientSocket, threads)).start();
						break;
					}
				}
				
				if (i == maxClientsCount)
				{
					PrintStream os = new PrintStream(clientSocket.getOutputStream());
					os.println("Server is full (" 
						+ maxClientsCount 
						+ "/"
						+ maxClientsCount
						+ " maximum clients)."); //z
					os.close();
					clientSocket.close();
				}
			}
			catch (IOException e)
			{
				System.out.println(e);
			}
		}
	}
}

/**
* The chat client thread. This client thread opens the input and the output
* streams for a particular client, ask the client's name, informs all the
* clients connected to the server about the fact that a new client has joined
* the chat room, and as long as it receive data, echos that data back to all
* other clients. The thread broadcast the incoming messages to all clients and
* routes the private message to the particular client. When a client leaves the
* chat room this thread informs also all the clients about that and terminates.
*/

/* the above may or may not be relevant by the time i'm done converting this to my engine */
class clientThread extends Thread
{	
	private String clientName = null;
	private BufferedReader m_in = null; // z - mine
	private PrintStream os = null;
	private Socket clientSocket = null;
	private final clientThread[] threads;
	private int maxClientsCount;
	
	//private ServerProtocol protocol = null; //z
	private Actor m_player = null;
	private static Parse m_p = new Parse();
	
	private boolean m_connected = true;
	
	public clientThread(Socket clientSocket, clientThread[] threads)
	{
		this.clientSocket = clientSocket;
		this.threads = threads;
		maxClientsCount = threads.length;
	}
	
	public void run()
	{
		int maxClientsCount = this.maxClientsCount;
		clientThread[] threads = this.threads;

		try
		{
			/* Create input and output streams for this client. */
			m_in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); //z
				
			os = new PrintStream(clientSocket.getOutputStream());
			String name;
				
			while (true) 
			{
				os.println("Enter your name.");
				name = m_in.readLine().trim(); //z
	
				if (name.indexOf("@") == -1 && name.indexOf(" ") == -1)  //z - added space check too
				{
					break;
				}
				else 
				{
					os.println("Don't use '@' symbols or spaces in your username.");
				}
			}
			
			/* Welcome the new the client. */
			os.println(name + " has connected."
					+ "\n@quit to disconnect.");

			synchronized (this)
			{
				for (int i = 0; i < maxClientsCount; i++)
				{
					if (threads[i] != null && threads[i] == this)
					{
						clientName = "player_" + name;
						//protocol = new ServerProtocol(clientName); //z
						if ((m_player = Actor.getActorsByName(clientName)) == null)
						{
							try
							{
								//URL playerURL = new File("ent/" + clientName + ".xml").toURI().toURL();
								m_player = new Actor("ent/" + clientName + ".xml");
							}
							catch (ActorIntializationException e)
							{
								System.out.println("Actor failed to intialize!");
								e.printStackTrace();
								
								System.out.println("DID NOT LOAD FROM FILE");
								m_player = new Actor(0, 0, 0, clientName, TYPE_ALIVE, 30, 30);
							}
							catch (IDConflictException e)
							{
								System.out.println("Conflicting IDs!");
								e.printStackTrace();
								
								System.out.println("DID NOT LOAD FROM FILE");
								m_player = new Actor(0, 0, 0, clientName, TYPE_ALIVE, 30, 30);
							}
						}
						break;
					}
				}
				for (int i = 0; i < maxClientsCount; i++)
				{
					if (threads[i] != null && threads[i] != this)
					{
						threads[i].os.println("*** " + name + " has connected! ***");
					}
				}
			}
			
			/* Start the conversation. */
			while (m_connected)
			{
				String line = m_in.readLine();
				int formatLines = 0; //z
				int messageType = 0; //z
				
				/** collapse input first */
				String[] msgRecieved  = m_p.parse(m_player, line);
				String msgFormatted = "";
				
				try
				{
					int m = Integer.parseInt(msgRecieved[0]);
					/**message type stuff here*/
					if (m == MSG_PERSONAL)
					{
						messageType = MSG_PERSONAL;
						formatLines = MSG_PERSONAL_LENGTH;
					}
					else if (m == MSG_ROOMWIDE)
					{
						messageType = MSG_ROOMWIDE;
						formatLines = MSG_ROOMWIDE_LENGTH;
					}
					else if (m == MSG_AREAWIDE)
					{
						messageType = MSG_AREAWIDE;
						formatLines = MSG_AREAWIDE_LENGTH;
					}
					else
					{
						System.out.println("no message type!");
					}
				}
				catch(Exception e)
				{
					System.out.println("Bad message type!");
					//e.printStackTrace();
				}
				
				//System.out.println("Message length: " + msgRecieved.length);
				//System.out.println("FormatLines: " + formatLines);
				
				for (int i = formatLines; i < msgRecieved.length; i++)
				{
					msgFormatted += msgRecieved[i];
					if (i < msgRecieved.length - 1)
					{
						msgFormatted += "#";
					}
				}
				
				if(msgRecieved.length > 0 && msgRecieved[0] != null)
				{
					synchronized (this)
					{
						for (int i = 0; i < threads.length; i++) //max clients -> threads
						{
							/** MESSAGE_TYPE_SELF */
							if (messageType == MSG_PERSONAL
								&& threads[i] != null
								&& threads[i] == this
								&& threads[i].clientName != null
								)
							{
								//System.out.println("did self message for " + m_player.getName());
								//this.os.println(get);
								threads[i].os.println(msgFormatted);
								//threads[i].os.println(protocol.processInput(line));
							}
							
							/** MESSAGE_TYPE_ROOM */
							if (messageType == MSG_ROOMWIDE
								&& threads[i] != null 
								&& threads[i].clientName != null
								&& threads[i].m_player.getXYZ().equals(m_player.getXYZ()))
							{
								//System.out.println("did room message for " + m_ent.getXYZ());
								threads[i].os.println(msgFormatted);
								/*TODO: first attempt to hook up the game to the engine: */
								//threads[i].os.println(protocol.processInput(line)); //TODO: cleanup
							}
							
							if (line.equalsIgnoreCase("@quit"))
							{
								m_connected = false;
							}
							if (line.length() > 0)
							{
								//System.out.println(line);
								//System.out.println(line.equalsIgnoreCase("@quit"));
							}
						}
					}
				}
			}//end of while
					
			//System.out.println(msgRecieved[0]);
				
			synchronized (this)
			{
				for (int i = 0; i < threads.length; i++)
				{
					if (threads[i] != null && threads[i] != this && threads[i].clientName != null)
					{
						//m_ent.writeSave();
						
						threads[i].os.println("*** " + name + " disconnected. ***");
						System.out.println("*** Disconected " + name + " ***");
						os.println("*** Disconected " + name + " ***");
					}
				}
			}
			
		
			/*
			* Clean up. Set the current thread variable to null so that a new client
			* could be accepted by the server.
			*/
			synchronized (this)
			{
				for (int i = 0; i < maxClientsCount; i++)
				{
					if (threads[i] == this)
					{
						//m_player.writeSave();
						threads[i] = null;
					}
				}
			}
			
			/*
			* Close the output stream, close the input stream, close the socket.
			*/
			//is.close();
			m_in.close();
			os.close();
			clientSocket.close();
		}
		catch (IOException e)
		{
			//z something should be here probs
		}
		/*catch (Exception e) //TODO drop client if they get an exception
		{
			for (int i = 0; i < threads.length; i++)
			{
				if (threads[i] != null && threads[i] == this)
				{
					threads[i].os.println("Something went wrong!");
					System.out.println(e.getMessage());
				}
			}
		}*/
	}
}

/*class ServerProtocol
{
	private WorldEntity ent = null;
	private static Parse m_p = new Parse();
	private String m_name = null; //z TODO: rework probably, rough pass
	
	private static final int WAITING_FOR_LOGIN = 0;
	private static final int CONNECTED = 1;
	
	private static int state = CONNECTED;
	
	public ServerProtocol(String name)
	{
		m_name = name;
		
		 I spent half an hour trying to figure out why m_p.parse() was
		 * causing null pointer exceptions only to realize that I didn't
		 * assign the new WorldEntity to ent.
		 
		ent = new WorldEntity(0, 0, 0, name, 30, 30);
		
	}
	public String processInput(String in) 
	{	
			
	}
}*/
