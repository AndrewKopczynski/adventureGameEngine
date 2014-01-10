package src;

import parser.noun.*;
import parser.verb.*;

public class Parse {
	
	// TODO: Is loading everything for a 'server' side a concern?
	// Does slow loading for a server start up matter?
	
	private ShortMove m_shortMove = new ShortMove();
	private Move m_move = new Move();
	private Take m_take = new Take();
	private Put m_put = new Put();
	private Drop m_drop = new Drop();
	private Quit m_quit = new Quit();
	private Admin m_admin = new Admin();
	
	private Direction m_direction = new Direction();
	
	public Parse()
	{
		//TODO: Needed or refactor?
		//Tempt message in the meanwhile
		Logger.log("Loading parsers and wordlists...");
		System.out.println("Loading parser...");
	}
	
	public String[] collapse(String input)
	{
		String[] vnn = input.split(" ");
		return vnn;
	}

	public void parse(String input)
	{
		String[] vnn = input.split(" ");
		
		/** SHORT MOVEMENT ------------------------------------------------	|
		 * Short: north
		 */
		if (vnn.length == 1 && m_shortMove.contains(vnn[0])) 
		{
			Client.player.move(m_shortMove.getMeaning(vnn[0]));
			
		} 
		
		/** MOVEMENT ------------------------------------------------------	|
		 * Long: go north
		 */
		else if (vnn.length == 2 && m_move.contains(vnn[0]))
		{
			Client.player.move(m_direction.getMeaning(vnn[1]));
		}
		
		/** TAKE ----------------------------------------------------------	|
		 * Short: get item
		 * Mixed: get item crate
		 * Long: get item from crate
		 */
		else if ((vnn.length == 2 || vnn.length == 3 || vnn.length == 4)
				&& m_take.contains(vnn[0]))
		{
			System.out.println("Not implimented, but detected 'take'.");
		}
		
		/** PUT -----------------------------------------------------------	|
		 * Short: put item crate
		 * Long: put huge sword in large crate
		 */
		else if ((vnn.length == 3 || vnn.length == 4) && m_put.contains(vnn[0]))
		{
			System.out.println("Not implimented, but detected 'put'.");
		}
		// short drop - eg. drop item
		else if (vnn.length == 2 && m_drop.contains(vnn[0]))
		{
			System.out.println("Not implimented, but detected 'drop'.");
		}
		else if (vnn.length == 3 && m_drop.contains(vnn[0]))
		{
			
		}
		
		/** ADMIN STUFF ---------------------------------------------------	|
		 * 
		 */
		else if ((vnn.length == 2 || vnn.length == 3) && m_admin.contains(vnn[0]))
		{
			/** setRange */
			if (vnn[1].equals("setRange"))
			{
				try
				{
					src.Render_ASCII.setRange(Integer.parseInt(vnn[2]));
					System.out.println("Set visibility range to " + vnn[2]);
				}
				
				catch(Exception e)
				{
					System.out.println("Usage: admin range [number]");
				}
			}
			
			/** giveItem 
			 * TODO: give items to any entity, not just the player */
			else if (vnn[1].equals("give"))
			{
				try
				{
					Client.player.m_inv.add(new Item(vnn[2]));
					System.out.println("Gave " + vnn[2] + " to player.");
				}
				
				catch(Exception e)
				{
					System.out.println("Usage: admin give [item]");
				}
			}
			/** removeItem */
			else if (vnn[1].equals("remove"))
			{
				try
				{
					Client.player.m_inv.remove(vnn[2]);
					System.out.println("Removed " + vnn[2] + " from player.");
				}
				
				catch(Exception e)
				{
					System.out.println("Usage: admin give [item]");
				}
			}
			
			else if (vnn[1].equals("noclip"))
			{
				try
				{
					Client.player.ignoresCollision(Boolean.parseBoolean(vnn[2]));
				}
				
				catch(Exception e)
				{
					System.out.println("Usage: admin noclip [true/false]");
				}
			}
			
			else
			{
				System.out.println("Usage: admin [command] [parameter(s)]");
			}
			
			
			
		}
		
		/** QUIT ----------------------------------------------------------	|
		 * Short: quit
		 */
		else if (vnn.length == 1
				&& m_quit.getMeaning(vnn[0]).equalsIgnoreCase("quit"))
		{
			//TODO: actual saving and quitting
			System.out.println("Saving and quitting...");
		}
		
		/** NO MATCH ------------------------------------------------------	|
		 */
		else
		{
			System.out.println("'" + vnn[0] + "' isn't a valid command..");
		}
	}
}
