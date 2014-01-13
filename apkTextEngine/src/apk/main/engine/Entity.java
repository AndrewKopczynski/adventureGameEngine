package apk.main.engine;

import java.util.List;
import apk.main.server.Server;

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
	/** List of Ids */
	private static List<Integer> eIdList;
	
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
	
	/** TODO: inventories. Placeholder. */
	private String m_invPath;
	/** Inventory of entity. */
	private Inventory m_inv;

	/** TODO: physics
	 * Probably going to have entities have a
	 * physics object or something instead of
	 * tracking velocity in entities. */
	@SuppressWarnings("unused")
	private double m_velocity;
	
	/** Ignores collision checks, may go to any tile within array range.
	 * <p>
	 * <ul>
	 * <li>On: Normal checks (cannot go through walls, must have appropriate exits)
	 * <li>Off: 'Noclip', ignores collision checks, can fly and go through walls.
	 * </ul>
	 */
	private boolean m_ignoresCollision = false;
	
	/** Creates an entity.
	 * 
	 * @param x X (W(-)  <->  (+)E) coordinate
	 * @param y Y (N(-)  <->  (+)S) coordinate
	 * @param z Z (D(-)  <->  (+)U) coordinates
	 * @param name Name of entity
	 * @param PhysVelocity **TODO: Implement physics Velocity of entity
	 * @param hasPlayerControl Unused TODO: Is this needed?
	 */
	public Entity(int x, int y, int z, String name, double physVelocity)
	{
		m_x = x;
		m_y = y;
		m_z = z;
		
		m_Id = (eIdList.size() + 1);
		m_name = name;
		m_invPath = createInvPath();
		m_inv = new Inventory(m_Id, m_name, m_invPath);
		
		Logger.log("ENTTY: Created new entity @ " + m_x + "," + m_y + "," + m_z + " - " + m_Id + ", " + m_name);
	}
	
	/** Creates an entity, with default velocity (0).
	 * 
	 * @param x X (W(-)  <->  (+)E) coordinate
	 * @param y Y (N(-)  <->  (+)S) coordinate
	 * @param z Z (D(-)  <->  (+)U) coordinates
	 * @param PhysVelocity **TODO: Implement physics Velocity of entity
	 * @param hasPlayerControl Unused TODO: Is this needed?
	 */
	public Entity(int x, int y, int z, String name)
	{
		m_x = x;
		m_y = y;
		m_z = z;
		m_Id = 0; // TODO later, lazy
		m_name = name;
		m_invPath = createInvPath();
		m_inv = new Inventory(m_Id, m_name, m_invPath);
		
		Logger.log("Created new entity @ " + x + "," + y + "," + z + " - " + m_Id + ", " + m_name);
	}
	
	public boolean move(String dir)
	{
		// found that logging every time graphics were fetched
		// was really slow, runs much faster now
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
		
		else {
			System.out.println("There isn't an exit that way.");
			return isDisplaced;
		}
		
		isDisplaced = true;
		
		//gross!
		//Client.map.renderMap();
		//Render_ASCII.renderMap();
		
		//System.out.println("Exits: " + Map.roomArray[m_x][m_y][m_z].getRoomExits());
		System.out.println("LOC: " + Server.player.getX() 
				+ ", " + Server.player.getY()
				+ ", " + Server.player.getZ());
		Logger.log(m_Id + ", " + m_name + " moved " + dir);
		
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
	
	public void ignoresCollision(boolean ignores)
	{
		m_ignoresCollision = ignores;
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
	
	private String createInvPath() 
	{
		return "ent/" + "inv_" + m_Id + "_" + m_name;
	}
	
	public Inventory getInventory()
	{
		return m_inv;
	}
}
