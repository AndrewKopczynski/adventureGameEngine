package apk.main.server;

import apk.main.engine.Map;
import apk.main.engine.Parse;
import apk.main.engine.Render_ASCII;
import apk.main.engine.WorldEntity;

//TODO: make proper server/client
//TODO: load map
public class Server 
{
	private WorldEntity player; //TODO: replace with loading entities from map/save files
	private static Parse m_p = new Parse();
	private static String msg[] = new String[1];
	
	public Server()
	{
		new Map("maps/test_map.xml", "gfx/standard_tileset.xml");
		player = new WorldEntity("ent/player[0].xml");
		WorldEntity testEnt = new WorldEntity("ent/testEnt[0].xml");
	}
	
	public String[] input(String in) 
	{	
		if (m_p.parse(player, in))
		{
			return Render_ASCII.renderMap(player.getX(), player.getY(), player.getZ());
		}
		else
		{
			msg[0] = "Test";
			return msg;
		}
		//String noMap[] = new String [1];
		//noMap[0] = "DEBUG: no map received.";
		//return noMap;
		//unused, old 'client/server' code
		/*Scanner scan = new Scanner(System.in);
		String userInput = "";
		
		Render_ASCII.renderMap();*/
		
		/*JFrame frame = new JFrame("Render");
		frame.setSize(300, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setAlwaysOnTop(true);
		frame.getContentPane().add(new Render_Graphical());
		frame.setVisible(true);*/
		
		//unused, old 'client/server' code
		/*while(!userInput.equals("quit"))
		{
			p.parse(userInput = scan.nextLine());
			frame.repaint();
		}*/
		
		//unused, old 'client/server' code
		/*scan.close();
		frame.dispose();*/
	}
	
	public void save()
	{
		// save TODO: better saving system that saves everything
		player.writeSave();
	}
}
