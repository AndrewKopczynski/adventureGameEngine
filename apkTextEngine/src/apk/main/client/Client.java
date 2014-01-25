package apk.main.client;

import java.util.Scanner;

//import apk.main.engine.XMLReader;
//import apk.main.engine.XMLWriter;
//import apk.main.engine.Entity;
import apk.main.server.Server;
//import apk.main.engine.Parse;

public class Client
{
	public static void main(String[] args)
	{
		new Server(); //TODO: actual server stuff
		//Parse p = new Parse(); //TODO: remove after testing
		Scanner scan = new Scanner(System.in);
		String input = "";
		String returned[];
		
		/*String[] invElements = {"entity", "health", "inventory"};
		XMLReader invXML = new XMLReader("ent/test[10].xml", invElements);
		
		String temp = invXML.getAttribute(invElements[0], 0, "id");
		System.out.println("id: " + temp);
		temp = invXML.getAttribute("entity", 0, "name");
		System.out.println("name: " + temp);
		temp = invXML.getAttribute(invElements[1], 0, "hp");
		System.out.println("hp: " + temp);
	
		System.out.println("printing inventory...");
		String ents[] = invXML.getChildren("inventory", 0);
		for (int a = 1; a < ents.length; a += 2)
		{
			System.out.println("id: " + Integer.parseInt(ents[a]));
		}*/
		
		/*XMLWriter r = new XMLWriter("test");
		String[] temp1 = new String[2];
		String[] temp2 = new String[2];
		temp1[0] = "id";
		temp1[1] = "name";
		temp2[0] = "0";
		temp2[1] = "player";
		
		r.writeRootOpenTag("entity", "id", "0");
		r.writeTag("health", "hpMax", "30");
		r.writeRootCloseTag("entity");
		r.close();*/
		
		while (!input.equals("quit"))
		{
			input = scan.nextLine();
			//String[] vnn = input.split(" ");
			//System.out.println(p.collapse(vnn, 2, vnn.length - 1));
			//System.out.println(Integer.parseInt(vnn[vnn.length - 1]));

			returned = Server.input(input);
			for (int i = 0; i < returned.length; i++)
			{
				System.out.println(returned[i]);
			}
		}
		scan.close();
		Server.save();
	}
}
