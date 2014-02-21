package apk.main.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import apk.parser.reference.ActorIntializationException;
import apk.parser.reference.IDConflictException;

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
	
	/** Creates an actor, and sets their max/current HP.
	 * 
	 * @param x X (W(-)  <->  (+)E) coordinate
	 * @param y Y (N(-)  <->  (+)S) coordinate
	 * @param z Z (D(-)  <->  (+)U) coordinates
	 * @param name Name of entity
	 * @param hpMax Value the entity has at maximum health
	 * @param hp Current HP, always less than maximum HP
	 */
	public Actor(int x, int y, int z, String name, int type, int hpMax, int hp)
	{
		System.out.println("------------------");
		System.out.println("CREATING NEW ACTOR");
		System.out.println("------------------");
		
		m_id = ID.add();
		m_actors.put(m_id, this);
		
		m_x = x;
		m_y = y;
		m_z = z;
		
		m_name = name;
		m_type = type;
		
		m_hpMax = hpMax;
		m_hp = hp;
		
		//writeSave();
	}
	
	public Actor(int x, int y, int z, Entity entity)
	{
		System.out.println("--------------------------");
		System.out.println("CREATING ACTOR FROM ENTITY");
		System.out.println("--------------------------");
		
		//TODO can i just cast this stuff or no
		m_id = entity.getId();
		m_actors.put(m_id, this);
		
		m_x = x;
		m_y = y;
		m_z = z;
		
		m_name = entity.getName();
		m_type = entity.getType();
		
		m_hpMax = entity.getHPMax();
		m_hp = entity.getHP();
		
		//writeSave();
	}
	
	public Actor(String filePath) throws IDConflictException, FileNotFoundException, ActorIntializationException
	{	
		System.out.println("-----------------------");
		System.out.println("LOADING ACTOR FROM FILE");
		System.out.println("-----------------------");
		
			Logger.log("Creating new entity from '" + filePath + "'...");
			String[] entElements = {"entity", "health", "inventory"};
			XMLReader invXML = new XMLReader(filePath, entElements);
			
			m_id = Integer.parseInt(invXML.getAttribute("entity", 0, "id"));
			ID.add(m_id);
			
			m_actors.put(m_id, this); //stop forgetting to do this aaaaaa
			
			m_name = invXML.getAttribute("entity", 0, "name");
			m_type = Integer.parseInt(invXML.getAttribute("entity", 0, "type"));
			
			System.out.println("Adding '" + m_name + "' to list with ID '" + m_id + "'...");
			
			String temp = invXML.getAttribute("entity", 0, "coords");
			String split[] = temp.split(",");
			
			if (!split[0].equals("inInventoryOf"))
			{
				m_x = Integer.parseInt(split[0]);
				m_y = Integer.parseInt(split[1]);
				m_z = Integer.parseInt(split[2]);
			}
			else
			{
				String err = "CRTICAL! ENTITY " + toString() + "TRIED TO LOAD" + " AN ACTOR WITH NO COORDINATES AS A WORLDENTITY!";
				System.out.println(err);
				Logger.log(err);
				
				throw new ActorIntializationException();
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
					System.out.println("added to inventory: " + addToInventory(new Entity("ent/" + ents[a] + ".xml")));
				}
				else
				{
					String err = "!CRTICAL! ENTITY " + toString() + "TRIED TO LOAD ITSELF!" + " CHECK ENTITY FILE " + getFilePath() + "!";
					System.out.println(err);
					Logger.log(err);
					
					throw new ActorIntializationException();
				}
			}
			System.out.println("--------------------------------");
			System.out.println("FINISHED LOADING ACTOR FROM FILE");
			System.out.println("--------------------------------");
			//writeSave();
	}
	
	@Override
	public String getFilePath() //temp
	{
		return "ent/" + getName();
	}
	
	@Override
	public void writeSave() 
	//INSANELY SLOOOOOOOOW TODO cry
	//100 actors w/ writeSave 	-> 1.5 seconds
	//100 actors w/o writeSave 	-> 2ms
	//750x slower!
	{
		Logger.start();
		
		//create file (eg. ent/player[0].xml)
		XMLWriter w = new XMLWriter(getFilePath());
		
		//fetch id, name, type, and coords
		String[] a = {"id", "name", "type", "coords"};
		String[] b = {"" + getId(), getName(), "" + getType(), getXYZ()};
		
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
		
		System.out.print("[ACTOR] Save write time: ");
		Logger.stop(true);
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
		
		//System.out.println("Bound check: " + isInBounds);
		//System.out.println("Connected check: " + isConnected);
		
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
	
	/** Get all actors BESIDES the one specified.
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return List of actors at those coordinates
	 */
	public static Actor[] getActors(Actor actor, int x, int y, int z)
	{
		Actor[] actors = new Actor[0];
		
		for (int i = 0; i < ID.size(); i++)
		{
			if (actors.length > 200)
			{
				/** If there's an ungodly amount of actors here
				 * then forget fetching the list.
				 */
				return null;
			}
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
			}
		}
		return actors;
	}
	
	/** Gets all actors in room including this one. */
	public static Actor[] getActors(int x, int y, int z)
	{
		Actor[] actors = new Actor[0];
		
		for (int i = 0; i < ID.size(); i++)
		{
			if (actors.length > 200)
			{
				/** If there's an ungodly amount of actors here
				 * then forget fetching the list.
				 */
				return null;
			}
			if (m_actors.get(ID.get(i)) != null
				//&& m_actors.get(ID.get(i)) != actor
				&& m_actors.get(ID.get(i)).getX() == x
				&& m_actors.get(ID.get(i)).getY() == y
				&& m_actors.get(ID.get(i)).getZ() == z)
			{
				Actor[] temp = new Actor[actors.length + 1];
				System.arraycopy(actors, 0, temp, 0, actors.length);
				temp[temp.length - 1] = m_actors.get(ID.get(i));
				
				actors = temp;
			}
		}
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
	
	
	public static Actor getActorsByName(String name) throws NullPointerException
	{
		for (int i = 0; i < ID.size(); i++)
		{
			System.out.println("ID:" + ID.get(i));
			if(m_actors.get(ID.get(i)) != null)
			{
				System.out.println("checking " + name + " against " 
						+ stripID(m_actors.get(ID.get(i)).toString())
						+ " (" + m_actors.get(ID.get(i)).toString() + ")");
				
				if (m_actors.get(ID.get(i)).getName().equalsIgnoreCase(stripID(name))
						//|| m_actors.get(ID.get(i)).toString().equalsIgnoreCase(name)
						) 
				{
					//why compare toString against something that strips toString()???
					return m_actors.get(ID.get(i));
				}
			}
		}
		System.out.println("failed to match " + name);
		return null;
		//throw new NullPointerException();
	}
	
	public static String stripID(String name)
	{
		if (name.contains("["))
			return name.substring(0, name.lastIndexOf("["));
		else
			return name;
	}
	
	/*public static Actor getByFullName(String fullname)
	{
		for (int i = 0; i < ID.size(); i++)
		{
			if (m_actors.get(ID.get(i)) != null)
			{
				System.out.println("checking " + fullname + " against "
						+ m_actors.get(ID.get(i)).toString());
				
				if (m_actors.get(ID.get(i)).toString().equalsIgnoreCase(fullname))
				{
					return m_actors.get(ID.get(i));
				}
			}
		}
		return null;
	}*/
	
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
			if (m_actors.get(ID.get(i)) != null)
				System.out.println(m_actors.get(ID.get(i)).toString());
			//else
				//System.out.println(m_actors.get(ID.get(i)));
		}
	}
	
	public static final void saveAll()
	{
		for (int i = 0; i < ID.size(); i++)
		{
			if (m_actors.get(ID.get(i)) != null)
			{
				m_actors.get(ID.get(i)).writeSave();
			}
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




