package apk.main.engine;

import apk.parser.noun.*;
import apk.parser.verb.*;

public class Parse {
	
	// TODO: Is loading everything for a 'server' side a concern?
	// Does slow loading for a server start up matter?
	
	private ShortMove m_shortMove = new ShortMove();
	private Move m_move = new Move();
	private Look m_look = new Look();
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
	
	/** Collapses a string array from a to b in a String array. */
	public String collapse(String[] input, int a, int b)
	{
		String temp = "";
		for (; a < b; a++)
		{
			temp += input[a];
			if (a + 1 < b)
			{
				temp += " ";
			}
		}
		return temp;
	}

	/** Tests input and executes actions.
	 * 
	 * @param input Input phrase to test
	 * @return True if should return map, false if not.
	 */
	public boolean parse(Entity entity, String input)
	{
		String[] vnn = input.split(" ");
		
		/** SHORT MOVEMENT ------------------------------------------------	|
		 * Short: north
		 */
		if (vnn.length == 1 
				&& m_shortMove.contains(vnn[0])) 
		{
			entity.move(m_shortMove.getMeaning(vnn[0]));
			return true;
		} 
		
		/** MOVEMENT ------------------------------------------------------	|
		 * Long: go north
		 */
		else if (vnn.length == 2 
				&& m_move.contains(vnn[0]))
		{
			entity.move(m_direction.getMeaning(vnn[1]));
			return true;
		}
		
		/** LOOK ----------------------------------------------------------	|
		 * Short: look
		 * Mixed: look [item] or look [place] //TODO: look item support
		 * Long: look at [item] or look to [direction] //TODO: implement
		 */
		else if (m_look.contains(vnn[0]))
		{
			if (vnn.length == 1)
			{
				return true;
			}
			else if (vnn.length == 2 && m_direction.contains(vnn[1]))
			{
				//TODO: clean this and entity code up, rough pass for now
				int n = 0;
				int e = 0;
				int s = 0;
				int w = 0;
				int u = 0;
				int d = 0;
				if (m_direction.getMeaning(vnn[1]).equals("n"))
				{
					n = 1;
				}
				else if (m_direction.getMeaning(vnn[1]).equals("ne"))
				{
					n = 1;
					e = 1;
				}
				else if (m_direction.getMeaning(vnn[1]).equals("e"))
				{
					e = 1;
				}
				else if (m_direction.getMeaning(vnn[1]).equals("se"))
				{
					s = 1;
					e = 1;
				}
				else if (m_direction.getMeaning(vnn[1]).equals("s"))
				{
					s = 1;
				}
				else if (m_direction.getMeaning(vnn[1]).equals("sw"))
				{
					s = 1;
					w = 1;
				}
				else if (m_direction.getMeaning(vnn[1]).equals("w"))
				{
					w = 1;
				}
				else if (m_direction.getMeaning(vnn[1]).equals("nw"))
				{
					n = 1;
					w = 1;
				}
				else if (m_direction.getMeaning(vnn[1]).equals("u"))
				{
					u = 1;
				}
				else if (m_direction.getMeaning(vnn[1]).equals("d"))
				{
					d = 1;
				}
				//TODO: lazy, refactor this later
				Render_ASCII.setxOffset(e - w);
				Render_ASCII.setyOffset(s - n);
				Render_ASCII.setzOffset(u - d);
				return true;
			}
			return false;
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
			return false;
		}
		
		/** PUT -----------------------------------------------------------	|
		 * Short: put item crate
		 * Long: put huge sword in large crate
		 */
		else if ((vnn.length == 3 || vnn.length == 4) 
				&& m_put.contains(vnn[0]))
		{
			System.out.println("Not implimented, but detected 'put'.");
			return false;
		}
		/** DROP ----------------------------------------------------------	|
		 * Short: drop item
		 */
		else if (vnn.length == 2 && m_drop.contains(vnn[0]))
		{
			System.out.println("Not implimented, but detected 'drop'.");
			return false;
		}
		/*else if (vnn.length == 3 && m_drop.contains(vnn[0]))
		{
			
		}*/
		
		/** ADMIN STUFF ---------------------------------------------------	|
		 * General: admin [command] [parameter]
		 */
		else if (m_admin.contains(vnn[0]))
		{
			/** setRange */
			if (vnn[1].equals("setRange"))
			{
				try
				{
					Render_ASCII.setRange(Integer.parseInt(vnn[2]));
					System.out.println("Set visibility range to " + vnn[2]);
					return true;
				}
				
				catch(Exception e)
				{
					System.out.println("Usage: admin range [number]");
					return false;
				}
			}
			
			/** giveItem 
			 * TODO: give items to any entity, not just the player */
			else if (vnn[1].equals("give"))
			{
				try
				{
					entity.getInventory().add(
							new Entity(collapse(vnn, 2, vnn.length - 1),
							Integer.parseInt(vnn[vnn.length - 1])));
					
					System.out.println("Gave " + vnn[2] + " to player.");
					return false;
				}
				
				catch(Exception e)
				{
					System.out.println("Usage: admin give [item] [hp]");
					return false;
				}
			}
			/** removeItem */
			else if (vnn[1].equals("remove"))
			{
				try
				{
					entity.getInventory().remove(collapse(vnn, 2, vnn.length - 2));
					System.out.println("Removed " + vnn[2] + " from player.");
					return false;
				}
				
				catch(Exception e)
				{
					System.out.println("Usage: admin give [item]");
					return false;
				}
			}
			
			else if (vnn[1].equals("noclip"))
			{
				try
				{
					entity.ignoresCollision(Boolean.parseBoolean(vnn[2]));
					return false;
				}
				
				catch(Exception e)
				{
					System.out.println("Usage: admin noclip [true/false]");
					return false;
				}
			}
			
			else
			{
				System.out.println("Usage: admin [command] [parameter(s)]");
				return false;
			}
		}
		
		/** QUIT ----------------------------------------------------------	|
		 * Short: quit
		 */
		else if (vnn.length == 1
				&& m_quit.getMeaning(vnn[0]).equalsIgnoreCase("quit"))
		{
			System.out.println("Saving and quitting...");
			return false;
		}
		
		/** NO MATCH ------------------------------------------------------	|
		 */
		else
		{
			System.out.println(input + " for entity " 
				+ entity + " isn't a valid command.");
			return false;
		}
	}
}
