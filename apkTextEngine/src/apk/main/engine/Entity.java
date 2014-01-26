package apk.main.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import apk.main.server.Server;

/** Represents an entity.
 * <p>
 * An entity has:
 * <ul>
 * <li>A ID
 * <li>A X coordinate (int)
 * <li>A Y coordinate (int)
 * <li>A Z coordinate (int)
 * <li>Maximum HP
 * <li>Current HP
 * <li>An inventory (Inventory)
 * </ul>
 * */
public class Entity
{	
	/** Grand list of IDs */
	private static List<Integer> m_idList = new ArrayList<Integer>();
	
	/** Inventory of entity. */
	private List<Entity> m_inv = new ArrayList<Entity>();
	
	/** Unique ID */
	private int m_id;
	
	/** Player's X (across the screen) coordinate. */
	private int m_x;
	/** Player's Y (vertically on screen) coordinate. */
	private int m_y;
	/** Player's Z (game world height) coordinate. */
	private int m_z;
	
	/** Name of entity. */
	private String m_name;
	/** Maxmimum health of entity. */
	private int m_hpMax;
	/** Current health of entity. */
	private int m_hp;
	
	/** Ignores collision checks, may go to any tile within array range.
	 * <p>
	 * <ul>
	 * <li>False: Normal checks (cannot go through walls, must have appropriate exits)
	 * <li>True: 'Noclip', ignores collision checks, can 'fly'/pass through walls.
	 * </ul>
	 */
	private boolean m_ignoresCollision = false;
	
	/** Only used for null entities (for Java garbage collection).
	 */
	public Entity()
	{
		
	}
	
	public Entity(String name, int hpMax)
	{
		m_id = getNextId();
		addId(m_id);
		
		m_name = name;
		m_hpMax = hpMax;
		m_hp = hpMax;
		
		Logger.log("Created new ent '" + toString() +  "' @ " + "inventory?");
		
		writeSave();
	}
	
	/** Creates an entity, and sets their max/current HP.
	 * 
	 * @param x X (W(-)  <->  (+)E) coordinate
	 * @param y Y (N(-)  <->  (+)S) coordinate
	 * @param z Z (D(-)  <->  (+)U) coordinates
	 * @param name Name of entity
	 * @hpMax Value the entity has at maxmimum health
	 * @hp Current HP, always less than maxmium HP
	 */
	public Entity(int x, int y, int z, String name, int hpMax, int hp)
	{
		m_id = getNextId();
		addId(m_id);
		
		m_x = x;
		m_y = y;
		m_z = z;
		
		m_name = name;
		m_hpMax = hpMax;
		m_hp = hp;
		
		Logger.log("Created new ent '" + toString() +  "' @ " + getXYZ());
		
		writeSave();
	}
	
	/** Creates an entity.
	 * 
	 * @param x X (W(-)  <->  (+)E) coordinate
	 * @param y Y (N(-)  <->  (+)S) coordinate
	 * @param z Z (D(-)  <->  (+)U) coordinate
	 * @param name Name of entity
	 * @hpMax Value the entity has at maxmimum health
	 */
	public Entity(int x, int y, int z, String name, int hpMax)
	{
		m_id = getNextId();
		addId(m_id);
		
		m_x = x;
		m_y = y;
		m_z = z;
		
		m_name = name;
		m_hpMax = hpMax;
		m_hp = hpMax;
		
		Logger.log("Created new ent '" + toString() +  "' @ " + getXYZ());
		
		writeSave();
	}
	
	/** Creates a new entity from a file.
	 * <p>
	 * Usually used in loading entities from a save, but can also be used for template entities.
	 * 
	 * @param filePath exampleEntity[id].xml
	 */
	public Entity(String filePath)
	{	
		String[] entElements = {"entity", "health", "inventory"};
		XMLReader invXML = new XMLReader(filePath, entElements);
		
		m_id = Integer.parseInt(invXML.getAttribute(entElements[0], 0, "id"));
		m_idList.add(m_id);
		
		m_name = invXML.getAttribute("entity", 0, "name");
		
		String temp = invXML.getAttribute("entity", 0, "coords");
		String split[] = temp.split(",");
		m_x = Integer.parseInt(split[0]);
		m_y = Integer.parseInt(split[1]);
		m_z = Integer.parseInt(split[2]);
		
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
				//throws a stackoverflow here probaby TODO: test
			}
		}
		
		writeSave();
	}
	
	/** Write to save file. */
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
	
	public boolean move(String dir)
	{
		boolean isDisplaced = false;
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
			System.out.println(dir + " is not a valid direction.");
			return isDisplaced;
		}
		
		int xDisp = getX() + (e - w);
		int yDisp = getY() + (s - n);
		int zDisp = getZ() + (u - d);
		
		/** Boundary check to avoid bad checks in array. */
		if (getNorth(n) < 0 
			|| getEast(e) > (Map.mapSize - 1) 
			|| getSouth(s) > (Map.mapSize - 1) 
			|| getWest(w) < 0 
			|| getUp(u) > (Map.HEIGHT_LIMIT - 1) 
			|| getDown(d) < 0)
		{
			System.out.println("You don't think that exit leads anywhere.");
			return isDisplaced;
		}
		
		/** Checks if the room has the correct exits to move in the requested direction,
		 * and then checks if the connecting room also has that exit.
		 * 
		 *  Returns true if the room contains a valid exit for that way OR contains an
		 *  'any' or ambiguous exit (symbolized by a '?' mark)
		 *  
		 *  TODO: overhaul into a point map. 
		 *  NEW: noclip / ignores collision. */
		if (!m_ignoresCollision && Map.isMapRoom(Map.roomArray, xDisp, yDisp, zDisp)) 
		{
			cond1 = Map.roomArray[getX()][getY()][getZ()].getRoomExits().contains(dir)
					|| Map.roomArray[getX()][getY()][getZ()].getRoomExits().contains(any);
			cond2 = Map.roomArray[xDisp][yDisp][zDisp].getRoomExits().contains(oppDir)
					|| Map.roomArray[xDisp][yDisp][zDisp].getRoomExits().contains(any);
		
		} 
		else if (m_ignoresCollision)
		{
			cond1 = true;
			cond2 = true;
		}	
		else
		{
			System.out.println("There's no exit that way.");
			return isDisplaced;
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
			System.out.println("There isn't an exit that way.");
			return isDisplaced;
		}
		
		isDisplaced = true;
		
		/* For some reason when I first programmed the line of code below,
		 * I used player.getX(), player.getY(), etc instead of just using
		 * getX(), getY() to make it entity inspecific.
		 * 
		 * Probably a good reminder to focus less on the player and more on
		 * getting other entities into the game. */
		System.out.println("LOC: " + getX() + ", " + getY() + ", " + getZ());
		
		return isDisplaced;
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
	
	public int getId()
	{
		return m_id;
	}
	
	public String getName()
	{
		return m_name;
	}
	
	public String getFilePath() 
	{
		//TODO: review inventory -> entity file changes
		//return "ent/" + "inv_" + toString();
		return "ent/" + toString();
	}
	
	public void ignoresCollision(boolean ignores)
	{
		m_ignoresCollision = ignores;
	}
	
	public int getHPMax()
	{
		return m_hpMax;
	}
	public int getHP()
	{
		return m_hp;
	}

	public boolean addToInventory(Entity entity)
	{
		return m_inv.add(entity);
	}
	
	public boolean delFromInventory(Entity entity)
	{
		return m_inv.remove(entity);
	}
	
	public boolean delFromInventory(String entityName)
	{
		System.out.println("doing entity removal from inventory by name"
				+ " for '" + entityName + "'");
		for (int i = 0; i < m_inv.size(); i++)
		{
			if (m_inv.get(i).getName().equals(entityName))
			{
				System.out.println("found item!");
				m_inv.get(i).remove();
				m_inv.remove(i);
				return true;
			}	
			System.out.println("checked " + m_inv.get(i).getName());
		}
		System.out.println("removal failed");
		return false;
	}
	
	/** Adds an ID to the list of IDs TODO: recycle IDs
	 * 
	 * @param id ID to add
	 * @return true if added, false if not added.
	 */
	private static boolean addId(int id)
	{
		if (!m_idList.contains(id))
		{
			return m_idList.add(id);
		}
		else
		{
			return false;
		}
	}
	/** Removes an ID from the ID list TODO: faster ID recycle
	 * 
	 * @param id ID to remove
	 * @return true if removed, false if not removed
	 */
	private static boolean delId(int id)
	{
		for (int i = 0; i < m_idList.size() - 1; i++)
		{
			if (m_idList.get(i) == id)
			{
				m_idList.remove(i);
				return true;
			}	
		}
		return false;
	}
	/** Gets the next free ID
	 * TODO: make second array to track free IDs / garbage ID recycler
	 * @return
	 */
	private static int getNextId()
	{
		int i = 0;
		while(m_idList.contains(i))
		{
			i++;
		}
		return i;
	}
	
	/** Sets this entity to null so it can be collected by
	 * java's garbage collector (whenever it feels like doing that, though).
	 */
	private void remove()
	{
		// first we need to delete the entity file TODO: make the entity drop all its stuff too
		try
		{
			File file = new File("ent/" + toString() + ".xml");
			
			if (file.delete())
			{
				System.out.println("entity file deleted!");
			}
			else
			{
				System.out.println("!CRTICAL! ENTITY FILE NOT DELETED!");
			}
		}
		catch (Exception e)
		{
			
		}
		
		// then we tell the server to remove the entity, RIP
		delId(m_id);
		Server.remove(this);
	}
	
	public String toString()
	{
		return m_name + "[" + m_id + "]";
	}
	
}
