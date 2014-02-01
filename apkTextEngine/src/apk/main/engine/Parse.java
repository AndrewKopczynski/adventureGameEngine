package apk.main.engine;

import apk.parser.action.*;
import apk.parser.target.*;

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
	private Say m_say = new Say();
	
	private Direction m_direction = new Direction();
	
	private String[] msg; //TODO replace with something nicer probably
	
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
	
	public String[] collapse(String[] input, int a, int b, String seperator)
	{
		int i = 0; //i = 0 first target //i = 1 second target
		String t1 = "";
		String t2 = "";
		
		for(; a < b; a++)
		{
			if (t1.length() > 0 && input[a].equals(seperator) && i < 1)
			{
				i++;
			}
			else
			{
				if (i == 0)
					t1 += input[a];
				else
					t2 += input[a];
				if (a + 1 < b)
				{
					if (i == 0 && (a + 1) < b)
						t1 += " ";
					else if (i == 1)
						t2 += " ";
				}
			}
		}
		String[] temp = {t1, t2};
		return temp;
	}

	/** Tests input and executes actions.
	 * 
	 * @param input Input phrase to test
	 * @return True if should return map, false if not.
	 */
	public String[] parse(WorldEntity entity, String input)
	{
		//clear msg;
		
		msg = new String[1];
		
		//att - [action] [target] [target]
		//eg. [look] [e]
		//eg. [take] [item] from [box]
		String[] att = input.split(" ");
		
		/** SHORT MOVEMENT ------------------------------------------------	|*/
		if (m_shortMove.check(att)) 
		{
			String temp = entity.move(m_shortMove.getMeaning(att[0]));
			msg = Render_ASCII.renderMap(entity.getX(), entity.getY(), entity.getZ());
			
			String[] arr = new String[msg.length + 1];
			System.arraycopy(msg, 0, arr, 0, msg.length);
			
			msg = arr;
			msg[msg.length - 1] = temp;
			
			return msg;
		} 
		
		/** MOVEMENT ------------------------------------------------------	|*/
		else if (m_move.check(att))
		{
			if (m_direction.check(att[att.length - 1]))
			{
				String temp = entity.move(m_direction.getMeaning(att[1]));
				msg = Render_ASCII.renderMap(entity.getX(), entity.getY(), entity.getZ());
				
				String[] arr = new String[msg.length + 1];
				System.arraycopy(msg, 0, arr, 0, msg.length);
				
				msg = arr;
				msg[msg.length - 1] = temp;
			}
			return msg;
		}
		
		/** LOOK ----------------------------------------------------------	|*/
		else if (m_look.check(att))
		{
			if (att.length == 1)
			{
				return Render_ASCII.renderMap(entity.getX(), entity.getY(), entity.getZ());
			}
			else if (m_direction.check(att[1]) && att.length == 2)
			{
				//TODO: clean this and entity code up, rough pass for now
				int n = 0;
				int e = 0;
				int s = 0;
				int w = 0;
				int u = 0;
				int d = 0;
				
				if (m_direction.getMeaning(att[1]).equals("n"))
				{
					n = 1;
				}
				else if (m_direction.getMeaning(att[1]).equals("ne"))
				{
					n = 1;
					e = 1;
				}
				else if (m_direction.getMeaning(att[1]).equals("e"))
				{
					e = 1;
				}
				else if (m_direction.getMeaning(att[1]).equals("se"))
				{
					s = 1;
					e = 1;
				}
				else if (m_direction.getMeaning(att[1]).equals("s"))
				{
					s = 1;
				}
				else if (m_direction.getMeaning(att[1]).equals("sw"))
				{
					s = 1;
					w = 1;
				}
				else if (m_direction.getMeaning(att[1]).equals("w"))
				{
					w = 1;
				}
				else if (m_direction.getMeaning(att[1]).equals("nw"))
				{
					n = 1;
					w = 1;
				}
				else if (m_direction.getMeaning(att[1]).equals("u"))
				{
					u = 1;
				}
				else if (m_direction.getMeaning(att[1]).equals("d"))
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
		/** SAY -----------------------------------------------------------	|*/
		else if (m_say.check(att))
		{
			msg[0] = entity.getName() + " says: \"" + collapse(att, 1, att.length) + "\"";
			return msg;
		}
		
		/** TAKE ----------------------------------------------------------	|*/
		else if (m_take.check(att))
		{
			if (!m_take.check(att))
				msg[0] = "detected 'take " + collapse(att, 1, att.length) + "'.";
			else if (m_take.check(att))
			{
				String[] tmp = collapse(att, 1, att.length, m_take.getMeaning("mod"));
				msg[0] = "detected 'take " + tmp[0] + tmp[1] + "'.";
				System.out.println(tmp[0] + " l: " + tmp[0].length());
				System.out.println(tmp[1] + " l: " + tmp[1].length());
			}
			return msg;
		}
		
		/** PUT -----------------------------------------------------------	|*/
		else if (m_put.check(att))
		{
			msg[0] = "Not implimented, but detected 'put'.";
			return msg;
		}
		
		/** DROP ----------------------------------------------------------	|*/
		else if (m_drop.check(att))
		{
			msg[0] = "Not implimented, but detected 'drop'.";
			return msg;
		}
		
		/** ST*ATUS -------------------------------------------------------	|*/
		else if (m_status.check(att))
		{
			msg[0] = entity.printMeter(entity.getHP(), entity.getHP(), entity.getHPMax(), 30);
			return msg;
		}
		
		/** TAC*TICAL -----------------------------------------------------	|*/
		else if (m_tactical.check(att))
		{
			msg[0] = entity.printNamedHealthBar();
			return msg;
		}
		
		/** ADMIN STUFF ---------------------------------------------------	|*/
		else if (m_admin.check(att))
		{
			/** setRange */
			if (m_admin.getMeaning(att[0]).equals("setVis"))
			{
				try
				{
					Render_ASCII.setRange(Integer.parseInt(att[1]));
					msg[0] = "Set visibility range to " + att[1];
					return msg;
				}
				
				catch(Exception e)
				{	
					msg[0] = "Usage: " + att[0] + " [number]";
					return msg;
				}
			}
			
			/** giveItem 
			 * TODO: give items to any entity, not just the player */
			else if (m_admin.getMeaning(att[0]).equals("add"))
			{
				try
				{
					//setup name, hp (assume full hp)
					String name = collapse(att, 1, att.length - 1);
					int hpMax = Integer.parseInt(att[att.length - 1]);
					
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
					msg[0] = "Usage: " + att[0] + " [itemName] [maxHp]";
					return msg;
				}
			}
			/** deleteItem */
			else if (m_admin.getMeaning(att[0]).equals("del"))
			{
				try
				{
					String name = collapse(att, 1, att.length);
					
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
					
					msg[0] = "Usage: " + att[0] + " [itemName]";
					return msg;
				}
			}
			/** noclip */
			else if (m_admin.getMeaning(att[0]).equals("noclip"))
			{
				try
				{
					entity.ignoresCollision(Boolean.parseBoolean(att[1]));

					msg[0] = "noclip = " + att[1];
					return msg;
				}
				catch(Exception e)
				{
					msg[0] = "Usage: " + att[0] + " [true/false]";
					return msg;
				}
			}
			/** hurt */
			else if (m_admin.getMeaning(att[0]).equals("hurt"))
			{
				try
				{
					entity.hurt(Integer.parseInt(att[1]));
					
					msg[0] = "Inflicted " + att[1] + " point(s) of damage to '" 
						+ entity.toString() + "'.";
					return msg;
				}
				catch(Exception e)
				{
					msg[0] = "Usage: " + att[0] + " [value]";
					return msg;
				}
			}
			/** heal */
			else if (m_admin.getMeaning(att[0]).equals("heal"))
			{
				try
				{
					entity.heal(Integer.parseInt(att[1]));
					
					msg[0] = "Healed " + att[1] + " point(s) of damage from '" 
							+ entity.toString() + "'.";
					return msg;
				}
				catch(Exception e)
				{
					msg[0] = "Usage: " + att[0] + " [value]";
					return msg;
				}
			}
			/** debug */
			else if (m_admin.getMeaning(att[0]).equals("debug"))
			{
				Entity.debug();
				msg[0] = "Printed debug.";
				return msg;
			}
			/** if invalid */
			else
			{
				msg[0] = "'" + att[0] + "' doesn't seem to be a valid command.";
				return msg;
			}
		}
		
		/** QUIT ----------------------------------------------------------	|
		 * Short: quit
		 */
		else if (m_quit.check(att))
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
