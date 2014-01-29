package apk.main.server;

import java.util.Scanner;

import apk.main.engine.Logger;
import apk.main.engine.Map;
import apk.main.engine.Parse;
import apk.main.engine.WorldEntity;

import java.net.*;
import java.io.*;

//TODO: make proper server/client
public class Server 
{
	
	
	/*public Server()
	{
		Logger.clear();
		new Map("maps/test_map.xml", "gfx/standard_tileset.xml");
		
		//Scanner scan = new Scanner(System.in);
		//System.out.println("DEBUG: login: ");
		
		//String temp = scan.nextLine(); //TODO: some sort of database like thing
		
		if (temp.length() > 0)
			ent = new WorldEntity("ent/" + temp + ".xml");
		else //default
			ent = new WorldEntity("ent/player[0].xml");
	}*/
	
	/** attempted server-client artcutecture! */
	public static void main(String[] args) throws IOException
	{
		//String[] tmp = in.split(" ");
		//return m_p.collapse(tmp, 1, tmp.length, "in");
		
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
}

class ServerProtocol
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
}
