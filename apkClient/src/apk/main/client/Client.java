package apk.main.client;

import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable
{

	// The client socket
	private static Socket clientSocket = null;

	// The output stream
	private static PrintStream os = null;

	// The input stream
	//private static DataInputStream is = null;
	
	//z - new input stream because some of DataInputStream's methods are deprecated
	private static BufferedReader m_in = null;

	private static BufferedReader inputLine = null;
	private static boolean closed = false;

	public static void main(String[] args)
	{
		// The default port.
		int portNumber = 2222;
		// The default host.
		String host = "localhost";

		if (args.length < 2)
		{
			System.out.println("Usage: java MultiThreadChatClient <host> <portNumber>\n"
				+ "Now using host=" + host + ", portNumber=" + portNumber);
		}
		else
		{
			host = args[0];
			portNumber = Integer.valueOf(args[1]).intValue();
		}

		/*
		* Open a socket on a given host and port. Open input and output streams.
		*/
		try
		{
			clientSocket = new Socket(host, portNumber);
			inputLine = new BufferedReader(new InputStreamReader(System.in));
			os = new PrintStream(clientSocket.getOutputStream());
			m_in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); //z - changed from DataStream to BufferedReader
		}
		catch (UnknownHostException e)
		{
			System.err.println("Don't know about host " + host);
		}
		catch (IOException e)
		{
			System.err.println("Couldn't get I/O for the connection to the host "+ host);
		}

	
		/* If everything has been initialized then we want to write some data to the
		* socket we have opened a connection to on the port portNumber.
		*/
		if (clientSocket != null && os != null && m_in != null)
		{
			try
			{
	
				/* Create a thread to read from the server. */
				new Thread(new Client()).start();
				while (!closed)
				{
					os.println(inputLine.readLine().trim());
				}
				
				/* Close the output stream, close the input stream, close the socket. */
				os.close();
				m_in.close(); //z
				clientSocket.close();
			}
			catch (IOException e)
			{
				System.err.println("IOException:  " + e);
			}
		}
	}

	/*
	* Create a thread to read from the server. (non-Javadoc)
	* 
	* @see java.lang.Runnable#run()
	*/
	public void run()
	{
		/*
		* Keep on reading from the socket till we receive "Bye" from the
		* server. Once we received that then we want to break.
		*/
		String responseLine;
		try
		{
		while ((responseLine = m_in.readLine()) != null) //z
		{
			System.out.println(responseLine);
			if (responseLine.indexOf("*** Bye") != -1)
			break;
		}
		closed = true;
		}
		catch (IOException e)
		{
			System.err.println("IOException:  " + e);
		}
	}
}


/** old client */
/*public class Client
{
	public static void main(String[] args) throws IOException
	{
		if (args.length != 2)
		{
			System.err.println(
					"Usage: java EchoClient <host name> <port number>");
			System.exit(1);
		}
		
		Socket clientSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		BufferedReader stdIn = null;
		
		//get hostname and port from commandLine args
		String hostName = args[0];
		int portNumber = Integer.parseInt(args[1]);
		
		try
		{
			//attempt to connect to server
			clientSocket = new Socket(hostName, portNumber);
			
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			stdIn = new BufferedReader(new InputStreamReader(System.in));
			
			String[] fromServer;
			String fromUser;
				
			while ((fromServer = in.readLine().split("#")) != null)
			{
				//print stuff recieved from server
				for (int i = 0; i < fromServer.length; i++)
				{
					System.out.println("Server: " + fromServer[i]);
				}
				
				if (fromServer[0].equals("@quit"))
					break;
				
				fromUser = stdIn.readLine();
				if (fromUser != null)
				{
					System.out.println("Client: " + fromUser);
					out.println(fromUser);
				}
			}
		}
		catch (UnknownHostException e)
		{
			System.err.println("Cannot find the host: " + hostName);
			System.exit(1);
			
		}
		catch (IOException e)
		{
			System.err.println("Couldn't get I/O for the connection to "
					+ hostName);
			System.exit(1);
		}
		finally
		{
			out.close();
			in.close();
			stdIn.close();
			clientSocket.close();
		}
	}
}*/
