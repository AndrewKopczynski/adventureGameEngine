package apk.main.engine;

import static apk.main.engine.Logger.logDebug;
import static apk.reference.EntType.*;
import apk.parser.action.*;
import apk.parser.target.*;
import apk.reference.MessageType;

public class Parse {
	
	// TODO: Is loading everything for a 'server' side a concern?
	// Does slow loading for a server start up matter?
	
	// TODO: parse does too much, needs refactor
	
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
	private Inventory m_inventory = new Inventory();
	
	private Direction m_direction = new Direction();
	
	private String[] msg; //TODO replace with something nicer probably
	
	public Parse()
	{
		Logger.log("Loading parsers and wordlists...");
		logDebug("Loading parser...");
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
		int target = 0; //i = 0 first target //i = 1 second target
		String t1 = "";
		String t2 = "";
		
		while(a < b)
		{
			if (t1.length() > 0 && input[a].equals(seperator) && target < 1)
			{
				target++;
			}
			else
			{
				if (target == 0)
					t1 += input[a];
				else
					t2 += input[a];
				
				if ((a + 1) < b)
				{
					if ((target == 0))
						t1 += " ";
					else if (target == 1)
						t2 += " ";
				}
			}
			a++;
		}
		String[] temp = {t1.trim(), t2.trim()};
		return temp;
	}

	/** Tests input and executes actions.
	 * 
	 * @param input Input phrase to test
	 * @return True if should return map, false if not.
	 */
	public String[] parse(Actor actor, String input) //TODO: make more brief
	{	
		msg = new String[1];
		
		//att - [action] [target] [target]
		//eg. [look] [e]
		//eg. [take] [item] from [box]
		String[] att = input.split(" ");
		
		/** MOVEMENT - SHORT MOVE MERGED WITH MOVE ------------------------	|*/
		if (m_direction.check(att) || m_move.check(att)) //check should stop bad movements
		{
			String target;
			int[] vel; // requested direction to move actor
			
			if (att.length == 1)
			{
				vel = m_direction.parse(att[0]);
				target = att[0];
			}
			else if (att.length == 2)
			{
				vel = m_direction.parse(att[1]);
				target = att[1];
			}
			else
				return m_move.getError();
			
			if (vel != null)
			{
				msg = actor.requestMap(vel, true);
				msg[msg.length - 1] = m_look.getActorsInRoom(actor, null);
				
				//done!
				return MessageType.personal(actor, "MOVE", target, msg);
			}
			return m_move.getError();
		}
		
		/** LOOK ----------------------------------------------------------	|*/
		else if (m_look.check(att))
		{
			int vel[] = null;
			
			if (att.length == 2)
			{
				if ((vel = m_direction.parse(att[1])) == null)
					return m_look.getError();
			}
			
			msg = actor.requestMap(vel, false);
			msg[msg.length - 1] = m_look.getActorsInRoom(actor, vel);
			
			//done!
			return MessageType.personal(actor, "LOOK", att[0], msg);
		}
		
		/** SAY -----------------------------------------------------------	|*/
		else if (m_say.check(att))
		{
			msg[0] = (actor.getName() 
					+ " says, \"" + collapse(att, 1, att.length) 
					+ "\"").trim();
			
			//done!
			return MessageType.roomwide(actor, "SAY", actor.getXYZ(), "WORLD", msg);
		}
		
		/** TAKE ----------------------------------------------------------	|*/
		else if (m_take.check(att))
		{
			if (!m_take.checkForMod(att) && att.length >= 2)
			{
				msg[0] = "detected 'take [" + collapse(att, 1, att.length) + "]'.";
				return msg;
			}
			else if (m_take.checkForMod(att))
			{
				String[] temp = collapse(att, 1, att.length, m_take.getMod());
				
				if (temp.length >= 2 && temp[0].length() > 0 && temp[1].length() > 0)
				{
					msg[0] = "detected 'take [" + temp[0] + "] from [" + temp[1] + "]'.";
					return msg;
				}
			}
			
			return m_take.getError(att[0]);
		}
		
		/** PUT -----------------------------------------------------------	|*/
		else if (m_put.check(att) && att.length >= 2)
		{
			if (m_put.checkForMod(att) && att.length >= 2)
			{
				String[] temp = collapse(att, 1, att.length, m_put.getMod());
				
				if (temp.length >= 2 && temp[0].length() > 0 && temp[1].length() > 0)
				{
					msg[0] = "detected 'put [" + temp[0] + "] in [" + temp[1] + "]'.";
					return msg;
				}
			}
			return m_put.getError(att[0], collapse(att, 1, att.length));
		}
		
		/** DROP ----------------------------------------------------------	|*/
		else if (m_drop.check(att))
		{	
			if (att.length >= 2)
			{
				String t = collapse(att, 1, att.length);
				msg[0] = actor.dropFromInv(t);
				
				
				//msg[0] = "detected 'drop [" + collapse(att, 1, att.length) + "]'.";
				return msg;
			}
			else
			{
				msg = m_drop.getError();
			}
			
			return MessageType.roomwide(actor, "DROP", actor.getXYZ(), "WORLD", msg);
		}
		
		/** ST*ATUS -------------------------------------------------------	|*/
		else if (m_status.check(att))
		{
			msg[0] = actor.printMeter(actor.getHP(), actor.getHP(), actor.getHPMax(), 30);
			
			return MessageType.personal(actor, "STATUS", actor.toString(), msg);
		}
		
		/** TAC*TICAL -----------------------------------------------------	|*/
		else if (m_tactical.check(att))
		{
			msg = m_tactical.getActorsTacticalInRoom(actor);
			return MessageType.personal(actor, "TACTICAL", actor.getXYZ(), msg);
		}
		
		/** I*NVENTORY ----------------------------------------------------	|*/
		else if (m_inventory.check(att))
		{
			msg = actor.getInventory();
			return MessageType.personal(actor, "INVENTORY", actor.toString(), msg);
		}
		
		/** ADMIN STUFF ---------------------------------------------------	|*/
		else if (m_admin.check(att))
		{
			/** setRange */
			if (m_admin.getMeaning(att[0]).equals("setVis"))
			{
				try
				{
					actor.setVisionRange(Integer.parseInt(att[1]));
					msg[0] = "Set visibility range to " + att[1];
					
					return MessageType.personal(actor, "@SETVIS", actor.toString(), msg);
				}
				
				catch(Exception e)
				{	
					//e.printStackTrace();
					msg[0] = "Usage: " + att[0] + " [number]";
					return MessageType.personal(actor, "@SETVIS", actor.toString(), msg);
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
					
					Entity entity = new Entity(name, TYPE_OBJECT, hp, hp, actor);
					
					if (actor.addToInventory(entity))
					{
						msg[0] = "Gave " + name + " to " + actor.toString();
						return MessageType.personal(actor, "@GIVE", actor.toString(), msg);
					}
					else
					{
						msg[0] = "Couldn't give " + name + " to " + actor.toString();
						return MessageType.personal(actor, "@GIVE", actor.toString(), msg);
					}
				}
				
				catch(Exception e)
				{
					e.printStackTrace();
					msg[0] = "Usage: " + att[0] + " [itemName] [maxHp]";
					return MessageType.personal(actor, "@GIVE", actor.toString(), msg);
				}
			}
			/** deleteItem */
			/*else if (m_admin.getMeaning(att[0]).equals("del"))
			{
				try
				{
					String name = collapse(att, 1, att.length);
					
					if (actor.delFromInventory(name))
					{
						msg[0] = "Deleted " + name + " from " + actor.toString();
						return MessageType.personal(actor, "@DEL", actor.toString(), msg);
					}
					else
					{
						msg[0] = "Couldn't delete " + name + " from " + actor.toString();
						return MessageType.personal(actor, "@DEL", actor.toString(), msg);
					}
				}
				catch(Exception e)
				{
					
					msg[0] = "Usage: " + att[0] + " [itemName]";
					return MessageType.personal(actor, "@DEL", actor.toString(), msg);
				}
			}*/
			/** noclip */
			else if (m_admin.getMeaning(att[0]).equals("noclip"))
			{
				try
				{
					actor.ignoresCollision(Boolean.parseBoolean(att[1]));

					msg[0] = "noclip = " + att[1];
					return MessageType.personal(actor, "@NOCLIP", actor.toString(), msg);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					msg[0] = "Usage: " + att[0] + " [true/false]";
					return MessageType.personal(actor, "@NOCLIP", actor.toString(), msg);
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
					return MessageType.personal(actor, "@HURT", actor.toString(), msg);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					msg[0] = "Usage: " + att[0] + " [value]";
					return MessageType.personal(actor, "@HURT", actor.toString(), msg);
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
					return MessageType.personal(actor, "@HEAL", actor.toString(), msg);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					msg[0] = "Usage: " + att[0] + " [value]";
					return MessageType.personal(actor, "@HEAL", actor.toString(), msg);
				}
			}
			/** debug */
			else if (m_admin.getMeaning(att[0]).equals("debug"))
			{
				String[] t1 = Entity.debug();
				String[] t2 = Actor.debug();
				
				msg = new String[t1.length + t2.length + 1];
				System.arraycopy(t1, 0, msg, 0, t1.length);
				System.arraycopy(t2, 0, msg, t1.length, t2.length);
				
				msg[msg.length - 1] = "----------------";
				return MessageType.personal(actor, "@DEBUG", actor.toString(), msg);
			}
			/** test - spawns four actors at the caller's coordinates */
			else if (m_admin.getMeaning(att[0]).equals("test"))
			{
				for (int i = 0; i < 4; i++)
				{
					new Actor(actor.getX(), actor.getY(), actor.getZ(), "npc_test_" + i, TYPE_ALIVE, 30, 30);
				}
				msg[0] = "created some dummy npcs at " + actor.getXYZ();
				return MessageType.personal(actor, "@TEST", actor.toString(), msg);
			}
			else if (m_admin.getMeaning(att[0]).equals("stress"))
			{
				Logger.start();
				for (int i = 0; i < 999; i++)
				{
					if (i % 100 == 0)
					{
						System.out.println(i);
						Logger.stop(true);
						Logger.start();
					}
					new Actor(actor.getX(),
							actor.getY(),
							actor.getZ(),
							"npc_test_" + i,
							TYPE_ALIVE,
							30,
							30);
					
				}
				return MessageType.personal(actor, "@STRESSTEST", actor.toString(), msg);
			}
			else if (m_admin.getMeaning(att[0]).equals("saveAll"))
			{
				Save.all();
				Save.state();
				msg[0] = "Saved!";
				return MessageType.personal(actor, "@SAVEALL", "WORLD", msg);
			}
			/** if invalid */
			else
			{
				msg[0] = "'" + att[0] + "' doesn't seem to be a valid command.";
				return MessageType.personal(actor, "@STRESSTEST", actor.toString(), msg);
			}
		}
		
		/** QUIT ----------------------------------------------------------	|
		 * Short: quit
		 */
		else if (m_quit.check(att))
		{
			msg[0] = "Logging out...";
			return MessageType.personal(actor, "@QUIT", actor.toString(), msg);
		}
		
		/** NO MATCH ------------------------------------------------------	|
		 */
		else
		{
			msg[0] = input + " for entity " + actor + " isn't a valid command.";
			return MessageType.personal(actor, "ERROR", collapse(att, 1, att.length), msg);
		}
	}
}
