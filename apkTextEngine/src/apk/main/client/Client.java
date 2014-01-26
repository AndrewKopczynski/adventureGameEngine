package apk.main.client;

import java.util.Scanner;
import apk.main.server.Server;

public class Client
{
	public static void main(String[] args)
	{
		Server myServer = new Server();

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
		myServer.save();
	}
}
