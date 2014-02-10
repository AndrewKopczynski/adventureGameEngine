package apk.main.engine;

import java.util.List;

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
	
	private String[] msg = new String[1]; //TODO replace with something nicer probably
	
	public Parse()
	{
		//TODO: Needed or refactor?
		//Tempt message in the meanwhile
		Logger.log("Loading parsers and wordlists...");
		System.out.println("Loading parser...");
	}
	
	/** Collapses a string array from a to b in a String array. */
	private String collapse(String[] input, int a, int b)
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
	
	private String[] collapse(String[] input, int a, int b, String seperator)
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
	public String[] parse(Actor actor, String input)
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
			String temp = actor.move(m_shortMove.getMeaning(att[0]));
			msg = Render_ASCII.renderMap(actor.getX(), actor.getY(), actor.getZ());
			
			String[] arr = new String[msg.length + 1];
			System.arraycopy(msg, 0, arr, 0, msg.length);
			
			msg = arr;
			msg[msg.length - 1] = temp;
			
			return msg;
		} 
		
		/** MOVEMENT ------------------------------------------------------	|*/
		else if (m_move.check(att))
		{
			if (att.length == 2 && m_direction.check(att[att.length - 1]))
			{
				String temp = actor.move(m_direction.getMeaning(att[1]));
				msg = Render_ASCII.renderMap(actor.getX(), actor.getY(), actor.getZ());
				
				String[] arr = new String[msg.length + 1];
				System.arraycopy(msg, 0, arr, 0, msg.length);
				
				msg = arr;
				msg[msg.length - 1] = temp;
			}
			else
			{
				msg[0] = actor.move(m_direction.getMeaning(att[att.length - 1]));
			}
			return msg;
		}
		
		/** LOOK ----------------------------------------------------------	|*/
		else if (m_look.check(att))
		{
			if (att.length == 1)
			{
				int x = actor.getX();
				int y = actor.getY();
				int z = actor.getZ();
				String temp = "";
				
				Actor[] actors = Actor.getActors(actor, x, y, z); //gets all actors besides this one
				
				msg = Render_ASCII.renderMap(x, y, z);
				
				String[] arr = new String[msg.length + actors.length];
				System.arraycopy(msg, 0, arr, 0, msg.length);
				
				//System.out.println(arr.length);
				//System.out.println(actors.length);
				
				msg = arr;
				
				System.out.println("got " + actors.length + " actors at " + actor.getXYZ());
				for (int i = 0; i < actors.length; i++)
				{
					//if (actors[i] != null)
					//{
						System.out.println(actors[i]);
					//}
				}
				
				if (actors.length > 0) //remember: did not include this actor in actors
					temp = "You see ";
				else
					return msg;
				
				//give up for now, too complicated :(
				//figure something else out.
				for (int i = 0; i < actors.length; i++)
				{
					System.out.println(actors[i].getName());
					temp += actors[i].getName();
					
					if (actors.length - 1 - i == 1)
					{
						System.out.println("2");
						temp += " and ";
					}
					else if (actors.length - 1 - i > 1)
					{
						System.out.println("3");
						temp += ", ";
					}
					else if (actors.length - 1 - i == 0)
					{
						System.out.println("4");
					}
				}
				
				if (temp.length() > 0)
					 temp += " standing here.";
				
				msg[msg.length - 1] = temp;
				
				return msg;
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
				return Render_ASCII.renderMap(actor.getX(), actor.getY(), actor.getZ());
			}
			
			msg[0] = "Look where?";
			return msg; //TODO replace with per-action error fetches
		}
		/** SAY -----------------------------------------------------------	|*/
		else if (m_say.check(att))
		{
			msg[0] = actor.getName() + " says: \"" + collapse(att, 1, att.length) + "\"";
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
			msg[0] = actor.printMeter(actor.getHP(), actor.getHP(), actor.getHPMax(), 30);
			return msg;
		}
		
		/** TAC*TICAL -----------------------------------------------------	|*/
		else if (m_tactical.check(att))
		{
			msg[0] = actor.printNamedHealthBar();
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
					int hp = Integer.parseInt(att[att.length - 1]);
					
					if (actor.addToInventory(new Entity(name, hp, hp)))
					{
						msg[0] = "Gave " + name + " to " + actor.toString();
						return msg;
					}
					else
					{
						msg[0] = "Couldn't give " + name + " to " + actor.toString();
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
					
					if (actor.delFromInventory(name))
					{
						
						msg[0] = "Deleted " + name + " from " + actor.toString();
						return msg;
					}
					else
					{
						
						msg[0] = "Couldn't delete " + name + " from " + actor.toString();
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
					actor.ignoresCollision(Boolean.parseBoolean(att[1]));

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
					actor.hurt(Integer.parseInt(att[1]));
					
					msg[0] = "Inflicted " + att[1] + " point(s) of damage to '" 
						+ actor.toString() + "'.";
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
					actor.heal(Integer.parseInt(att[1]));
					
					msg[0] = "Healed " + att[1] + " point(s) of damage from '" 
							+ actor.toString() + "'.";
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
				System.out.println("ENTITY:");
				Entity.debug();
				System.out.println("ACTOR:");
				Actor.debug();
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
			
			msg[0] = input + " for entity " + actor + " isn't a valid command.";
			return msg;
		}
	}
}
