package apk.main.server;

/*import apk.main.engine.Logger;
import apk.main.engine.Map;
import apk.main.engine.Parse;
import apk.main.engine.WorldEntity;*/

/*import java.net.*;
import java.io.*;*/

import java.io.BufferedReader;
//import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

/*
* A chat server that delivers public and private messages.
*/
public class Server {

// The server socket.
private static ServerSocket serverSocket = null;
// The client socket.
private static Socket clientSocket = null;

// This chat server can accept up to maxClientsCount clients' connections.
private static final int maxClientsCount = 10;
private static final clientThread[] threads = new clientThread[maxClientsCount];

public static void main(String args[])
{
	/**all modified lines will start with //z for the sake of clairity*/
	
	//z
	System.out.println("Starting server...");
	
	// The default port number.
	int portNumber = 2222;
	if (args.length < 1)
	{
		System.out.println("Usage: java MultiThreadChatServerSync <portNumber>\n"
			+ "Now using port number: " + portNumber);
	} else
	{
		portNumber = Integer.valueOf(args[0]).intValue();
	}

	/*
	* Open a server socket on the portNumber (default 2222). Note that we can
	* not choose a port less than 1023 if we are not privileged users (root).
	*/
		try 
		{
			//z
			System.out.println("Opening connection...");
			serverSocket = new ServerSocket(portNumber);
		}
		catch (IOException e)
		{
			System.out.println(e);
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
				//z
				System.out.print("Client connecting... ");
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
					os.println("Server is full (" + maxClientsCount + " maximum clients)."); //z
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

/*
* The chat client thread. This client thread opens the input and the output
* streams for a particular client, ask the client's name, informs all the
* clients connected to the server about the fact that a new client has joined
* the chat room, and as long as it receive data, echos that data back to all
* other clients. The thread broadcast the incoming messages to all clients and
* routes the private message to the particular client. When a client leaves the
* chat room this thread informs also all the clients about that and terminates.
*/
class clientThread extends Thread
{
	
	private String clientName = null;
	//private DataInputStream is = null;
	private BufferedReader m_in = null; // mine
	private PrintStream os = null;
	private Socket clientSocket = null;
	private final clientThread[] threads;
	private int maxClientsCount;
	
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
			//TODO: REPLACE BELOW LINE IF THIS BREAKS 
			//is = new DataInputStream(clientSocket.getInputStream());
			m_in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); //z
				
			os = new PrintStream(clientSocket.getOutputStream());
			String name;
				
			while (true) 
			{
				os.println("Enter your name.");
				name = m_in.readLine().trim(); //z
				//name = is.readLine().trim();
	
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
					+ "\nTo leave enter /quit in a new line.");

			synchronized (this)
			{
				for (int i = 0; i < maxClientsCount; i++)
				{
					if (threads[i] != null && threads[i] == this)
					{
						clientName = "@" + name;
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
			while (true)
			{
				//String line = is.readLine();
				String line = m_in.readLine();
				if (line.startsWith("/quit"))
				{
					break;
				}
				/** PRIVATE MESSAGE - STUDY THIS AND FIGURE IT OUT */
				/* If the message is private sent it to the given client. */
				if (line.startsWith("@"))
				{
					String[] words = line.split("\\s", 2);
					if (words.length > 1 && words[1] != null)
					{
						words[1] = words[1].trim();
						if (!words[1].isEmpty())
						{
							synchronized (this)
							{
								for (int i = 0; i < maxClientsCount; i++)
								{
									if (threads[i] != null && threads[i] != this 
											&& threads[i].clientName != null 
											&& threads[i].clientName.equals(words[0]))
									{
										threads[i].os.println("<" + name + "> " + words[1]);
										/*
										* Echo this message to let the client know the private
										* message was sent.
										*/
										this.os.println(">" + name + "> " + words[1]);
										break;
									}
								}
							}
						}
					}
				} 
				else
				{
					/* The message is public, broadcast it to all other clients. */
					synchronized (this)
					{
						for (int i = 0; i < maxClientsCount; i++)
						{
							if (threads[i] != null && threads[i].clientName != null)
							{
								threads[i].os.println("<" + name + "> " + line);
							}
						}
					}
				}
			}
			synchronized (this)
			{
				for (int i = 0; i < maxClientsCount; i++)
				{
					if (threads[i] != null && threads[i] != this && threads[i].clientName != null)
					{
						threads[i].os.println("*** " + name + " disconnected from the chatroom. ***");
					}
				}
			}
			os.println("*** Disconected " + name + " ***");
			
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
	}
}


/*public class Server extends Thread
{
	final static int m_portNumber = 4000; //TODO: change later
	
	public static void main(String[] args)
	{
		try
		{
			System.out.println("Starting server on " + m_portNumber);
			new Server().startServer();
		}
		catch (Exception e)
		{
			System.out.println("I/O failure: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void startServer() throws Exception
	{
		ServerSocket serverSocket = null;
		boolean listening = true;
		
		try
		{
			serverSocket = new ServerSocket(m_portNumber);
		}
		catch (IOException e)
		{
			System.err.println("Could not listen on port: " + m_portNumber);
			System.exit(-1);
		}
		while (listening)
		{
			handleClientRequest(serverSocket);
		}
		serverSocket.close();
	}
	
	private void handleClientRequest(ServerSocket serverSocket)
	{
		try
		{
			new ConnectionRequestHandler(serverSocket.accept()).run();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
		
	public class ConnectionRequestHandler implements Runnable
	{
		private Socket m_socket = null;
		private PrintWriter m_out = null;
		private BufferedReader m_in = null;
		
		public ConnectionRequestHandler(Socket socket)
		{
			m_socket = socket;
		}
		
		public void run()
		{
			System.out.println("Client connected to socket: " +  m_socket.toString());
			
			try
			{
				m_out = new PrintWriter(m_socket.getOutputStream(), true);
				m_in = new BufferedReader(new InputStreamReader(m_socket.getInputStream()));
				
				String inputLine;
				String outputLine;
				
				ProtocolLogic businessLogic = new ProtocolLogic();
				outputLine = businessLogic.processInput(null);
				
				m_out.println(outputLine);
				
				//Read from socket and write back the response to client
				while ((inputLine = m_in.readLine()) != null)
				{
					outputLine = businessLogic.processInput(inputLine);
					if (outputLine != null)
					{
						m_out.println(outputLine);
						if (outputLine.equals("@quit"))
						{
							System.out.println("Server is closing socket for client: " + m_socket.getLocalSocketAddress());
							break;
						}
					}
					else
					{
						System.out.println("outputLine is null!");
					}
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					m_out.close();
					m_in.close();
					m_socket.close();
				}
				catch (Exception e)
				{
					System.out.println("Couldn't close I/O streams");
				}
			}
		}
	}
	
	public static class ProtocolLogic
	{
		private static final int LoginUserName = 0;
		private static final int LoginPassword = 1;
		private static final int AuthenticateUser = 2;
		private static final int AuthSuccess = 3;
		
		private int state = LoginUserName;
		
		private String userName = null;
		private String userPassword = null;
		
		public String processInput(String clientRequest)
		{
			String reply = null;
			try
			{
				if (clientRequest != null && clientRequest.equalsIgnoreCase("login"))
				{
					state = LoginPassword;
				}
				else if (clientRequest != null && clientRequest.equalsIgnoreCase("@quit"))
				{
					return "@quit";
				}
				
				if(state == LoginUserName)
				{
					reply = "Enter username: ";
					state = LoginPassword;
				}
				else if(state == LoginPassword)
				{
					userName = clientRequest;
					reply = "Enter password: ";
					state = AuthenticateUser;
				}
				else if(state == AuthenticateUser)
				{
					userPassword = clientRequest;
					if(userName.equalsIgnoreCase("zesty") && userPassword.equals("123"))
					{
						reply = "Logged in.";
						state = AuthSuccess;
					}
					else
					{
						reply = "Username/password combination not foumd. Try again.";
						state = LoginUserName;
					}
				}
				else
				{
					reply = "Invalid request";
				}
			}
			catch(Exception e)
			{
				System.out.println("Input process failed: " + e.getMessage());
				return "@quit";
			}
			
			return reply;
		}
	}*/

	/** old server-client code */
	/*public static void main(String[] args) throws IOException
	{	
		Logger.clear();
		new Map("maps/test_map.xml", "gfx/standard_tileset.xml");
		
		if (args.length != 1)
		{
			System.err.println("Usage java Server <port number>");
			System.exit(1);
		}
		
		int portNumber = Integer.parseInt(args[0]);
		
		try
		(
			ServerSocket serverSocket = new ServerSocket(portNumber);
			Socket clientSocket = serverSocket.accept();
			PrintWriter out =
					new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));
		)
		{
			String input;
			String output;
			
			//Intiate a conersation with the client
			ServerProtocol sp = new ServerProtocol();
			output = sp.processInput("");
			out.println(out);
			
			while((input = in.readLine()) != null)
			{
				output = sp.processInput(input);
				
				out.println(output);
				if (output.equals("quit"))
				{
					break;
				}
			}
		}
		catch (IOException e) 
		{
			System.out.println("Exception caught when trying to listen on port "
					+ portNumber + " or listening for a connection");
			System.out.println(e.getMessage());
		}
	}
}*/

/*class ServerProtocol
{
	private WorldEntity ent = new WorldEntity("ent/player[0].xml");
	private static Parse m_p = new Parse();
	
	private static final int WAITING_FOR_LOGIN = 0;
	private static final int CONNECTED = 1;
	
	private int state = WAITING_FOR_LOGIN;
	
	public String processInput(String in) 
	{	
		String msg[] = {""};
		String tmp = "";
		
		if (state == WAITING_FOR_LOGIN)
		{
			//TODO: login stuff
			state = CONNECTED;
		}
		else if (state == CONNECTED)
		{
			msg = m_p.parse(ent, in);
		}	
		for (int i = 0; i < msg.length; i++)
		{
			tmp += msg[i] + "#";
		}
		return tmp;
	}
}*/
