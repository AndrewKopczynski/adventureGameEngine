package apk.main.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.Timer;

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
	
	private static String buffer; //z
	private static final int TIMER_SPEED = 2;
	private static ActionListener taskPerformer = new ActionListener() //z
	{
		@Override
		public void actionPerformed(ActionEvent evt)
		{
			try
			{
				//System.out.println("tick");
				if (buffer.length() > 0)
				{
					String t = buffer.substring(0, 1);
					
					if (t.equals("#"))
						System.out.println();
					else
						System.out.print(t);
					buffer = buffer.substring(1, buffer.length());
				}
				else
				{
					System.out.println();
					timer.stop();
				}
			}
			catch (NullPointerException e)
			{
				System.err.println("Timer stopped abrubtly: " + e);
				timer.stop();
				return;
			}
		}
	};
	
	private static Timer timer = new Timer(TIMER_SPEED, taskPerformer);

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

		/* Open a socket on a given host and port. Open input and output streams.*/
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
		try
		{
			while ((buffer = m_in.readLine()) != null) //z
			{
				if (buffer.indexOf("Logging off...") != -1)
				{
					System.out.println("Disconnected.");
					break;
				}
				else
				{
					System.out.println(buffer.length());
					//buffer = responseLine;
					timer.start();
				}
				
				//System.out.println(responseLine);
			}
			closed = true;
		}
		catch (IOException e)
		{
			System.err.println("IOException:  " + e);
		}
	}
}
