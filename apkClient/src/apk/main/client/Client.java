package apk.main.client;

import java.io.*;
import java.net.*;

public class Client
{
	public static void main(String[] args) throws IOException
	{
		if (args.length != 2)
		{
			System.err.println(
					"Usage: java EchoClient <host name> <port number>");
			System.exit(1);
		}
		
		String hostName = args[0];
		int portNumber = Integer.parseInt(args[1]);
		
		try (
			Socket clientSocket = new Socket(hostName, portNumber);
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			
			BufferedReader in = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));
			)
			{
				BufferedReader stdIn =
					new BufferedReader(new InputStreamReader(System.in));
				String[] fromServer;
				String fromUser;
				
				while ((fromServer = in.readLine().split("#")) != null)
				//while (fromServer == null)
				{
					//System.out.println("Server: " + fromServer);
					for (int i = 0; i < fromServer.length; i++)
					{
						//System.out.println("Recieved from server after [" + i + "] loops");
						System.out.println("Server: " + fromServer[i]);
					}
					
					if (fromServer[0].equals("quit"))
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
			System.err.println("Don't know about host " + hostName);
			System.exit(1);
			
		}
		catch (IOException e)
		{
			System.err.println("Couldn't get I/O for the connection to "
					+ hostName);
			System.exit(1);
		}
			
		/*Server myServer = new Server();

		Scanner scan = new Scanner(System.in);
		String input = "";
		String returned[];
		
		while (!input.equals("quit"))
		{
			input = scan.nextLine();
			
			returned = myServer.input(input);
			for (int i = 0; i < returned.length; i++)
			{
				System.out.println(returned[i]);
			}
		}
		scan.close();
		myServer.save();*/
	}
}
