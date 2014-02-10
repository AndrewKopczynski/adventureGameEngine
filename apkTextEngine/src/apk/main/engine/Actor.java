package apk.main.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Represents an entity in the world (a world entity).
 * <p>
 * A world entity has:
 * <ul>
 * <li>A ID (int, contained in list of int)
 * <li>A X coordinate (int)
 * <li>A Y coordinate (int)
 * <li>A Z coordinate (int)
 * <li>Maximum HP (int)
 * <li>Current HP (int)
 * <li>An inventory (list of entities)
 * </ul>
 * */
public class Actor extends Entity
{
	/**List of ID/Actors */
	private static Map<Integer, Actor> m_actors = new HashMap<Integer, Actor>();
	
	private int m_x;
	private int m_y;
	private int m_z;
	
	/** Creates an entity, and sets their max/current HP.
	 * 
	 * @param x X (W(-)  <->  (+)E) coordinate
	 * @param y Y (N(-)  <->  (+)S) coordinate
	 * @param z Z (D(-)  <->  (+)U) coordinates
	 * @param name Name of entity
	 * @param hpMax Value the entity has at maxmimum health
	 * @param hp Current HP, always less than maxmium HP
	 */
	public Actor(int x, int y, int z, String name, int hpMax, int hp)
	{
		m_id = ID.add();
		m_actors.put(m_id, this);
		
		m_x = x;
		m_y = y;
		m_z = z;
		
		m_name = name;
		m_hpMax = hpMax;
		m_hp = hp;
		
		writeSave();
	}
	
	public Actor(String filePath)
	{	
		try
		{
			Logger.log("Creating new entity from '" + filePath + "'...");
			String[] entElements = {"entity", "health", "inventory"};
			XMLReader invXML = new XMLReader(filePath, entElements);
			
			m_id = Integer.parseInt(invXML.getAttribute(entElements[0], 0, "id"));
			ID.add(m_id);
			
			m_name = invXML.getAttribute("entity", 0, "name");
			
			System.out.println("Adding '" + m_name + "' to list with ID '" + m_id + "'...");
			
			
			String temp = invXML.getAttribute("entity", 0, "coords");
			String split[] = temp.split(",");
			
			if (!split[0].equals("inInventory"))
			{
				m_x = Integer.parseInt(split[0]);
				m_y = Integer.parseInt(split[1]);
				m_z = Integer.parseInt(split[2]);
			}
			else
			{
				String err = "CRTICAL! ENTITY " + toString() + "TRIED TO LOAD"
						+ " AN ENTITY WITH NO COORDINATES AS A WORLDENTITY!";
				System.out.println(err);
				Logger.log(err);
			}
			
			m_hpMax = Integer.parseInt(invXML.getAttribute("health", 0, "hpMax"));
			m_hp = Integer.parseInt(invXML.getAttribute("health", 0, "hp"));
			
			System.out.println("printing inventory...");
			String ents[] = invXML.getChildren("inventory", 0);
			for (int a = 1; a < ents.length; a += 2)
			{
				System.out.println("found ent: " + ents[a]);
				if (!ents[a].equals(toString()))
				{
					addToInventory(new Entity("ent/" + ents[a] + ".xml"));
				}
				else
				{
					String err = "!CRTICAL! ENTITY " + toString() + "TRIED TO LOAD ITSELF!"
							+ " CHECK ENTITY FILE " + getFilePath() + "!";
					System.out.println(err);
					Logger.log(err);
				}
			}
			writeSave();
		}
		catch(IDConflictException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	@Override
	public void writeSave()
	{
		//create file (eg. ent/player[0].xml)
		XMLWriter w = new XMLWriter(getFilePath());
		
		//fetch id, name, and coords
		String[] a = {"id", "name", "coords"};
		String[] b = {"" + getId(), getName(), getXYZ()};
		
		//open root
		w.writeOpenTag("entity", a, b);
		
		//fetch hp
		String[] c = {"hpMax", "hp"};
		String[] d = {"" + getHPMax(), "" + getHP()};
		
		//write health
		w.writeTag("health", c, d);
		
		//open inventory, write items
		w.writeOpenTag("inventory");
		for (int i = 0; i < m_inv.size(); i++)
		{
			w.writeTextContent("entity", m_inv.get(i).toString());
		}
		//close inventory
		w.writeCloseTag("inventory");
		
		//close root
		w.writeCloseTag("entity");
		
		//close file writer
		w.close();
		System.out.println("Wrote " + toString() +"'s inventory to " + getFilePath());
	}
	
	public String move(String dir)
	{
		if (dir == null)
		{
			return "Go where?";
		}
		//boolean isDisplaced = false;
		boolean cond1; // contains direction you want to go
		boolean cond2; // contains ? any room modifier
		
		int n = 0;
		int e = 0;
		int s = 0;
		int w = 0;
		int u = 0;
		int d = 0;
		
		String north = "n";
		String east = "e";
		String south = "s";
		String west = "w";
		String northeast = "ne";
		String northwest = "nw";
		String southeast = "se";
		String southwest = "sw";
		String up = "u";
		String down = "d";
		
		String oppDir;
		String any = "?";
		
		if (dir.equals(north))
		{
			n = 1;
			oppDir = south;
		} 
		else if (dir.equals(east))
		{
			e = 1;
			oppDir = west;
		} 
		else if (dir.equals(south))
		{
			s = 1;
			oppDir = north;
		} 
		else if (dir.equals(west))
		{
			w = 1;
			oppDir = east;
		} 
		else if (dir.equals(northeast))
		{
			n = 1;
			e = 1;
			oppDir = southwest;
		} 
		else if (dir.equals(southeast))
		{
			s = 1;
			e = 1;
			oppDir = northwest;
		} 
		else if (dir.equals(southwest))
		{
			s = 1;
			w = 1;
			oppDir = northeast;
		} 
		else if (dir.equals(northwest))
		{
			n = 1;
			w = 1;
			oppDir = southeast;
		} 
		else if (dir.equals(up))
		{
			u = 1;
			oppDir = down;
		}
		else if (dir.equals(down))
		{
			d = 1;
			oppDir = up;
		}
		else 
		{
			System.out.println(toString() + " tried to go invalid direction '" + dir + "'.");
			return "'" + dir + "' is not a valid direction.";
		}
		
		int xDisp = getX() + (e - w);
		int yDisp = getY() + (s - n);
		int zDisp = getZ() + (u - d);
		
		/** Boundary check to avoid bad checks in array. */
		if (getNorth(n) < 0 
			|| getEast(e) > (World.mapSize - 1) 
			|| getSouth(s) > (World.mapSize - 1) 
			|| getWest(w) < 0 
			|| getUp(u) > (World.HEIGHT_LIMIT - 1) 
			|| getDown(d) < 0)
		{
			System.out.println(toString() + " was blocked from going out of bounds of map.");
			return "That doesn't go anywhere.";
		}
		
		/** Checks if the room has the correct exits to move in the requested direction,
		 * and then checks if the connecting room also has that exit.
		 * 
		 *  Returns true if the room contains a valid exit for that way OR contains an
		 *  'any' or ambiguous exit (symbolized by a '?' mark)
		 *  
		 *  TODO: overhaul into a point map
		 */
		if (!m_ignoresCollision && World.isMapRoom(World.roomArray, xDisp, yDisp, zDisp)) 
		{
			cond1 = World.roomArray[getX()][getY()][getZ()].getRoomExits().contains(dir)
					|| World.roomArray[getX()][getY()][getZ()].getRoomExits().contains(any);
			cond2 = World.roomArray[xDisp][yDisp][zDisp].getRoomExits().contains(oppDir)
					|| World.roomArray[xDisp][yDisp][zDisp].getRoomExits().contains(any);
		
		} 
		else if (m_ignoresCollision)
		{
			cond1 = true;
			cond2 = true;
		}	
		else
		{
			System.out.println(toString() + " tried to a room that didn't have the appropriate opposite exit.");
			return "I can't seem to go that way.";
		}
		
		if (cond1 && cond2) 
		{
			goNorth(n);
			goEast(e);
			goSouth(s);
			goWest(w);
			goUp(u);
			goDown(d);
		} 
		else
		{
			System.out.println(toString() + " tried to go through an exit that doesn't exist.");
			return "I can't go that way.";
		}
		
		//isDisplaced = true;
		
		/* For some reason when I first programmed the line of code below,
		 * I used player.getX(), player.getY(), etc instead of just using
		 * getX(), getY() to make it entity inspecific.
		 * 
		 * Probably a good reminder to focus less on the player and more on
		 * getting other entities into the game. */
		
		/* It's me again, I did another dumb thing. I used getX(), getY(), and getZ()
		 * even though it would have been far better to just use the getXYZ() method
		 * I made for this exact purpose.
		 * 
		 * TODO: review code, there's probably a lot of this stufff.
		 */
		System.out.println(toString() + " moved to " + getXYZ());
		
		return "LOC: " + getXYZ();
	}
	
	/** Returns X coordinate of entity.
	 * @return X coord of entity */
	public int getX() 
	{
		return m_x;
	}
	/** Returns Y coordinate of entity.
	 * @return Y coord of entity */
	public int getY() 
	{
		return m_y;
	}
	/** Returns Z coordinate of entity.
	 * @return Z coord of entity */
	public int getZ() 
	{
		return m_z;
	}
	/** Returns XYZ as a string.
	 * @return XYZ in format "0,0,0" */
	public String getXYZ()
	{
		return m_x + "," + m_y + "," + m_z;
	}

	// getDirection
	private int getNorth(int n) 
	{
		return m_y - n;
	}
	private int getEast(int e) 
	{
		return m_x + e;
	}
	private int getSouth(int s) 
	{
		return m_y + s;
	}
	public int getWest(int w) 
	{
		return m_x - w;
	}
	public int getUp(int u)
	{
		return m_z + u;
	}
	public int getDown(int d)
	{
		return m_z - d;
	}
	
	// goDirection
	private void goNorth(int n) 
	{
		m_y -= n;
	}
	private void goEast(int e) 
	{
		m_x += e;
	}
	private void goSouth(int s) 
	{
		m_y += s;
	}
	private void goWest(int w) 
	{
		m_x -= w;
	}
	private void goUp(int u)
	{
		m_z += u;
	}
	private void goDown(int d)
	{
		m_z -= d;
	}
	
	/** Returns a list of actors at specific coordinates.
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return List of actors at those coordinates
	 */
	/*public static List<Actor> getActors(int x, int y, int z)
	{
		List<Actor> temp = new ArrayList<Actor>();
		for (int i = 0; i < ID.size(); i++)
		{
			if (m_actors.get(ID.get(i)) != null
				&& m_actors.get(ID.get(i)).getX() == x
				&& m_actors.get(ID.get(i)).getY() == y
				&& m_actors.get(ID.get(i)).getZ() == z)
			{
				temp.add(m_actors.get(ID.get(i)));
			}
		}
		return temp;
	}*/
	
	public static Actor[] getActors(int x, int y, int z)
	{
		int index = 0;
		Actor[] temp = new Actor[m_actors.size()];
		
		for (int i = 0; i < ID.size(); i++)
		{
			if (m_actors.get(ID.get(i)) != null
				&& m_actors.get(ID.get(i)).getX() == x
				&& m_actors.get(ID.get(i)).getY() == y
				&& m_actors.get(ID.get(i)).getZ() == z)
			{
				temp[index] = m_actors.get(i);
				index++;
			}
		}
		return temp;
	}
	
	/** Get all actors BESIDES the one specified. */
	public static Actor[] getActors(Actor actor, int x, int y, int z)
	{
		int index = 0;
		Actor[] actors = new Actor[0];
		
		for (int i = 0; i < ID.size(); i++)
		{
			if (m_actors.get(ID.get(i)) != null
				&& m_actors.get(ID.get(i)) != actor
				&& m_actors.get(ID.get(i)).getX() == x
				&& m_actors.get(ID.get(i)).getY() == y
				&& m_actors.get(ID.get(i)).getZ() == z)
			{
				Actor[] temp = new Actor[actors.length + 1];
				temp[index] = m_actors.get(i);
				//System.arraycopy(actor, 0, temp, 0, temp.length);
				actors = temp;
				
				/*msg = Render_ASCII.renderMap(actor.getX(), actor.getY(), actor.getZ());
				
				String[] arr = new String[msg.length + 1];
				System.arraycopy(msg, 0, arr, 0, msg.length);*/
				
				System.out.println("index: " + index);
				System.out.println(m_actors.get(i));
				index++;
			}
		}
		System.out.println("arr: " + actors.length);
		
		return actors;
	}
	
	public static Actor getById(int id)
	{
		if (m_actors.containsKey(id))
			return m_actors.get(id);
		else
			return null;
	}
	
	public static boolean existsById(int id)
	{
		if (m_actors.containsKey(id))
			return true;
		else
			return false;
	}
	
	public static Entity getByName(String name)
	{
		for (int i = 0; i < m_actors.size(); i++)
		{
			System.out.println("checking " + name + " against " + m_actors.get(i).getName() + " (" + m_actors.get(i).toString() + ")");
			if (m_actors.get(i).getName().equalsIgnoreCase(name))
			{
				return m_actors.get(i);
			}
		}
		return null;
	}
	
	public static boolean existsByName(String name)
	{
		for (int i = 0; i < m_actors.size(); i++)
		{
			if (m_actors.get(i).toString().equalsIgnoreCase(name))
				return true;
		}
		return false;
	}
	
	/** debug list print */
	public static void debug()
	{
		for (int i = 0; i < m_actors.size(); i++)
		{
			System.out.println(m_actors.get(i));
		}
	}
	
	/** Removes all references to this entity so that
	 * java's garbage collector will clean it up whenever. */
	@Override
	public void kill()
	{
		try //TODO: make the entity drop all its stuff too
		{
			File file = new File("ent/" + toString() + ".xml");
			
			if (file.delete())
				Logger.log("Deleted: " + getFilePath());
			else
			{
				System.out.println("!CRTICAL! ENTITY FILE NOT DELETED!");
				throw new IllegalArgumentException("Could not delete entity file " + getFilePath());
			}
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		m_actors.remove(m_id); //no references, should clean up
		m_name = null;
		m_inv = null;
	}
	
}




