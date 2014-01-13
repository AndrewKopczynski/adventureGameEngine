package apk.main.engine;

import java.util.Scanner;

import javax.swing.JFrame;

public class Client 
{
	public static Entity player;
	public static Map map;
	
	public static void main(String[] args) 
	{	
		// clear log
		Logger.clear();
		
		/** Load parser */
		Parse p = new Parse();
		
		/** Load map */
		map = new Map("maps/test_map.xml", "gfx/standard_tileset.xml");
		
		/** Create player
		 * TODO: save player in room inventory & load player instead
		 */
		player = new Entity(0, 0, 0, "player");
		player.m_inv.print();
		
		/** Input preparation. */
		Scanner scan = new Scanner(System.in);
		String userInput = "";
		
		Render_ASCII.renderMap();
		//old code
		//map.renderMap();
		
		JFrame frame = new JFrame("Render");
		frame.setSize(300, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setAlwaysOnTop(true);
		frame.getContentPane().add(new Render_Graphical());
		frame.setVisible(true);
		
		/** Loop for inputs to parse. */
		while(!userInput.equals("quit"))
		{
			p.parse(userInput = scan.nextLine());
			frame.repaint();
		}
		
		// save TODO: better saving system that saves errythang
		player.m_inv.save();
		scan.close();
		frame.dispose();
	}
}
