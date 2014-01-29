package apk.main.engine;

import apk.parser.action.*;
import apk.parser.parameter.*;

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
	private Status m_status = new Status();
	private Tactical m_tactical = new Tactical();
	
	private Direction m_direction = new Direction();
	
	private String[] msg = new String[1]; //TODO replace with something nicer probably
	
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
	public String[] parse(WorldEntity entity, String input)
	{
		String[] vnn = input.split(" ");
		
		/** SHORT MOVEMENT ------------------------------------------------	|*/
		if (m_shortMove.check(vnn)) 
		{
			entity.move(m_shortMove.getMeaning(vnn[0]));
			return Render_ASCII.renderMap(entity.getX(), entity.getY(), entity.getZ());
		} 
		
		/** MOVEMENT ------------------------------------------------------	|*/
		else if (m_move.check(vnn))
		{
			entity.move(m_direction.getMeaning(vnn[1]));
			return Render_ASCII.renderMap(entity.getX(), entity.getY(), entity.getZ());
		}
		
		/** LOOK ----------------------------------------------------------	|*/
		else if (m_look.check(vnn))
		{
			if (vnn.length == 1)
			{
				return Render_ASCII.renderMap(entity.getX(), entity.getY(), entity.getZ());
			}
			else if (m_direction.check(vnn[1]) && vnn.length == 1)
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
				return Render_ASCII.renderMap(entity.getX(), entity.getY(), entity.getZ());
			}
			
			msg[0] = "Look where?";
			return msg; //TODO replace with per-action error fetches
		}
		
		/** TAKE ----------------------------------------------------------	|*/
		else if (m_take.check(vnn))
		{
			msg[0] = "Not implimented, but detected 'take'.";
			return msg;
		}
		
		/** PUT -----------------------------------------------------------	|*/
		else if (m_put.check(vnn))
		{
			msg[0] = "Not implimented, but detected 'put'.";
			return msg;
		}
		
		/** DROP ----------------------------------------------------------	|*/
		else if (m_drop.check(vnn))
		{
			msg[0] = "Not implimented, but detected 'drop'.";
			return msg;
		}
		
		/** ST*ATUS -------------------------------------------------------	|*/
		else if (m_status.check(vnn))
		{
			
			msg[0] = entity.printMeter(entity.getHP(), entity.getHP(), entity.getHPMax(), 30);
			return msg;
		}
		
		/** TAC*TICAL -----------------------------------------------------	|*/
		else if (m_tactical.check(vnn))
		{
			
			msg[0] = entity.printNamedHealthBar();
			return msg;
		}
		
		/** ADMIN STUFF ---------------------------------------------------	|*/
		else if (m_admin.check(vnn))
		{
			/** setRange */
			if ((m_admin.getMeaning(vnn[0]).equals("setVis")))
			{
				try
				{
					Render_ASCII.setRange(Integer.parseInt(vnn[1]));
					msg[0] = "Set visibility range to " + vnn[1];
					return msg;
				}
				
				catch(Exception e)
				{	
					msg[0] = "Usage: " + vnn[0] + " [number]";
					return msg;
				}
			}
			
			/** giveItem 
			 * TODO: give items to any entity, not just the player */
			else if ((m_admin.getMeaning(vnn[0]).equals("add")))
			{
				try
				{
					//setup name, hp (assume full hp)
					String name = collapse(vnn, 1, vnn.length - 1);
					int hpMax = Integer.parseInt(vnn[vnn.length - 1]);
					
					if (entity.addToInventory(new Entity(name, hpMax)))
					{
						msg[0] = "Gave " + name + " to " + entity.toString();
						return msg;
					}
					else
					{
						msg[0] = "Couldn't give " + name + " to " + entity.toString();
						return msg;
					}
				}
				
				catch(Exception e)
				{
					msg[0] = "Usage: " + vnn[0] + " [itemName] [maxHp]";
					return msg;
				}
			}
			/** deleteItem */
			else if ((m_admin.getMeaning(vnn[0]).equals("del")))
			{
				try
				{
					String name = collapse(vnn, 1, vnn.length);
					
					if (entity.delFromInventory(name))
					{
						
						msg[0] = "Deleted " + name + " from " + entity.toString();
						return msg;
					}
					else
					{
						
						msg[0] = "Couldn't delete " + name + " from " + entity.toString();
						return msg;
					}
				}
				catch(Exception e)
				{
					
					msg[0] = "Usage: " + vnn[0] + " [itemName]";
					return msg;
				}
			}
			/** noclip */
			else if ((m_admin.getMeaning(vnn[0]).equals("noclip")))
			{
				try
				{
					entity.ignoresCollision(Boolean.parseBoolean(vnn[1]));

					
					msg[0] = "noclip = " + vnn[1];
					return msg;
				}
				catch(Exception e)
				{
					
					msg[0] = "Usage: " + vnn[0] + " [true/false]";
					return msg;
				}
			}
			/** hurt */
			else if ((m_admin.getMeaning(vnn[0]).equals("hurt")))
			{
				try
				{
					entity.hurt(Integer.parseInt(vnn[1]));
					
					msg[0] = "Inflicted " + vnn[1] + " point(s) of damage to '" 
						+ entity.toString() + "'.";
					return msg;
				}
				catch(Exception e)
				{
					msg[0] = "Usage: " + vnn[0] + " [value]";
					return msg;
				}
			}
			/** heal */
			else if ((m_admin.getMeaning(vnn[0]).equals("heal")))
			{
				try
				{
					entity.heal(Integer.parseInt(vnn[1]));
					
					msg[0] = "Healed " + vnn[1] + " point(s) of damage from '" 
							+ entity.toString() + "'.";
					return msg;
				}
				catch(Exception e)
				{
					msg[0] = "Usage: " + vnn[0] + " [value]";
					return msg;
				}
			}
			/** if invalid */
			else
			{
				msg[0] = "'" + vnn[0] + "' doesn't seem to be a valid command.";
				return msg;
			}
		}
		
		/** QUIT ----------------------------------------------------------	|
		 * Short: quit
		 */
		else if (m_quit.check(vnn))
		{
			
			msg[0] = "Saving and quitting...";
			return msg;
		}
		
		/** NO MATCH ------------------------------------------------------	|
		 */
		else
		{
			
			msg[0] = input + " for entity " + entity + " isn't a valid command.";
			return msg;
		}
	}
}
