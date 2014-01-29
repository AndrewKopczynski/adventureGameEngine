package apk.main.server;

import java.util.Scanner;

import apk.main.engine.Logger;
import apk.main.engine.Map;
import apk.main.engine.Parse;
import apk.main.engine.WorldEntity;

//TODO: make proper server/client
public class Server 
{
	private WorldEntity ent;
	private static Parse m_p;
	
	public Server()
	{
		Logger.clear();
		new Map("maps/test_map.xml", "gfx/standard_tileset.xml");
		
		Scanner scan = new Scanner(System.in);
		System.out.println("DEBUG: login: ");
		
		String temp = scan.nextLine(); //TODO: some sort of database like thing
		
		if (temp.length() > 0)
			ent = new WorldEntity("ent/" + temp + ".xml");
		else //default
			ent = new WorldEntity("ent/player[0].xml");
		
		m_p = new Parse();
	}
	
	public String[] input(String in) 
	{	
		return m_p.parse(ent, in);
	}
	
	public void save()
	{
		// save TODO: better saving system that saves everything
		ent.writeSave();
	}
}
