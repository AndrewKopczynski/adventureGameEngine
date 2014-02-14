package apk.main.engine;

import java.io.File;
import java.util.HashMap;
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
		
		//System.out.println("created" + getName() + "@" + getXYZ());
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
		//System.out.println("Wrote " + toString() +"'s inventory to " + getFilePath());
	}
	
	/** Refactoring from move because it just did too much in one method. */
	public String[] requestMap(int[] vel, boolean moveToLocation)
	{
		String[] msg = new String[1];
		boolean isInBounds;
		boolean isConnected;
		
		int x = getX();
		int y = getY();
		int z = getZ();
		
		//System.out.println("VEL: " + vel[0] + ", " + vel[1] + ", " + vel[2]);
		if (vel != null)
		{
			x += vel[0];
			y += vel[1];
			z += vel[2];
		}
		//System.out.println("GOING: " + x + ", " + y + ", " + z);
		
		isInBounds = boundryCheck(x, y, z);
		isConnected = (m_ignoresCollision || exitCheck(x, y, z));
		
		System.out.println("Bound check: " + isInBounds);
		System.out.println("Connected check: " + isConnected);
		
		if (isInBounds && isConnected)
		{
			msg = Render_ASCII.renderMap(x, y, z);
			
			if(moveToLocation)
				move(vel);
		}
		else
			msg[0] = "No map recieved.";
		
		return msg;
	}
	
	private boolean boundryCheck(int x, int y, int z)
	{ // Boundary check to avoid bad checks in array.
		/*System.out.println(x >= 0);
		System.out.println(y >= 0);
		System.out.println(z >= 0);
		
		System.out.println(x < World.mapSize - 1);
		System.out.println(y < World.mapSize - 1);
		System.out.println(z < World.HEIGHT_LIMIT - 1);
		System.out.println(World.isMapRoom(World.roomArray, x, y, z));*/
		
		return (x >= 0 && y >= 0 && z >= 0
				&& x < World.mapSize - 1
				&& y < World.mapSize - 1
				&& z < World.HEIGHT_LIMIT - 1
				&& (World.isMapRoom(World.roomArray, x, y, z) || m_ignoresCollision));
	}
	
	private boolean exitCheck(int x, int y, int z)
	{
		return true; //TODO do later
	}
	
	private void move(int[] vel)
	{
		goX(vel[0]);
		goY(vel[1]);
		goZ(vel[2]);
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
	
	// goDirection
	private void goX(int x) 
	{
		m_x += x;
	}
	private void goY(int y) 
	{
		m_y += y;
	}
	private void goZ(int z) 
	{
		m_z += z;
	}
	
	
	/** Returns a list of actors at specific coordinates.
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return List of actors at those coordinates
	 */	
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
				System.arraycopy(actors, 0, temp, 0, actors.length);
				temp[temp.length - 1] = m_actors.get(ID.get(i));
				
				actors = temp;
				
				//System.out.println(m_actors.get(ID.get(i)));
			}
		}
		//System.out.println("arr: " + actors.length);
		
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
	
	public static Actor getByName(String name)
	{
		for (int i = 0; i < ID.size(); i++)
		{
			if(m_actors.get(ID.get(i)) != null)
			{
				System.out.println("checking " + name + " against " + m_actors.get(i).getName() + " (" + m_actors.get(i).toString() + ")");
				if (m_actors.get(i).getName().equalsIgnoreCase(name))
				{
					return m_actors.get(i);
				}
			}
		}
		return null;
	}
	
	public static boolean existsByName(String name)
	{
		for (int i = 0; i < ID.size(); i++)
		{
			if (m_actors.get(ID.get(i)) != null
				&& m_actors.get(ID.get(i)).toString().equalsIgnoreCase(name))
				return true;
		}
		return false;
	}
	
	/** debug list print */
	public static void debug()
	{
		for (int i = 0; i < ID.size(); i++)
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




