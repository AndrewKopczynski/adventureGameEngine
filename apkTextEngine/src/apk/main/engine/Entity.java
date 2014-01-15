package apk.main.engine;

import java.util.ArrayList;
import java.util.List;

/** Represents an entity.
 * <p>
 * An entity has:
 * <ul>
 * <li>A X coordinate (int)
 * <li>A Y coordinate (int)
 * <li>A Z coordinate (int)
 * <li>An inventory (Inventory)
 * <li>A velocity (TODO: physics?)
 * </ul>
 * */
public class Entity
{
	/** List of Ids
	 * 
	 * TODO: Replace this with a global ID list, probably. */
	private static List<Integer> eIdList = new ArrayList<Integer>();
	
	/** Player's X (across the screen) coordinate. */
	private int m_x;
	/** Player's Y (vertically on screen) coordinate. */
	private int m_y;
	/** Player's Z (game world height) coordinate. */
	private int m_z;
	
	/** Unique ID */
	private int m_Id;
	
	/** Name of entity. */
	private String m_name;
	/** Maxmimum health of entity. */
	private int m_hpMax;
	/** Current health of entity. */
	private int m_hp;
	
	/** Inventory of entity. */
	private Inventory m_inv;
	
	/** Ignores collision checks, may go to any tile within array range.
	 * <p>
	 * <ul>
	 * <li>False: Normal checks (cannot go through walls, must have appropriate exits)
	 * <li>True: 'Noclip', ignores collision checks, can 'fly'/pass through walls.
	 * </ul>
	 */
	private boolean m_ignoresCollision = false;
	
	public Entity(String name, int hpMax)
	{
		m_Id = eIdList.size();
		eIdList.add(m_Id);
		
		m_name = name;
		m_hpMax = hpMax;
		m_hp = hpMax;
		
		m_inv = new Inventory(this);
		
		Logger.log("Created new ent '" + toString() +  "' @ " + "inventory?");
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
		m_Id = eIdList.size();
		eIdList.add(m_Id);
		
		m_x = x;
		m_y = y;
		m_z = z;
		
		m_name = name;
		m_hpMax = hpMax;
		m_hp = hp;
		
		m_inv = new Inventory(this);
		
		Logger.log("Created new ent '" + toString() +  "' @ " + getXYZ());
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
		m_Id = eIdList.size();
		eIdList.add(m_Id);
		
		m_x = x;
		m_y = y;
		m_z = z;
		
		m_name = name;
		m_hpMax = hpMax;
		m_hp = hpMax;
		
		m_inv = new Inventory(this);
		
		Logger.log("Created new ent '" + toString() +  "' @ " + getXYZ());
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
	
	public String getFilePath() 
	{
		return "ent/" + "inv_" + toString();
	}
	
	public void ignoresCollision(boolean ignores)
	{
		m_ignoresCollision = ignores;
	}
	
	public Inventory getInventory()
	{
		return m_inv;
	}
	
	public int getHPMax()
	{
		return m_hpMax;
	}
	public int getHP()
	{
		return m_hp;
	}
	
	public String toString()
	{
		return m_name + "[" + m_Id + "]";
	}
}
